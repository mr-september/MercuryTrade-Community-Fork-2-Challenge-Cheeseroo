package com.mercury.platform;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LangTranslator {
    private static final Logger logger = LogManager.getLogger(LangTranslator.class);

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
        if (lang == null) {
            lang = Languages.en;
        }
        System.out.println("intializating translation for " + lang.shortName());
        Map<String, String> translations = new HashMap<>();
        try {
            InputStream is = this.getClass().getResourceAsStream("/lang/" + lang.shortName() + ".lang");

            if (is == null) {
                System.out.println("first resource not exists");
                is = this.getClass().getResourceAsStream("/" + lang.shortName() + ".lang");
                if (is == null) {
                    System.out.println("second resource not exists");
                    return translations;
                } else {
                    System.out.println("2 file exists");
                }
            } else {
                System.out.println("file exists");
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
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

            return translations;
        } catch (Exception e) {
            logger.error("Loading translations error", e);
        } finally {
            return translations;
        }
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
