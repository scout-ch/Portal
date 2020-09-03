package ch.itds.pbs.portal.domain;

import ch.itds.pbs.portal.util.I18nEnum;

public enum Language implements I18nEnum {
    DE, FR, IT, EN;

    public static Language valueOfOrDefault(String key) {
        for (Language l : values()) {
            if (l.name().equalsIgnoreCase(key)) {
                return l;
            }
        }
        return Language.DE;
    }
}
