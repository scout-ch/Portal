package ch.itds.pbs.portal.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Getter
@Setter
@Embeddable
public class LocalizedString {

    private String de = null;
    private String fr = null;
    private String it = null;
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

    public void put(Language language, String value) {
        switch (language) {
            case DE -> de = value;
            case FR -> fr = value;
            case IT -> it = value;
            case EN -> en = value;
        }
    }
}
