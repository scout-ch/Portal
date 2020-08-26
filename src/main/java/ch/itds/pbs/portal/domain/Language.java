package ch.itds.pbs.portal.domain;

public enum Language {
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
