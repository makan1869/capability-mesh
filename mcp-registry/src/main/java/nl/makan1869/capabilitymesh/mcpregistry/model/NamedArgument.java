package nl.makan1869.capabilitymesh.mcpregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record NamedArgument(
        String type,
        String name,
        Boolean isRepeated,
        String description,
        Boolean isRequired,
        String format,
        String value,
        Boolean isSecret,
        @JsonProperty("default") String defaultValue,
        String placeholder,
        List<String> choices,
        Map<String, Input> variables)
        implements Argument {}
