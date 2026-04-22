package nl.makan1869.capabilitymesh.example1.a2a;

import java.util.List;

/**
 * A2A Agent Card — describes an agent's identity, capabilities, and skills.
 * Served at the agent's well-known discovery URL.
 *
 * @see <a href="https://google.github.io/A2A/specification/">A2A Specification</a>
 */
public record AgentCard(
        String name,
        String description,
        String url,
        String version,
        AgentCapabilities capabilities,
        List<AgentSkill> skills
) {}
