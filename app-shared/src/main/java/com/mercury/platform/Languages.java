package com.mercury.platform;

public enum Languages {
    en("english"),
    RU("russian"),
    ;

    private String name;

    Languages(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
