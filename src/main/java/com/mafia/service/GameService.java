package com.mafia.service;

import com.mafia.model.Game;
import com.mafia.model.Player;
import com.mafia.model.Team;
import com.mafia.model.role.Civilian;
import com.mafia.model.role.Commissioner;
import com.mafia.model.role.Doctor;
import com.mafia.model.role.Lover;
import com.mafia.model.role.Mafia;
import com.mafia.model.role.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GameService {

    public Game createGame(List<String> names) {
        if (names == null || names.size() < 5) {
            throw new IllegalArgumentException("Нужно минимум 5 игроков");
        }

        Set<String> uniqueNames = new HashSet<>();

        for (String name : names) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Имя игрока не может быть пустым");
            }
            if (!uniqueNames.add(name)) {
                throw new IllegalArgumentException("Имена игроков не должны повторяться");
            }
        }

        int mafiaCount = Math.max(1, names.size() / 3);

        List<Role> roles = new ArrayList<>();

        for (int i = 0; i < mafiaCount; i++) {
            roles.add(new Mafia());
        }

        roles.add(new Commissioner());
        roles.add(new Doctor());
        roles.add(new Lover());

        while (roles.size() < names.size()) {
            roles.add(new Civilian());
        }

        Collections.shuffle(roles);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            players.add(new Player(names.get(i), roles.get(i)));
        }
        return new Game(players);
    }

    public Optional<Team> checkWinner(Game game) {
        if (game.getAliveMafiaCount() == 0) {
            return Optional.of(Team.CIVILIANS);
        }

        if (game.getAliveMafiaCount() >= game.getAliveCityCount()) {
            return Optional.of(Team.MAFIA);
        }

        return Optional.empty();
    }
}