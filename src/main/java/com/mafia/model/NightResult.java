package com.mafia.model;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Итог одной ночи.
 */
public class NightResult {

    private final Player killed;
    private final Player saved;
    private final Set<Player> healed;
    private final Set<Player> blocked;
    private final Map<Player, Team> checks;

    public NightResult(Player killed, Player saved, Set<Player> healed, Set<Player> blocked, Map<Player, Team> checks) {
        this.killed = killed;
        this.saved = saved;
        this.healed = Set.copyOf(Objects.requireNonNull(healed));
        this.blocked = Set.copyOf(Objects.requireNonNull(blocked));
        this.checks = Map.copyOf(Objects.requireNonNull(checks));
    }

    public Player getKilled() {
        return killed;
    }

    public Player getSaved() {
        return saved;
    }

    public Set<Player> getHealed() {
        return healed;
    }

    public Set<Player> getBlocked() {
        return blocked;
    }

    public Map<Player, Team> getChecks() {
        return checks;
    }

    public boolean hasKilled() {
        return killed != null;
    }

    public boolean hasSaved() {
        return saved != null;
    }
}
