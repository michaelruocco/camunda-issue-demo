package uk.co.mruoc.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RejectRequestDelegateTest {

    private final JavaDelegate delegate = new RejectRequestDelegate();

    @Test
    void shouldSetStatusToRejected() throws Exception {
        DelegateExecution execution = mock(DelegateExecution.class);

        delegate.execute(execution);

        verify(execution).setVariable("status", "REJECTED");
    }

}
