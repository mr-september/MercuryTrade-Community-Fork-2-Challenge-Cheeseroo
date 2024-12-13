package com.mercury.platform;

public enum Languages {
    en("english"),
    pl("polish"),
    ru("russian"),
    ;

    private String name;

    Languages(String name) {
        this.name = name;
    }

    public static boolean isCJK() {
        return false;
    }

    public String getName() {
        return name;
    }
}
