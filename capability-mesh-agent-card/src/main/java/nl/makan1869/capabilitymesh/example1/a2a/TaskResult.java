package nl.makan1869.capabilitymesh.example1.a2a;

import java.util.List;

public record TaskResult(
        String id,
        TaskStatus status,
        List<TaskArtifact> artifacts
) {}
