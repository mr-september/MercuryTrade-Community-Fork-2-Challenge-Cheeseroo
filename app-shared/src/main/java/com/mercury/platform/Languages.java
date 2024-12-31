package com.mercury.platform;

public enum Languages {
    en(TranslationKey.english, "en"),
    pl(TranslationKey.polish, "pl"),
    ru(TranslationKey.russian, "ru"),
    kr(TranslationKey.korean, "kr"),
    ;

    private TranslationKey name;
    private String shortName;

    Languages(TranslationKey name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public TranslationKey getName() {
        return name;
    }

    public String shortName() {
        return shortName;
    }


    @Override
    public String toString() {
        return name.value();
    }
}
