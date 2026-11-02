package com.mafia.model.role;

import com.mafia.model.Player;

public interface NightAction {
    NightActionResult perform(Player actor, Player target);
}