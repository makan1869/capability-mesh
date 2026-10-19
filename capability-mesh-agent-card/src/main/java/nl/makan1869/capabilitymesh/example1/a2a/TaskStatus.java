package nl.makan1869.capabilitymesh.example1.a2a;

public record TaskStatus(String state) {
    public static TaskStatus completed() {
        return new TaskStatus("completed");
    }
}
