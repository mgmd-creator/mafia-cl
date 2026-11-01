package com.mafia.model;

import com.mafia.model.role.Role;

import java.util.Objects;

public class Player {

    private final String name;
    private final Role role;
    private final PlayerKnowledge knowledge;
    private boolean alive;

    public Player(String name, Role role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя игрока не может быть пустым");
        }
        this.name = name;
        this.role = Objects.requireNonNull(role, "Роль игрока не может быть null");
        this.knowledge = new PlayerKnowledge();
        this.alive = true;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public PlayerKnowledge getKnowledge() {
        return knowledge;
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isMafia() {
        return role.getTeam() == Team.MAFIA;
    }

    public void kill() {
        this.alive = false;
    }

    @Override
    public String toString() {
        return name;
    }
}