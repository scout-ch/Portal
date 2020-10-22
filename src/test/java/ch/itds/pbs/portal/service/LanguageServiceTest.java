package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Language;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LanguageServiceTest {

    LanguageService languageService = new LanguageService();

    @Test
    public void defaultLanguage() {
        Language l = languageService.convertToLanguage(Locale.CHINA);
        assertEquals(Language.DE, l);
    }

    @Test
    public void german() {
        Language l = languageService.convertToLanguage(Locale.GERMAN);
        assertEquals(Language.DE, l);
    }

    @Test
    public void french() {
        Language l = languageService.convertToLanguage(Locale.FRENCH);
        assertEquals(Language.FR, l);
    }

    @Test
    public void italian() {
        Language l = languageService.convertToLanguage(Locale.ITALIAN);
        assertEquals(Language.IT, l);
    }

    @Test
    public void english() {
        Language l = languageService.convertToLanguage(Locale.ENGLISH);
        assertEquals(Language.EN, l);
    }


}