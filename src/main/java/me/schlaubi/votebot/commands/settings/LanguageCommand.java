package me.schlaubi.votebot.commands.settings;

import me.schlaubi.votebot.core.command.Command;
import me.schlaubi.votebot.core.command.CommandCategory;
import me.schlaubi.votebot.core.command.CommandEvent;
import me.schlaubi.votebot.core.command.Result;
import me.schlaubi.votebot.core.command.permission.Permissions;
import me.schlaubi.votebot.core.translation.TranslationManager;
import me.schlaubi.votebot.util.FormatUtil;

import java.util.Locale;

public class LanguageCommand extends Command {

    public LanguageCommand() {
        super(new String[]{"language", "lang", "locale", "locales", "loc"}, CommandCategory.GENERAL, Permissions.everyone(), "Lets you set your language", "[language-tag]");
    }

    @Override
    public Result run(String[] args, CommandEvent event) {
        final TranslationManager translationManager = event.getBot().getTranslationManager();
        if (args.length == 0)
            return send(
                    info(
                            event.translate("command.language.title"),
                            event.translate("command.language.description")
                    ).addField(event.translate("phrases.text.locales"), FormatUtil.formatLocales(translationManager.getLocales()), false)
            );

        Locale locale = Locale.forLanguageTag(args[0]);

        if (!event.getBot().getTranslationManager().isTranslated(locale))
            return send(error(event.translate("command.language.not.translated.title"), event.translate("command.language.not.translated.description")));

        event.getDatabaseUser().setLocale(locale);
        return send(success(event.translate("command.language.success.title"), String.format(event.translate("command.language.success.description"), locale.toLanguageTag())));
    }
}
