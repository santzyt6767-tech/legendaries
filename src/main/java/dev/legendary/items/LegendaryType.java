package dev.legendary.items;

public enum LegendaryType {
    VOIDBANE,
    EMBERHEART,
    GLACIUS,
    STORMBREAKER,
    SOULREAPER;

    public static LegendaryType fromString(String name) {
        for (LegendaryType type : values()) {
            if (type.name().equalsIgnoreCase(name)) return type;
        }
        return null;
    }
}
