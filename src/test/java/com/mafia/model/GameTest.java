package com.mafia.model;

import com.mafia.model.role.Civilian;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameTest {

    @Test
    void alivePlayersExcludeDead() {
        Player alive = new Player("Аня", new Civilian());
        Player dead = new Player("Борис", new Civilian());
        Game game = new Game(List.of(alive, dead));

        dead.kill();

        assertEquals(List.of(alive), game.getAlivePlayers());
        assertEquals(1, game.getAliveCityCount());
    }
}
