package nl.makan1869.capabilitymesh.mcpregistry.model;

import java.util.List;

/** Named PackageEntry (not Package) to avoid clashing with java.lang.Package. */
public record PackageEntry(
        String registryType,
        String registryBaseUrl,
        String identifier,
        String version,
        String fileSha256,
        String runtimeHint,
        Transport transport,
        List<Argument> runtimeArguments,
        List<Argument> packageArguments,
        List<KeyValueInput> environmentVariables) {}
