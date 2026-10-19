package nl.makan1869.capabilitymesh.mcpregistry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Input(
        String description,
        Boolean isRequired,
        String format,
        String value,
        Boolean isSecret,
        @JsonProperty("default") String defaultValue,
        String placeholder,
        List<String> choices) {}
