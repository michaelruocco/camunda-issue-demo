package uk.co.mruoc.camunda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Data;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

@Builder(toBuilder = true)
@Data
public class ItemRequestParams {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Builder.Default
    private final String businessKey = "999";

    @Builder.Default
    private final Collection<Integer> items = Arrays.asList(1, 2);

    @Builder.Default
    private final String requestedBy = "Joe Bloggs";

    public Map<String, String> toMap() {
        return Map.of(
                "businessKey", businessKey,
                "requestedBy", requestedBy,
                "items", itemsAsJsonArray()
        );
    }

    private String itemsAsJsonArray() {
        try {
            return MAPPER.writeValueAsString(items);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
