package uk.co.mruoc.camunda;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import static uk.co.mruoc.camunda.Action.Type.CREATE;
import static uk.co.mruoc.camunda.Action.Type.RESTART;
import static uk.co.mruoc.camunda.Action.Type.UPDATE;

@RequiredArgsConstructor
@Slf4j
public class FindOpenRequestsDelegate implements JavaDelegate {

    private static final String PROCESS_ITEM_REQUEST = "process-item-request";

    private final RuntimeService runtimeService;
    private final HistoryService historyService;

    @Override
    public void execute(DelegateExecution execution) {
        Action action = calculateAction(execution);
        if (action.isRestart()) {
            restart(action.getCompletedProcess());
        }
        log.info("setting action {} for request {}", action.getType(), execution.getBusinessKey());
        execution.setVariable("action", action.getType().name());
    }

    private Action calculateAction(DelegateExecution execution) {
        if (otherRunningProcessesExist(execution)) {
            return new Action(UPDATE);
        }
        return findMostRecentlyCompletedHistoricProcess(execution.getBusinessKey())
                .map(process -> new Action(RESTART, process))
                .orElseGet(() -> new Action(CREATE));
    }

    private boolean otherRunningProcessesExist(DelegateExecution execution) {
        String businessKey = execution.getBusinessKey();
        Collection<String> processIds = getRunningProcessIds(businessKey);
        log.info("found running processIds {} for business key {}", processIds, businessKey);
        if (processIds.isEmpty()) {
            return false;
        }
        if (processIds.size() == 1) {
            log.info("comparing {} to running process ids {}", execution.getId(), processIds);
            return !processIds.contains(execution.getId());
        }
        return true;
    }

    private Collection<String> getRunningProcessIds(String businessKey) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .processDefinitionKeyIn("request-item", PROCESS_ITEM_REQUEST)
                .list()
                .stream()
                .map(Execution::getId)
                .collect(Collectors.toList());
    }

    private Optional<HistoricProcessInstance> findMostRecentlyCompletedHistoricProcess(String businessKey) {
        return historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .processDefinitionKey(PROCESS_ITEM_REQUEST)
                .orderByProcessInstanceEndTime()
                .desc()
                .listPage(0, 1)
                .stream()
                .findFirst();
    }

    public void restart(HistoricProcessInstance process) {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(process.getId());
        log.info("attempting to restart process {}", process.getId());
        logFormsFromPreviousCompletedRequests(process.getId());

        runtimeService.restartProcessInstances(process.getProcessDefinitionId())
                .historicProcessInstanceQuery(query)
                .startBeforeActivity("new-item-requested")
                .skipCustomListeners()
                .execute();
        log.info("completed call to restart process");
    }

    private void logFormsFromPreviousCompletedRequests(String processId) {
        Object form = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processId)
                .list()
                .stream()
                .filter(variable -> variable.getName().equals("requestForm"))
                .findFirst()
                .map(HistoricVariableInstance::getValue);
        log.info("historic process had form {}", form);
    }

}
