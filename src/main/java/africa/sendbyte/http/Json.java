package africa.sendbyte.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Internal JSON helper wrapping a configured Jackson {@link ObjectMapper}.
 *
 * <p>Java field names are camelCase; they map to the SendByte API's snake_case
 * wire format automatically via {@link PropertyNamingStrategies#SNAKE_CASE}.
 *
 * <p>This class is part of the SDK's internal machinery and is not intended for
 * direct use by application code.
 */
public final class Json {

    private final ObjectMapper mapper;

    public Json() {
        this.mapper = new ObjectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /** Serialize a value to a JSON string, omitting {@code null} fields. */
    public String write(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize request body", e);
        }
    }

    /** Deserialize a JSON string into the given type. */
    public <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse response body", e);
        }
    }

    public ObjectMapper mapper() {
        return mapper;
    }
}
