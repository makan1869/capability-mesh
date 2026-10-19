package nl.makan1869.capabilitymesh.mcpregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/** A named header or environment variable entry (spec: KeyValueInput = InputWithVariables + name). */
public record KeyValueInput(
        String name,
        String description,
        Boolean isRequired,
        String format,
        String value,
        Boolean isSecret,
        @JsonProperty("default") String defaultValue,
        String placeholder,
        List<String> choices,
        Map<String, Input> variables) {}
