package com.mafia.service;

import com.mafia.model.Player;
import com.mafia.model.Vote;
import com.mafia.model.VotingResult;

import java.util.*;

/**
 * Считает голоса и определяет итог голосования.
 */
public class VotingService {

    public VotingResult vote(List<Vote> votes, List<Player> candidates) {
        Map<Player, Integer> counts = new HashMap<>();
        for (Player candidate : candidates) {
            counts.put(candidate, 0);
        }

        Set<Player> voters = new HashSet<>();
        for (Vote vote : votes) {
            if (!voters.add(vote.getVoter())) {
                throw new IllegalArgumentException("Игрок уже голосовал");
            }
            if (!counts.containsKey(vote.getCandidate())) {
                throw new IllegalArgumentException("Кандидат не участвует в голосовании");
            }
            counts.put(vote.getCandidate(), counts.get(vote.getCandidate()) + 1);
        }

        int max = 0;
        for (int count : counts.values()) {
            if (count > max) {
                max = count;
            }
        }

        if (max == 0) {
            return new VotingResult(null, List.of(), false, counts);
        }

        List<Player> leaders = new ArrayList<>();
        for (Player candidate : candidates) {
            if (counts.get(candidate) == max) {
                leaders.add(candidate);
            }
        }

        if (leaders.size() == 1) {
            return new VotingResult(leaders.get(0), leaders, false, counts);
        }

        return new VotingResult(null, leaders, true, counts);


    }
}
