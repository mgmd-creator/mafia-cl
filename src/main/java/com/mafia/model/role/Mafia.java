package com.mafia.model.role;

import com.mafia.model.Player;
import com.mafia.model.Team;

public class Mafia extends Role {

    public Mafia() {
        super("Мафия", Team.MAFIA);
    }

    @Override
    public NightActionResult perform(Player actor, Player target) {
        return NightActionResult.kill(actor, target);
    }
}