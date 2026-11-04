package nl.makan1869.capabilitymesh.nextcharprinter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "capability-mesh.registry.enabled=false")
class NextCharPrinterApplicationTests {

    @Test
    void contextLoads() {
    }
}
