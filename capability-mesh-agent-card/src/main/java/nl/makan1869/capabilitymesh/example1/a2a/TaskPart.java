package nl.makan1869.capabilitymesh.example1.a2a;

public record TaskPart(
        String type,
        String text
) {
    public static TaskPart text(String text) {
        return new TaskPart("text", text);
    }
}
