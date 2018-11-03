package me.schlaubi.votebot.util;

import me.schlaubi.votebot.core.entities.Vote;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Misc {

    public static final String[] EMOTES = {"🍏", "🍎", "🍐", "🍊", "🍋", "🍔", "🍟", "🌭", "🍕", "🍝", "⚽", "🏀", "🏈", "⚾", "🎾", "☎", "📟", "💿", "🖲", "🕹", "🎥", "⌚", "📱", "⏰"};

    public static final Color[] SERIES_COLORS = {Color.decode("#7289DA"), Color.decode("#ff6b6b"), Color.decode("#ff906a"), Color.decode("#f7f56c"), Color.decode("#b6f76c"), Color.decode("#6cf798"), Color.decode("#6cf2f7"), Color.decode("#6ca8f7"), Color.decode("#9e6cf7"), Color.decode("#f87cff")};

    /**
     * Get's the first key from value x of a map
     * @param map The map
     * @param neededValue The value of the key
     * @param <E> The type of the key
     * @param <T> The type of the value
     * @return The key
     */
    public static <E, T> E getValueByKey(Map<E, T> map, T neededValue) {
        Optional<Map.Entry<E, T>> optional = map.entrySet().stream().filter(entry -> entry.getValue().equals(neededValue)).findFirst();
        return optional.map(Map.Entry::getKey).orElse(null);
    }

    public static Date parseDate(String date) {
        int amount = parseInt(date);
        if (amount == 0)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if (date.contains("d"))
            cal.add(Calendar.DAY_OF_MONTH, amount);
        else if (date.contains("m"))
            cal.add(Calendar.MINUTE, amount);
        else if (date.contains("y"))
            cal.add(Calendar.YEAR, amount);
        else if (date.contains("M"))
            cal.add(Calendar.MONTH, amount);
        else if (date.contains("h"))
            cal.add(Calendar.HOUR_OF_DAY, amount);
        else if(date.contains("s"))
            cal.add(Calendar.SECOND, amount);
        else
            return null;
        return cal.getTime();
    }

    private static int parseInt(String integer) {
        try {
            return Integer.parseInt(integer.replace("d", "").replace("m", "").replace("y", "").replace("M", "").replace("h", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String mentionEmote(String emoteId, Guild guild) {
        return guild.getEmoteById(emoteId).getAsMention();
    }

    private static List<String> getAvailableEmotes(Vote vote, Guild guild, int count) {
        List<String> emotes = new LinkedList<>();
        if (guild.getSelfMember().hasPermission(Permission.MESSAGE_EXT_EMOJI))
            emotes.addAll(guild.getEmotes().stream().map(Emote::getId).collect(Collectors.toSet()));
        if (emotes.size() < count)
            Collections.addAll(emotes, EMOTES);
        if (vote != null)
            emotes.removeAll(vote.getEmotes().keySet());
        return emotes;
    }

    public static List<String> getAvailableEmotes(Vote vote) {
        return getAvailableEmotes(vote, vote.getGuild(), vote.getOptions().size());
    }

    public static List<String> getAvailableEmotes(Guild guild, int count) {
        return getAvailableEmotes(null, guild, count);
    }
}
