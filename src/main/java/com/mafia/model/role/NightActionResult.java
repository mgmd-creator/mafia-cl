package com.mafia.model.role;

import com.mafia.model.Player;
import com.mafia.model.Team;

import java.util.Objects;
import java.util.Optional;

/**
 * Результат одного ночного действия.
 */
public class NightActionResult {

    private final NightActionType type;
    private final Player actor;
    private final Player target;
    private final Team checkedTeam;

    private NightActionResult(NightActionType type, Player actor, Player target, Team checkedTeam) {
        this.type = Objects.requireNonNull(type, "Тип действия не может быть null");
        this.actor = Objects.requireNonNull(actor, "Исполнитель не может быть null");

        if (type != NightActionType.NONE && target == null) {
            throw new IllegalArgumentException("Для действия " + type + " нужна цель");
        }
        if (type == NightActionType.CHECK && checkedTeam == null) {
            throw new IllegalArgumentException("Для проверки нужна команда");
        }

        this.target = target;
        this.checkedTeam = checkedTeam;
    }

    public static NightActionResult none(Player actor) {
        return new NightActionResult(NightActionType.NONE, actor, null, null);
    }

    public static NightActionResult kill(Player actor, Player target) {
        return new NightActionResult(NightActionType.KILL, actor, target, null);
    }

    public static NightActionResult heal(Player actor, Player target) {
        return new NightActionResult(NightActionType.HEAL, actor, target, null);
    }

    public static NightActionResult block(Player actor, Player target) {
        return new NightActionResult(NightActionType.BLOCK, actor, target, null);
    }

    public static NightActionResult check(Player actor, Player target, Team checkedTeam) {
        return new NightActionResult(NightActionType.CHECK, actor, target, checkedTeam);
    }

    public NightActionType getType() {
        return type;
    }

    public Player getActor() {
        return actor;
    }

    public Player getTarget() {
        return target;
    }

    public Optional<Team> getCheckedTeam() {
        return Optional.ofNullable(checkedTeam);
    }
}
