package com.mafia.service;

import com.mafia.model.JournalEntry;
import com.mafia.model.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JournalService {

    private final List<JournalEntry> entries = new ArrayList<>();

    public void addEntry(JournalEntry entry) {
        entries.add(Objects.requireNonNull(entry, "Запись не может быть null"));
    }

    public List<JournalEntry> getJournalFor(Player player) {
        Objects.requireNonNull(player, "Игрок не может быть null");

        return entries.stream()
                .filter(entry -> entry.isVisibleTo(player))
                .toList();
    }
}
