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
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameServiceTest {

    private final GameService service = new GameService();

    private long countRole(Game game, Class<? extends Role> roleClass) {
        return game.getPlayers().stream()
                .filter(player -> roleClass.isInstance(player.getRole()))
                .count();
    }

    @Test
    void createGameWithSixPlayersHasRolesAndKnowledge() {
        Game game = service.createGame(List.of(
                "Аня", "Борис", "Вера", "Глеб", "Дина", "Егор"
        ));

        assertEquals(6, game.getPlayers().size());
        assertEquals(2L, countRole(game, Mafia.class));
        assertEquals(1L, countRole(game, Commissioner.class));
        assertEquals(1L, countRole(game, Doctor.class));
        assertEquals(1L, countRole(game, Lover.class));
        assertEquals(1L, countRole(game, Civilian.class));

        List<Player> mafia = game.getAliveMafia();
        assertEquals(2, mafia.size());

        Player first = mafia.get(0);
        Player second = mafia.get(1);

        assertEquals(Team.MAFIA, first.getKnowledge().getKnownTeam(first).orElseThrow());
        assertEquals(Team.MAFIA, first.getKnowledge().getKnownTeam(second).orElseThrow());
        assertEquals(Team.MAFIA, second.getKnowledge().getKnownTeam(first).orElseThrow());
    }

    @Test
    void mafiaWinsWhenNotFewerThanCity() {
        Game game = new Game(List.of(
                new Player("Мафия", new Mafia()),
                new Player("Аня", new Civilian())
        ));

        assertEquals(Optional.of(Team.MAFIA), service.checkWinner(game));
    }

    @Test
    void cityWinsWhenNoMafiaAlive() {
        Game game = new Game(List.of(
                new Player("Аня", new Civilian())
        ));

        assertEquals(Optional.of(Team.CIVILIANS), service.checkWinner(game));
    }
}
