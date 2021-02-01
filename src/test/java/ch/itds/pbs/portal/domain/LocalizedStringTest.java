package ch.itds.pbs.portal.domain;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class LocalizedStringTest {

    @Test
    void getOrDefault() {
        LocalizedString ls = new LocalizedString();
        ls.setDe("de");

        assertEquals("de", ls.getOrDefault(Language.DE, null));
        assertNull(ls.getOrDefault(Language.FR, null));
        assertEquals("default", ls.getOrDefault(Language.FR, "default"));
    }

    @Test
    void getLocalized() {
        LocalizedString ls = new LocalizedString();
        ls.setDe("de");
        ls.setFr("fr");
        ls.setIt("it");
        ls.setEn("en");

        assertEquals("de", ls.getLocalized(Locale.GERMAN));
        assertEquals("fr", ls.getLocalized(Locale.FRENCH));
        assertEquals("it", ls.getLocalized(Locale.ITALIAN));
        assertEquals("en", ls.getLocalized(Locale.ENGLISH));
        assertEquals("de", ls.getLocalized(Locale.JAPANESE));
    }

    @Test
    void put() {
        LocalizedString ls = new LocalizedString();
        ls.put(Language.DE, "de");
        ls.put(Language.FR, "fr");
        ls.put(Language.IT, "it");
        ls.put(Language.EN, "en");

        assertEquals("de", ls.getDe());
        assertEquals("fr", ls.getFr());
        assertEquals("it", ls.getIt());
        assertEquals("en", ls.getEn());
    }
}