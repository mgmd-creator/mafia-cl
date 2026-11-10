package com.mafia.model.role;

import com.mafia.model.Player;
import com.mafia.model.Team;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleTest {

    @Test
    void commissionerReturnsCheckWithTeam() {
        Player actor = new Player("Комиссар", new Commissioner());
        Player target = new Player("Мафия", new Mafia());

        NightActionResult result = new Commissioner().perform(actor, target);

        assertEquals(NightActionType.CHECK, result.getType());
        assertEquals(Team.MAFIA, result.getCheckedTeam().orElseThrow());
    }

    @Test
    void allRolesReturnExpectedActionType() {
        Player actor = new Player("Актор", new Civilian());
        Player target = new Player("Цель", new Mafia());

        assertEquals(NightActionType.NONE, new Civilian().perform(actor, target).getType());
        assertEquals(NightActionType.KILL, new Mafia().perform(actor, target).getType());
        assertEquals(NightActionType.HEAL, new Doctor().perform(actor, target).getType());
        assertEquals(NightActionType.BLOCK, new Lover().perform(actor, target).getType());
        assertEquals(NightActionType.CHECK, new Commissioner().perform(actor, target).getType());
    }
}
