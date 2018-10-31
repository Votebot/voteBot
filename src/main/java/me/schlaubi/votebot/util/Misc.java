package me.schlaubi.votebot.util;

import java.awt.*;
import java.util.Map;
import java.util.Optional;

public class Misc {

    public static final String[] EMOTES = {"🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", "🥒", "🥨", "🥖", "🍞", "🥐", "🍚", "🍩", "🍪", "🍻", "🥂", "🍷", "🥃", "🍦", "🎂", "🍮", "🍰", "🍭", "🍵", "☕", "🥛", "🎊", "🏮", "🛏", "⌚", "⌛", "🔌", "💡", "🏺", "📕", "🎼", "🎧", "🥁", "🎬", "⚽", "🏀", "🏈", "🏈", "🏐", "🏉", "🎱", "🏓", "🏸", "🏆", "🎗", "🎪", "🎮", "🎯", "🛰", "🚀", "🚂", "✈", "🗿", "🗽", "🏰", "🗻", "🌋", "🎡"};

    public static final Color[] SERIES_COLORS = {Color.decode("#7289DA"), Color.decode("#ff6b6b"), Color.decode("#ff906a"), Color.decode("#f7f56c"), Color.decode("#b6f76c"), Color.decode("#6cf798"), Color.decode("#6cf2f7"), Color.decode("#6ca8f7"), Color.decode("#9e6cf7"), Color.decode("#f87cff")};

    public static <E, T> E getValueByKey(Map<E, T> map, T neededValue) {
        Optional<Map.Entry<E, T>> optional = map.entrySet().stream().filter(entry -> entry.getValue().equals(neededValue)).findFirst();
        return optional.map(Map.Entry::getKey).orElse(null);
    }
}
