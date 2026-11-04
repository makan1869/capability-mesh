package nl.makan1869.capabilitymesh.randomprinter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "capability-mesh.registry.enabled=false")
class RandomPrinterApplicationTests {

    @Test
    void contextLoads() {
    }
}
