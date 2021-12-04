package uk.co.mruoc.camunda;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public FindOpenRequestsDelegate findOpenRequestsDelegate(RuntimeService runtimeService,
                                                             HistoryService historyService) {
        return new FindOpenRequestsDelegate(runtimeService, historyService);
    }

    @Bean
    public CreateRequestDelegate createRequestDelegate(RuntimeService runtimeService) {
        return new CreateRequestDelegate(runtimeService);
    }

    @Bean
    public IssueRequestUpdateDelegate issueRequestUpdateDelegate(RuntimeService runtimeService) {
        return new IssueRequestUpdateDelegate(runtimeService);
    }

    @Bean
    public ApproveRequestDelegate approveRequestDelegate() {
        return new ApproveRequestDelegate();
    }

    @Bean
    public RejectRequestDelegate rejectRequestDelegate() {
        return new RejectRequestDelegate();
    }

    @Bean
    public UpdateRequestDelegate updateRequestDelegate() {
        return new UpdateRequestDelegate();
    }

}
