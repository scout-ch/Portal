package ch.itds.pbs.portal.util;

public interface I18nEnum {
    default String getMessageKey(Enum<?> e) {
        return e.getClass().getSimpleName() + '.' + e.name();
    }
}