package ch.itds.pbs.portal.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageTest {

    @Test
    void valueOfOrDefault() {
        assertEquals(Language.DE, Language.valueOfOrDefault("de"));
        assertEquals(Language.FR, Language.valueOfOrDefault("FR"));
        assertEquals(Language.FR, Language.valueOfOrDefault("fr"));
        assertEquals(Language.FR, Language.valueOfOrDefault("fR"));
        assertEquals(Language.IT, Language.valueOfOrDefault("it"));
        assertEquals(Language.EN, Language.valueOfOrDefault("en"));
        assertEquals(Language.DE, Language.valueOfOrDefault("Ru"));
    }
}