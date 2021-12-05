package uk.co.mruoc.camunda;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.text.StringSubstitutor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.util.SocketUtils;
import uk.co.mruoc.Application;
import uk.co.mruoc.camunda.ItemRequestParams.ItemRequestParamsBuilder;
import uk.co.mruoc.rest.client.RestClient;
import uk.co.mruoc.rest.client.SimpleRestClient;
import uk.co.mruoc.rest.client.header.ApplicationJsonHeaders;
import uk.co.mruoc.rest.client.response.Response;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
import uk.org.webcompere.systemstubs.properties.SystemProperties;

import java.time.Duration;
import java.util.Arrays;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static uk.co.mruoc.file.content.ContentLoader.loadContentFromClasspath;
import static uk.org.webcompere.systemstubs.resource.Resources.with;

@ExtendWith(SystemStubsExtension.class)
class ItemRequestIntegrationTest {

    private static final String POST_REQUEST_PATH = "%s/process-definition/key/request-item/start";
    private static final String GET_PROCESS_ID_BY_BUSINESS_KEY = "%s/process-instance?businessKey=%s";
    private static final String GET_VARIABLES_BY_PROCESS_ID = "%s/process-instance/%s/variables?deserializeValues=false";
    private static final String GET_TASK_ID_BY_BUSINESS_KEY = "%s/task?processInstanceBusinessKey=%s";
    private static final String POST_SUBMIT_FORM = "%s/task/%s/submit-form";

    private static final String BUSINESS_KEY = "999";

    private static final String ITEM_REQUEST_TEMPLATE = loadContentFromClasspath("item-request-template.json");

    private final int port = SocketUtils.findAvailableTcpPort();
    private final String host = String.format("http://localhost:%d/engine-rest", port);

    private final ItemRequestParamsBuilder paramsBuilder = ItemRequestParams.builder().businessKey(BUSINESS_KEY);
    private final RestClient client = new SimpleRestClient();

    @Test
    void shouldUpdateOpenAndRestartApprovedRequest() throws Exception {
        with(systemProperties()).execute(() -> {
                startApplication();
                createRequest();
                updateOpenRequest();
                assertRequestUpdated();
                approveRequest();
                recreateRequest();

        });
    }

    private void startApplication() {
        Application.main();
    }

    private void createRequest() {
        ItemRequestParams createParams = paramsBuilder
                .items(Arrays.asList(1, 2))
                .requestedBy("Joe Bloggs")
                .build();
        postItemsRequest(createParams);
    }

    private void updateOpenRequest() {
        ItemRequestParams updateParams = paramsBuilder
                .items(Arrays.asList(3, 4))
                .requestedBy("Jane Doe")
                .build();
        postItemsRequest(updateParams);
    }

    private void assertRequestUpdated() {
        String requestForm = findRequestForm(BUSINESS_KEY);
        assertThatJson(requestForm).isEqualTo("{\"items\":[1,2,3,4],\"requestedBy\":\"Jane Doe\"}");
    }

    private void approveRequest() {
        approveRequest(BUSINESS_KEY);
        assertThat(processIdReturned(BUSINESS_KEY)).isFalse();
    }

    private void recreateRequest() {
        ItemRequestParams recreateParams = paramsBuilder
                .items(Arrays.asList(5, 6))
                .requestedBy("Joe Bloggs")
                .build();
        postItemsRequest(recreateParams);
    }

    private void assertRecreatedFormContainsApprovedFormValues() {
        await().atMost(Duration.ofMillis(2500))
                .pollInterval(Duration.ofMillis(500))
                .until(() -> requestFormContains(BUSINESS_KEY, "[1,2,3,4,5,6]"));
        String recreatedRequestForm = findRequestForm(BUSINESS_KEY);
        assertThatJson(recreatedRequestForm).isEqualTo("{\"items\":[1,2,3,4,5,6],\"requestedBy\":\"Joe Bloggs\"}");
    }

    private SystemProperties systemProperties() {
        return new SystemProperties().set("server.port", Integer.toString(port));
    }

    private void postItemsRequest(ItemRequestParams params) {
        String uri = String.format(POST_REQUEST_PATH, host);
        Response response = client.post(uri, toRequestBody(params), new ApplicationJsonHeaders());
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    private static String toRequestBody(ItemRequestParams params) {
        StringSubstitutor replacer = new StringSubstitutor(params.toMap());
        return replacer.replace(ITEM_REQUEST_TEMPLATE);
    }

    private boolean requestFormContains(String businessKey, String value) {
        String requestForm = findRequestForm(businessKey);
        return requestForm.contains(value);
    }

    private String findRequestForm(String businessKey) {
        String processId = toProcessId(businessKey);
        return toRequestForm(processId);
    }

    private String toProcessId(String businessKey) {
        Response response = lookupProcessId(businessKey);
        DocumentContext context = JsonPath.parse(response.getBody());
        return context.read("$[0].id");
    }

    private String toRequestForm(String processId) {
        String uri = String.format(GET_VARIABLES_BY_PROCESS_ID, host, processId);
        Response response = client.get(uri);

        DocumentContext context = JsonPath.parse(response.getBody());
        return context.read("$.requestForm.value");
    }

    private void approveRequest(String businessKey) {
        String taskId = toTaskId(businessKey);
        submitForm(taskId);
    }

    private String toTaskId(String businessKey) {
        String uri = String.format(GET_TASK_ID_BY_BUSINESS_KEY, host, businessKey);
        Response response = client.get(uri);

        DocumentContext context = JsonPath.parse(response.getBody());
        return context.read("$[0].id");
    }

    private void submitForm(String taskId) {
        String uri = String.format(POST_SUBMIT_FORM, host, taskId);
        client.post(uri, "{\"variables\":{\"approved\":{\"value\":true}}}", new ApplicationJsonHeaders());
    }

    private boolean processIdReturned(String businessKey) {
        Response response = lookupProcessId(businessKey);
        return !response.getBody().equals("[]");
    }

    private Response lookupProcessId(String businessKey) {
        String uri = String.format(GET_PROCESS_ID_BY_BUSINESS_KEY, host, businessKey);
        return client.get(uri);
    }

}
