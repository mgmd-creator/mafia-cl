package com.mafia.model.role;

import com.mafia.model.Player;
import com.mafia.model.Team;


public class Civilian extends Role {

    public Civilian() {
        super("Мирный", Team.CIVILIANS);
    }

    @Override
    public NightActionResult perform(Player actor, Player target) {
        return NightActionResult.none(actor);
    }
}