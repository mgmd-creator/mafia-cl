package com.mafia.model.role;
import com.mafia.model.Team;
import java.util.Objects;
/**
 * Базовая роль игрока.
 */
public abstract class Role implements NightAction {

    private final String name;
    private final Team team;

    protected Role(String name, Team team) {
        this.name = Objects.requireNonNull(name, "Название роли не может быть null");
        this.team = Objects.requireNonNull(team, "Команда не может быть null");
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public String toString() {
        return name;
    }
}
