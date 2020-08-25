package ch.itds.pbs.portal.service;

import ch.itds.pbs.portal.domain.Language;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LanguageService {

    public Language convertToLanguage(Locale locale) {
        Language language = Language.DE;

        for (Language l : Language.values()) {
            if (l.name().equalsIgnoreCase(locale.getLanguage())) {
                language = l;
            }
        }

        return language;
    }

}
