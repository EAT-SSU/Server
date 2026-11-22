package ssu.eatssu.domain.menu.util;

public record SanitizedMenuName(String name, MenuNameExcludeReason excludeReason) {

    public static SanitizedMenuName accepted(String name) {
        return new SanitizedMenuName(name, null);
    }

    public static SanitizedMenuName excluded(String name, MenuNameExcludeReason excludeReason) {
        return new SanitizedMenuName(name, excludeReason);
    }

    public boolean isExcluded() {
        return excludeReason != null;
    }
}
