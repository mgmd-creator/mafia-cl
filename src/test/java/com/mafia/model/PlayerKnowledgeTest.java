package com.mafia.model;

import com.mafia.model.role.Civilian;
import com.mafia.model.role.Mafia;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerKnowledgeTest {

    private final Player anya = new Player("Аня", new Civilian());
    private final Player boris = new Player("Борис", new Mafia());

    @Test
    void remembersKnownTeam() {
        anya.getKnowledge().rememberTeam(boris, Team.MAFIA);
        assertEquals(Optional.of(Team.MAFIA), anya.getKnowledge().getKnownTeam(boris));
    }
}
