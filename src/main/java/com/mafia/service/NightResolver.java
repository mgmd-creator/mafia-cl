package com.mafia.service;

import com.mafia.model.Game;
import com.mafia.model.NightResult;
import com.mafia.model.Player;
import com.mafia.model.Team;
import com.mafia.model.role.NightActionType;
import com.mafia.model.role.NightActionResult;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NightResolver {

    public NightResult resolve(Game game, List<NightActionResult> actions) {
        Set<Player> blocked = new HashSet<>();
        Set<Player> healed = new HashSet<>();
        Map<Player, Team> checks = new HashMap<>();
        Player killed = null;
        Player saved = null;

        for (NightActionResult action : actions) {
            if (action.getType() == NightActionType.BLOCK) {
                blocked.add(action.getTarget());
            }
        }

        for (NightActionResult action : actions) {
            if (action.getType() == NightActionType.HEAL
                    && !blocked.contains(action.getActor())) {
                healed.add(action.getTarget());
            }
        }

        for (NightActionResult action : actions) {
            if (action.getType() == NightActionType.CHECK
                    && !blocked.contains(action.getActor())) {
                Team team = action.getCheckedTeam().orElseThrow();
                checks.put(action.getTarget(), team);
                action.getActor().getKnowledge().rememberTeam(action.getTarget(), team);
            }
        }

        for (NightActionResult action : actions) {
            if (action.getType() == NightActionType.KILL
                    && !blocked.contains(action.getActor())) {
                Player target = action.getTarget();

                if (healed.contains(target)) {
                    saved = target;
                } else {
                    target.kill();
                    killed = target;
                }
            }
        }
        return new NightResult(killed, saved, healed, blocked, checks);
    }
}