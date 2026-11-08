package com.mafia.service;

import com.mafia.model.Game;
import com.mafia.model.Player;
import com.mafia.model.Team;
import com.mafia.model.role.Civilian;
import com.mafia.model.role.Commissioner;
import com.mafia.model.role.Doctor;
import com.mafia.model.role.Lover;
import com.mafia.model.role.Mafia;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameService {

    public Game createGame(List<String> names) {
        if (names == null || names.size() != 5) {
            throw new IllegalArgumentException("Нужно ровно 5 игроков");
        }

        List<Player> players = new ArrayList<>();

        players.add(new Player(names.get(0), new Mafia()));
        players.add(new Player(names.get(1), new Commissioner()));
        players.add(new Player(names.get(2), new Doctor()));
        players.add(new Player(names.get(3), new Lover()));
        players.add(new Player(names.get(4), new Civilian()));

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

    public void play(Game game) {
    }
}