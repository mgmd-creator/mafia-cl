package com.mafia.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Знания игрока о других участниках.
 */
public class PlayerKnowledge {

    private final Map<Player, Team> knownTeams = new HashMap<>();

    public void rememberTeam(Player player, Team team) {
        Objects.requireNonNull(player, "Игрок не может быть null");
        Objects.requireNonNull(team, "Команда не может быть null");
        knownTeams.put(player, team);
    }

    public Optional<Team> getKnownTeam(Player player) {
        return Optional.ofNullable(knownTeams.get(player));
    }

    public boolean knowsTeamOf(Player player) {
        return knownTeams.containsKey(player);
    }

    public Map<Player, Team> getKnownTeams() {
        return Map.copyOf(knownTeams);
    }
}