package nl.makan1869.capabilitymesh.mcpregistry.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PositionalArgument.class, name = "positional"),
    @JsonSubTypes.Type(value = NamedArgument.class, name = "named")
})
public sealed interface Argument permits PositionalArgument, NamedArgument {}
