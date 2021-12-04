package uk.co.mruoc.camunda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@RequiredArgsConstructor
@Slf4j
public class IssueRequestUpdateDelegate implements JavaDelegate {

    private final RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) {
        String businessKey = execution.getBusinessKey();
        log.info("issuing update of type {} for request {}", execution.getVariable("action"), businessKey);
        runtimeService.createMessageCorrelation("update-open-request")
                .processInstanceBusinessKey(businessKey)
                .setVariable("updateForm", execution.getVariable("requestForm"))
                .correlate();
    }

}
