package com.mafia.model;

import java.util.Objects;

public class Vote {

    private final Player voter;
    private final Player candidate;

    public Vote(Player voter, Player candidate) {
        this.voter = Objects.requireNonNull(voter, "Голосующий не может быть null");
        this.candidate = Objects.requireNonNull(candidate, "Кандидат не может быть null");
    }

    public Player getVoter() {
        return voter;
    }

    public Player getCandidate() {
        return candidate;
    }
}
