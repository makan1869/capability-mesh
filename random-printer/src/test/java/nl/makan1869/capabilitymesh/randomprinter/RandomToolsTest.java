package nl.makan1869.capabilitymesh.randomprinter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RandomToolsTest {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final RandomTools randomTools = new RandomTools();

    @Test
    void returnsASingleLetterRegardlessOfInput() {
        String result = randomTools.randomCharacter("this input is ignored");

        assertThat(result).hasSize(1);
        assertThat(LETTERS).contains(result);
    }

    @Test
    void ignoresAnEmptyInput() {
        String result = randomTools.randomCharacter("");

        assertThat(result).hasSize(1);
        assertThat(LETTERS).contains(result);
    }

    @Test
    void eventuallyProducesDifferentCharactersAcrossManyCalls() {
        boolean sawDifferentValues = false;
        String first = randomTools.randomCharacter("x");
        for (int i = 0; i < 100 && !sawDifferentValues; i++) {
            if (!randomTools.randomCharacter("x").equals(first)) {
                sawDifferentValues = true;
            }
        }

        assertThat(sawDifferentValues).isTrue();
    }
}
