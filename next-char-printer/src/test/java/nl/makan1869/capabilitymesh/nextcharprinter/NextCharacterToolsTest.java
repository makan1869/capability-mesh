package nl.makan1869.capabilitymesh.nextcharprinter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NextCharacterToolsTest {

    private final NextCharacterTools nextCharacterTools = new NextCharacterTools();

    @Test
    void returnsTheNextLetter() {
        assertThat(nextCharacterTools.nextCharacter("A")).isEqualTo("B");
        assertThat(nextCharacterTools.nextCharacter("Y")).isEqualTo("Z");
    }

    @Test
    void cyclesFromUppercaseZBackToUppercaseA() {
        assertThat(nextCharacterTools.nextCharacter("Z")).isEqualTo("A");
    }

    @Test
    void preservesLowercaseAndCyclesFromZToA() {
        assertThat(nextCharacterTools.nextCharacter("a")).isEqualTo("b");
        assertThat(nextCharacterTools.nextCharacter("z")).isEqualTo("a");
    }

    @Test
    void rejectsInputThatIsNotASingleLetter() {
        assertThatThrownBy(() -> nextCharacterTools.nextCharacter("AB"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> nextCharacterTools.nextCharacter("1"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> nextCharacterTools.nextCharacter(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
