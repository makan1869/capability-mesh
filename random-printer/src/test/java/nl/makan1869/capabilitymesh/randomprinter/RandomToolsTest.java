package nl.makan1869.capabilitymesh.randomprinter;

import java.util.Random;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RandomToolsTest {

    private static final String PRINTABLE_CHARACTERS =
            "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    @Test
    void indexFiveAlwaysReturnsTheGivenCharacter() {
        Random random = mock(Random.class);
        // 5 calls to fill the random prefix, then a 6th call for the final index (0-5).
        when(random.nextInt(anyInt())).thenReturn(0, 1, 2, 3, 4, 5);
        RandomTools randomTools = new RandomTools(random);

        String result = randomTools.randomCharacter("Z");

        assertThat(result).isEqualTo("Z");
    }

    @Test
    void anIndexBelowFiveReturnsOneOfTheRandomPrefixCharacters() {
        Random random = mock(Random.class);
        when(random.nextInt(anyInt())).thenReturn(0, 1, 2, 3, 4, 2);
        RandomTools randomTools = new RandomTools(random);

        // prefix index 2 picked from PRINTABLE_CHARACTERS using the sequence 0,1,2,3,4
        String expected = String.valueOf(PRINTABLE_CHARACTERS.charAt(2));
        String result = randomTools.randomCharacter("Z");

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void theRandomPrefixCanIncludeDigitsAndSymbolsNotJustLetters() {
        Random random = mock(Random.class);
        // index into PRINTABLE_CHARACTERS for '5' and for '@'
        int digitIndex = PRINTABLE_CHARACTERS.indexOf('5');
        int symbolIndex = PRINTABLE_CHARACTERS.indexOf('@');
        when(random.nextInt(anyInt()))
                .thenReturn(digitIndex, symbolIndex, 0, 0, 0, 0) // prefix picks '5' then '@', final index 0
                .thenReturn(digitIndex, symbolIndex, 0, 0, 0, 1); // final index 1
        RandomTools randomTools = new RandomTools(random);

        assertThat(randomTools.randomCharacter("Z")).isEqualTo("5");
        assertThat(randomTools.randomCharacter("Z")).isEqualTo("@");
    }

    @Test
    void rejectsInputThatIsNotASingleCharacter() {
        RandomTools randomTools = new RandomTools();

        assertThatThrownBy(() -> randomTools.randomCharacter("ab"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> randomTools.randomCharacter(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void usingARealRandomNeverThrowsAndEventuallyReturnsTheGivenCharacter() {
        RandomTools randomTools = new RandomTools();
        boolean sawGivenCharacter = false;

        for (int i = 0; i < 200 && !sawGivenCharacter; i++) {
            String result = randomTools.randomCharacter("Z");
            assertThat(result).hasSize(1);
            if (result.equals("Z")) {
                sawGivenCharacter = true;
            }
        }

        assertThat(sawGivenCharacter).isTrue();
    }
}
