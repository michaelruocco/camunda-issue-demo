package uk.co.mruoc.camunda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

@RequiredArgsConstructor
@Slf4j
public class CreateRequestDelegate implements JavaDelegate {

    private final RuntimeService runtimeService;

    @Override
    public void execute(DelegateExecution execution) {
        log.info("creating new request {}", execution.getBusinessKey());
        runtimeService.createProcessInstanceByKey("process-item-request")
                .businessKey(execution.getBusinessKey())
                .setVariable("requestForm", execution.getVariable("requestForm"))
                .setVariable("status", "OPEN")
                .startAfterActivity("new-item-requested")
                .execute();
    }

}
