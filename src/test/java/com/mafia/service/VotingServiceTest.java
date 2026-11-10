package com.mafia.service;

import com.mafia.model.Player;
import com.mafia.model.Vote;
import com.mafia.model.VotingResult;
import com.mafia.model.role.Civilian;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VotingServiceTest {

    private final Player anya = new Player("Аня", new Civilian());
    private final Player boris = new Player("Борис", new Civilian());
    private final Player vera = new Player("Вера", new Civilian());
    private final List<Player> candidates = List.of(anya, boris, vera);
    private final VotingService service = new VotingService();

    @Test
    void majorityWins() {
        VotingResult result = service.vote(List.of(
                new Vote(anya, boris),
                new Vote(boris, anya),
                new Vote(vera, anya)
        ), candidates);

        assertEquals(anya, result.getExecuted());
        assertFalse(result.isTie());
    }

    @Test
    void tieReturnsLeaders() {
        VotingResult result = service.vote(List.of(
                new Vote(anya, boris),
                new Vote(boris, anya)
        ), candidates);

        assertTrue(result.isTie());
        assertNull(result.getExecuted());
        assertEquals(2, result.getLeaders().size());
    }

    @Test
    void revoteResolvesTie() {
        VotingResult first = service.vote(List.of(
                new Vote(anya, boris),
                new Vote(boris, anya)
        ), candidates);

        assertTrue(first.isTie());

        VotingResult second = service.vote(List.of(
                new Vote(anya, boris),
                new Vote(boris, anya),
                new Vote(vera, boris)
        ), first.getLeaders());

        assertEquals(boris, second.getExecuted());
        assertFalse(second.isTie());
    }
}
