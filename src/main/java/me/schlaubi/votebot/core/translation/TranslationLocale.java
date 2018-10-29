package me.schlaubi.votebot.core.translation;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

@Log4j2
public class TranslationLocale {

    @Getter
    private final Locale locale;
    @Getter
    private final String languageName;
    @Getter
    private final ResourceBundle resourceBundle;
    private final TranslationManager translationManager;

    public static TranslationLocale create(Locale locale, String languageName, TranslationManager translationManager, boolean defaultLocale) {
        try {
            if (!defaultLocale)
                return new TranslationLocale(locale, languageName, translationManager);
            else
                return new TranslationLocale(locale, languageName, translationManager) {
                    @Override
                    public String translate(String key) {
                        if (getResourceBundle().containsKey(key))
                            return getResourceBundle().getString(key);
                        else {
                            log.error(String.format("TranslationLocale for '%s' missing in default locale %s", key, languageName));
                            return "Missing translation for " + key;
                        }
                    }
                };
        } catch (IOException e) {
            log.error("[TranslationManager] Error while creating locale");
            return null;
        }
    }

    private TranslationLocale(Locale locale, String languageName, TranslationManager translationManager) throws IOException {
        this.locale = locale;
        this.languageName = languageName;
        this.translationManager = translationManager;
        this.resourceBundle = new PropertyResourceBundle(new InputStreamReader(ClassLoader.getSystemResourceAsStream(String.format("translation_%s.properties", locale.toLanguageTag())), StandardCharsets.UTF_8));
    }

    public String translate(String key) {
        if (resourceBundle.containsKey(key))
            return resourceBundle.getString(key);
        else
            return translationManager.getDefaultLocale().translate(key);
    }
}
