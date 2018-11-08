package me.schlaubi.votebot.core.translation;

import lombok.Getter;
import me.schlaubi.votebot.core.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Getter
public class TranslationManager {

    private final TranslationLocale defaultLocale;
    private final List<TranslationLocale> locales;

    public TranslationManager() {
        this.defaultLocale = TranslationLocale.create(Locale.forLanguageTag("en-US"), "English (United States)", this, true);
        this.locales = new ArrayList<>();
        locales.add(defaultLocale);
        locales.add(TranslationLocale.create(Locale.forLanguageTag("fr-FR"), "Français (French)", this, false));
    }

    public boolean isTranslated(Locale locale) {
        return locales.stream().anyMatch(translation -> translation.getLocale().equals(locale));
    }

    public TranslationLocale getLocale(Locale locale) {
        Optional<TranslationLocale> result = locales.stream().filter(translation -> translation.getLocale().equals(locale)).findFirst();
        return result.orElse(defaultLocale);
    }

    public TranslationLocale getLocale(User user) {
        return getLocale(user.getLocale());
    }

}
