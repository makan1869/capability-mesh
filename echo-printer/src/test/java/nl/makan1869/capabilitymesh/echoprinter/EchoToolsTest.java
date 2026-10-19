package nl.makan1869.capabilitymesh.echoprinter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EchoToolsTest {

    private final EchoTools echoTools = new EchoTools();

    @Test
    void echoesBackTheExactCharacterItIsGiven() {
        assertThat(echoTools.echo("a")).isEqualTo("a");
    }

    @Test
    void echoesBackAnEmptyString() {
        assertThat(echoTools.echo("")).isEqualTo("");
    }

    @Test
    void echoesBackANonAsciiCharacter() {
        assertThat(echoTools.echo("é")).isEqualTo("é");
    }
}
