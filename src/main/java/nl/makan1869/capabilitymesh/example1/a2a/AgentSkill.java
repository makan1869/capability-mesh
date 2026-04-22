package nl.makan1869.capabilitymesh.example1.a2a;

import java.util.List;

public record AgentSkill(
        String id,
        String name,
        String description,
        List<String> tags,
        List<String> examples
) {}
