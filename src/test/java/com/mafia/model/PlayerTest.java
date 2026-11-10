package com.mafia.model;

import com.mafia.model.role.Civilian;
import com.mafia.model.role.Mafia;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void playerStartsAlive() {
        Player player = new Player("Аня", new Civilian());
        assertTrue(player.isAlive());
    }

    @Test
    void killMakesPlayerDead() {
        Player player = new Player("Аня", new Civilian());
        player.kill();
        assertFalse(player.isAlive());
    }

    @Test
    void mafiaPlayerIsMafia() {
        Player player = new Player("Борис", new Mafia());
        assertTrue(player.isMafia());
    }
}
