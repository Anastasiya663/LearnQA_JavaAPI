package other;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class JustTest {

    @ParameterizedTest
    @ValueSource(strings = {"", "Hello, world", "Welcome, my friend!", "123456789012345"})
    void testString(String s) {
        assertTrue(s.length() > 15,  "Length of string less than 15");
    }
}
