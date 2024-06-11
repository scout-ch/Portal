package ch.itds.pbs.portal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import jakarta.persistence.Embeddable;
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
        String value;
        switch (language) {
            case DE -> {
                value = de;
            }
            case FR -> {
                value = fr;
            }
            case IT -> {
                value = it;
            }
            case EN -> {
                value = en;
            }
            default -> {
                value = defaultValue;
            }
        }
        if (StringUtils.hasText(value)) {
            return value;
        } else {
            return defaultValue;
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
