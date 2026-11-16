package com.mafia.model;

/**
 * Фаза партии.
 */
public enum GamePhase {
    NIGHT,
    MORNING,
    DISCUSSION,
    VOTING,
    EXECUTION;

    public GamePhase next() {
        return switch (this) {
            case NIGHT -> MORNING;
            case MORNING -> DISCUSSION;
            case DISCUSSION -> VOTING;
            case VOTING -> EXECUTION;
            case EXECUTION -> NIGHT;
        };
    }
}
