package com.mafia.model.role;

import com.mafia.model.Player;
import com.mafia.model.Team;


/**
 * Доктор.
 */
public class Doctor extends Role {

    public Doctor() {
        super("Доктор", Team.CIVILIANS);
    }

    @Override
    public NightActionResult perform(Player actor, Player target) {
        return NightActionResult.heal(actor, target);
    }
}