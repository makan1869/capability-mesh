package nl.makan1869.capabilitymesh.example1.a2a;

import java.util.List;

public record TaskMessage(
        String role,
        List<TaskPart> parts
) {
    public static TaskMessage user(String text) {
        return new TaskMessage("user", List.of(TaskPart.text(text)));
    }
}
