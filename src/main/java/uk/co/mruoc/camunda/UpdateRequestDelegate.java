package uk.co.mruoc.camunda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.spin.SpinList;
import org.camunda.spin.json.SpinJsonNode;

import static org.camunda.spin.Spin.JSON;

@RequiredArgsConstructor
@Slf4j
public class UpdateRequestDelegate implements JavaDelegate {

    private static final String ITEMS = "items";

    @Override
    public void execute(DelegateExecution execution) {
        SpinJsonNode requestForm = (SpinJsonNode) execution.getVariable("requestForm");
        SpinJsonNode updateForm = (SpinJsonNode) execution.getVariable("updateForm");
        log.info("updating open request {} with original request form {} and update form {}",
                execution.getBusinessKey(),
                requestForm,
                updateForm
        );

        SpinList<SpinJsonNode> mergedItems = requestForm.prop(ITEMS).elements();
        SpinList<SpinJsonNode> updatedItems = updateForm.prop(ITEMS).elements();
        mergedItems.addAll(updatedItems);

        requestForm.prop(ITEMS, JSON(mergedItems.toString()));
        requestForm.prop("requestedBy", updateForm.prop("requestedBy"));
        execution.setVariable("requestForm", requestForm);
        log.info("updated original form to {}", requestForm);

        execution.removeVariable("updateForm");
    }

}
