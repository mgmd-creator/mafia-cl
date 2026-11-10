package com.mafia.service;

import com.mafia.model.GamePhase;
import com.mafia.model.JournalEntry;
import com.mafia.model.Player;
import com.mafia.model.role.Civilian;
import com.mafia.model.role.Mafia;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JournalServiceTest {

    private final Player anya = new Player("Аня", new Civilian());
    private final Player boris = new Player("Борис", new Mafia());
    private final JournalService service = new JournalService();

    @Test
    void serviceFiltersJournalByPlayer() {
        service.addEntry(new JournalEntry(
                1, GamePhase.MORNING, "Публичная запись", Set.of()
        ));
        service.addEntry(new JournalEntry(
                1, GamePhase.NIGHT, "Личная запись для Ани", Set.of(anya)
        ));

        List<JournalEntry> anyaJournal = service.getJournalFor(anya);
        List<JournalEntry> borisJournal = service.getJournalFor(boris);

        assertEquals(2, anyaJournal.size());
        assertEquals(1, borisJournal.size());
        assertEquals("Публичная запись", borisJournal.get(0).getMessage());
        assertEquals("Личная запись для Ани", anyaJournal.get(1).getMessage());
    }
}
