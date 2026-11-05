package com.mafia.model;

import java.util.Objects;
import java.util.Set;

public class JournalEntry {
    private final int day;
    private final GamePhase phase;
    private final String message;
    private final Set<Player> visibleTo;

    public JournalEntry(int day, GamePhase phase, String message, Set<Player> visibleTo) {
        if (day < 1) {
            throw new IllegalArgumentException("День должен быть больше 0");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Сообщение не может быть пустым");
        }
        this.day = day;
        this.phase = Objects.requireNonNull(phase, "Фаза не может быть null");
        this.message = message;
        this.visibleTo = Set.copyOf(Objects.requireNonNull(visibleTo, "Получатели не могут быть null"));
    }

    public int getDay() {
        return day;
    }

    public GamePhase getPhase() {
        return phase;
    }

    public String getMessage() {
        return message;
    }

    public Set<Player> getVisibleTo() {
        return visibleTo;
    }

    public boolean isPublic() {
        return visibleTo.isEmpty();
    }

    public boolean isVisibleTo(Player player) {
        return isPublic() || visibleTo.contains(player);
    }
}
