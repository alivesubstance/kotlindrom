package util.brt;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.regex.Pattern;

public class JsonPathKey {
    private final String fieldName;
    private final String jsonPath;

    public JsonPathKey(String fieldName, String jsonPath) {
        this.fieldName = fieldName;
        this.jsonPath = jsonPath;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getJsonPath() {
        return jsonPath;
    }

}
