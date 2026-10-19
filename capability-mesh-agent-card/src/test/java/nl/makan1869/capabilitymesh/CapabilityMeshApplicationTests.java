package nl.makan1869.capabilitymesh;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled(
        "SmartPrinterOrchestrator's ApplicationRunner bean fires live HTTP calls to "
                + "localhost:8080/api/agents/... and a real Anthropic call during context "
                + "startup, so @SpringBootTest can only succeed with the app's own agents "
                + "already running and a real API key - same category of issue as printer's "
                + "lack of a context-load test. @Disabled must be on the class: Spring builds "
                + "the context during test-instance creation, before a method-level @Disabled "
                + "would take effect.")
class CapabilityMeshApplicationTests {

    @Test
    void contextLoads() {
    }

}
