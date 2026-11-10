package com.mafia.service;

import com.mafia.model.Game;
import com.mafia.model.NightResult;
import com.mafia.model.Player;
import com.mafia.model.Team;
import com.mafia.model.role.Civilian;
import com.mafia.model.role.Commissioner;
import com.mafia.model.role.Doctor;
import com.mafia.model.role.Lover;
import com.mafia.model.role.Mafia;
import com.mafia.model.role.NightActionResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NightResolverTest {

    private final NightResolver resolver = new NightResolver();

    @Test
    void doctorSavesVictim() {
        Player mafia = new Player("Мафия", new Mafia());
        Player doctor = new Player("Доктор", new Doctor());
        Player victim = new Player("Аня", new Civilian());
        Game game = new Game(List.of(mafia, doctor, victim));

        NightResult result = resolver.resolve(game, List.of(
                NightActionResult.kill(mafia, victim),
                NightActionResult.heal(doctor, victim)
        ));

        assertNull(result.getKilled());
        assertEquals(victim, result.getSaved());
        assertTrue(victim.isAlive());
    }

    @Test
    void mafiaKillsWithoutHeal() {
        Player mafia = new Player("Мафия", new Mafia());
        Player victim = new Player("Аня", new Civilian());
        Game game = new Game(List.of(mafia, victim));

        NightResult result = resolver.resolve(game, List.of(
                NightActionResult.kill(mafia, victim)
        ));

        assertEquals(victim, result.getKilled());
        assertNull(result.getSaved());
        assertFalse(victim.isAlive());
    }

    @Test
    void loverBlocksDoctor() {
        Player mafia = new Player("Мафия", new Mafia());
        Player doctor = new Player("Доктор", new Doctor());
        Player lover = new Player("Любовница", new Lover());
        Player victim = new Player("Аня", new Civilian());
        Game game = new Game(List.of(mafia, doctor, lover, victim));

        NightResult result = resolver.resolve(game, List.of(
                NightActionResult.block(lover, doctor),
                NightActionResult.heal(doctor, victim),
                NightActionResult.kill(mafia, victim)
        ));

        assertEquals(victim, result.getKilled());
        assertFalse(victim.isAlive());
        assertTrue(result.getHealed().isEmpty());
        assertTrue(result.getBlocked().contains(doctor));
    }

    @Test
    void commissionerCheckAddsKnowledge() {
        Player commissioner = new Player("Комиссар", new Commissioner());
        Player mafia = new Player("Мафия", new Mafia());
        Game game = new Game(List.of(commissioner, mafia));

        NightResult result = resolver.resolve(game, List.of(
                NightActionResult.check(commissioner, mafia, Team.MAFIA)
        ));

        assertEquals(Team.MAFIA, result.getChecks().get(mafia));
        assertEquals(Team.MAFIA, commissioner.getKnowledge().getKnownTeam(mafia).orElseThrow());
    }

    @Test
    void loverBlocksMafia() {
        Player mafia = new Player("Мафия", new Mafia());
        Player lover = new Player("Любовница", new Lover());
        Player victim = new Player("Аня", new Civilian());
        Game game = new Game(List.of(mafia, lover, victim));

        NightResult result = resolver.resolve(game, List.of(
                NightActionResult.block(lover, mafia),
                NightActionResult.kill(mafia, victim)
        ));

        assertNull(result.getKilled());
        assertTrue(victim.isAlive());
        assertTrue(result.getBlocked().contains(mafia));
    }
}
