package uk.co.mruoc.camunda;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.history.HistoricProcessInstance;

@RequiredArgsConstructor
@Data
public class Action {

    private final Type type;
    private final HistoricProcessInstance completedProcess;

    public Action(Type type) {
        this(type, null);
    }

    public boolean isRestart() {
        return type == Type.RESTART;
    }

    public enum Type {
        UPDATE,
        RESTART,
        CREATE
    }

}
