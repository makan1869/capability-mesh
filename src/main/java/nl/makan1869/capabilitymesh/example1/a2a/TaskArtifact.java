package nl.makan1869.capabilitymesh.example1.a2a;

import java.util.List;

public record TaskArtifact(
        String name,
        List<TaskPart> parts
) {
    public static TaskArtifact of(String name, String text) {
        return new TaskArtifact(name, List.of(TaskPart.text(text)));
    }
}
