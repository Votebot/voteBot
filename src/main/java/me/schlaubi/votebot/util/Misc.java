package me.schlaubi.votebot.util;

import java.util.Map;
import java.util.Optional;

public class Misc {

    public static final String[] EMOTES = {"🍏", "🍎", "🍐", "🍊", "🍋", "🍌", "🍉", "🍇", "🍓", "🍈", "🍒", "🍑", "🍍", "🥥", "🥝", "🍅", "🍆", "🥑", "🥦", "🥒", "🥨", "🥖", "🍞", "🥐", "🍚", "🍩", "🍪", "🍻", "🥂", "🍷", "🥃", "🍦", "🎂", "🍮", "🍰", "🍭", "🍵", "☕️", "🥛", "🎊", "🏮", "🛏", "⌚️", "⌛️", "🔌", "💡", "🏺", "📕", "🎼", "🎧", "🥁", "🎬", "⚽️", "🏀", "🏈", "🏈", "🏐", "🏉", "🎱", "🏓", "🏸", "🏆", "🎗", "🎪", "🎮", "🎯", "🛰", "🚀", "🚂", "✈️", "🗿", "🗽", "🏰", "🗻", "🌋", "🎡"};
    public static <E, T> E getValueByKey(Map<E, T> map, T neededValue) {
        Optional<Map.Entry<E, T>> optional = map.entrySet().stream().filter(entry -> entry.getValue().equals(neededValue)).findFirst();
        return optional.map(Map.Entry::getKey).orElse(null);
    }
}
