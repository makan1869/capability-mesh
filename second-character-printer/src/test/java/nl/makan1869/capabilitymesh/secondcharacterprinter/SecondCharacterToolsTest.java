package nl.makan1869.capabilitymesh.secondcharacterprinter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecondCharacterToolsTest {

    private final SecondCharacterTools secondCharacterTools = new SecondCharacterTools();

    @Test
    void returnsTheSecondCharacterOfAString() {
        assertThat(secondCharacterTools.secondCharacter("hello")).isEqualTo("e");
    }

    @Test
    void returnsTheSecondCharacterWhenExactlyTwoCharactersLong() {
        assertThat(secondCharacterTools.secondCharacter("ab")).isEqualTo("b");
    }

    @Test
    void rejectsAStringShorterThanTwoCharacters() {
        assertThatThrownBy(() -> secondCharacterTools.secondCharacter("a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsAnEmptyString() {
        assertThatThrownBy(() -> secondCharacterTools.secondCharacter(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
