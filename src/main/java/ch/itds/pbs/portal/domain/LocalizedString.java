package ch.itds.pbs.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.Locale;

@Getter
@Setter
@Embeddable
@Schema(description = "localized string")
public class LocalizedString {

    @Schema(description = "Deutsch")
    private String de = null;

    @Schema(description = "Francais")
    private String fr = null;

    @Schema(description = "Italiano")
    private String it = null;

    @Schema(description = "English")
    private String en = null;

    public String getOrDefault(Language language, String defaultValue) {
        switch (language) {
            case DE -> {
                return de;
            }
            case FR -> {
                return fr;
            }
            case IT -> {
                return it;
            }
            case EN -> {
                return en;
            }
            default -> {
                return defaultValue;
            }
        }
    }

    public String getLocalized(Locale locale) {
        switch (locale.getLanguage()) {
            case "de" -> {
                return de;
            }
            case "fr" -> {
                return fr;
            }
            case "it" -> {
                return it;
            }
            case "en" -> {
                return en;
            }
            default -> {
                return de;
            }
        }
    }

    public void put(Language language, String value) {
        switch (language) {
            case DE -> de = value;
            case FR -> fr = value;
            case IT -> it = value;
            case EN -> en = value;
        }
    }
}
