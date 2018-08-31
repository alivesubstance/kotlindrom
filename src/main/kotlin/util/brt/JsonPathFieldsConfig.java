package util.brt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class JsonPathFieldsConfig {
    private final String rootPath;
    private final List<JsonPathKey> fields;

    public JsonPathFieldsConfig(String rootPath, List<JsonPathKey> fields) {
        this.rootPath = rootPath;
        this.fields = fields;
    }


}
