package com.mafia.model.role;

import com.mafia.model.Player;

/**
 * Контракт ночного действия роли.
 */
public interface NightAction {
    NightActionResult perform(Player actor, Player target);
}