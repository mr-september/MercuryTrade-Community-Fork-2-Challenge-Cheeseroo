package com.mercury.platform;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LangTranslator {
    static {
        try {
            instance = new LangTranslator();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Map<String, String> translations = new HashMap<>();
    private static LangTranslator instance;

    private LangTranslator() throws IOException {
        translations = intializeTranslations(Languages.en);
    }
    public static LangTranslator getInstance() {
        return instance;
    }

    private Map<String, String> intializeTranslations(Languages lang) throws IOException {
        Map<String, String> translations = new HashMap<>();
        File file = new File("src/main/resources/lang/" + lang.toString() + ".lang");
        if (file.exists() && file.isFile()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                        continue;
                    }
                    String[] split = line.split("=");
                    String key = split[0].trim();
                    String value = split[1].trim();
                    translations.put(key, value);
                }
            }
        }
        return translations;
    }

    public void changeLanguage(Languages lang) throws IOException {
        Map<String, String> translationsTemp = intializeTranslations(lang);
        if (!translationsTemp.isEmpty()) {
            translations = translationsTemp;
        }
    }

    public String getTranslated(TranslationKey key) {
        return translations.getOrDefault(key.toString(), key.getDefaultValue());
    }

}
