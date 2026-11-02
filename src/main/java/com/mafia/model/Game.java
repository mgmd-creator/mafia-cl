package com.mafia.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Game {

    private final List<Player> players;
    private GamePhase phase;
    private int day;
    private Team winner;

    public Game(List<Player> players) {
        if (players == null || players.isEmpty()) {
            throw new IllegalArgumentException("В партии должен быть хотя бы один игрок");
        }
        this.players = List.copyOf(players);
        this.phase = GamePhase.NIGHT;
        this.day = 1;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getAlivePlayers() {
        List<Player> alive = new ArrayList<>();
        for (Player player : players) {
            if (player.isAlive()) {
                alive.add(player);
            }
        }
        return alive;
    }

    public List<Player> getAliveMafia() {
        List<Player> mafia = new ArrayList<>();
        for (Player player : getAlivePlayers()) {
            if (player.isMafia()) {
                mafia.add(player);
            }
        }
        return mafia;
    }

     public List<Player> getAliveCity() {
         List<Player> city = new ArrayList<>();
         for (Player player : getAlivePlayers()) {
             if (!player.isMafia()) {
                 city.add(player);
             }
         }
         return city;
     }

     public int getAliveMafiaCount() {
        return getAliveMafia().size();
     }

     public int getAliveCityCount() {
        return getAliveCity().size();
     }

     public GamePhase getPhase() {
        return phase;
     }

     public void setPhase(GamePhase phase) {
        this.phase = Objects.requireNonNull(phase, "Фаза не может быть null");
     }

     public int getDay() {
        return day;
     }

     public void nextDay() {
        day++;
     }

     public Team getWinner() {
        return winner;
     }

    public void setWinner(Team winner) {
        this.winner = Objects.requireNonNull(winner, "Победитель не может быть null");
    }

    public boolean isGameOver() {
        return winner != null;
    }
}
