package com.mafia.model.role;

import com.mafia.model.Player;
import com.mafia.model.Team;


public class Lover extends Role {

    public Lover() {
        super("Любовница", Team.CIVILIANS);
    }

    @Override
    public NightActionResult perform(Player actor, Player target) {
        return NightActionResult.block(actor, target);
    }
}