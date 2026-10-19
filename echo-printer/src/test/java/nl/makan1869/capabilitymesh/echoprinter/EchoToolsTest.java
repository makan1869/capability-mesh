package nl.makan1869.capabilitymesh.echoprinter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EchoToolsTest {

    private final EchoTools echoTools = new EchoTools();

    @Test
    void echoesBackALowercaseLetter() {
        assertThat(echoTools.echo("a")).isEqualTo("a");
    }

    @Test
    void echoesBackAnUppercaseLetter() {
        assertThat(echoTools.echo("Z")).isEqualTo("Z");
    }

    @Test
    void echoesBackADigit() {
        assertThat(echoTools.echo("7")).isEqualTo("7");
    }

    @Test
    void rejectsAnEmptyString() {
        assertThatThrownBy(() -> echoTools.echo("")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAMultiCharacterString() {
        assertThatThrownBy(() -> echoTools.echo("ab")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsASymbol() {
        assertThatThrownBy(() -> echoTools.echo("@")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsANonAsciiCharacter() {
        assertThatThrownBy(() -> echoTools.echo("é")).isInstanceOf(IllegalArgumentException.class);
    }
}
