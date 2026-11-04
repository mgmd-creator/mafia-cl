package com.mafia.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class VotingResult {

    private final Player executed;
    private final List<Player> leaders;
    private final boolean tie;
    private final Map<Player, Integer> counts;

    public VotingResult(Player executed, List<Player> leaders, boolean tie, Map<Player, Integer> counts) {
        this.executed = executed;
        this.leaders = List.copyOf(Objects.requireNonNull(leaders));
        this.tie = tie;
        this.counts = Map.copyOf(Objects.requireNonNull(counts));
    }

    public Player getExecuted() {
        return executed;
    }

    public List<Player> getLeaders() {
        return leaders;
    }

    public boolean isTie() {
        return tie;
    }

    public Map<Player, Integer> getCounts() {
        return counts;
    }

    public boolean hasExecuted() {
        return executed != null;
    }
}
