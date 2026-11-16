package com.mafia;

import com.mafia.model.Game;
import com.mafia.model.GamePhase;
import com.mafia.model.JournalEntry;
import com.mafia.model.NightResult;
import com.mafia.model.Player;
import com.mafia.model.Team;
import com.mafia.model.Vote;
import com.mafia.model.VotingResult;
import com.mafia.model.role.NightActionType;
import com.mafia.model.role.NightActionResult;
import com.mafia.model.role.Role;
import com.mafia.service.GameService;
import com.mafia.service.JournalService;
import com.mafia.service.NightResolver;
import com.mafia.service.VotingService;
import com.mafia.view.ConsoleView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        ConsoleView view = new ConsoleView();
        GameService gameService = new GameService();
        NightResolver nightResolver = new NightResolver();
        VotingService votingService = new VotingService();
        JournalService journal = new JournalService();

        view.printHeader();

        int playerCount = view.readInt("Сколько игроков? (минимум 5): ");
        while (playerCount < 5) {
            playerCount = view.readInt("Минимум 5 игроков. Сколько игроков? ");
        }

        List<String> names = new ArrayList<>();
        Set<String> usedNames = new HashSet<>();

        while (names.size() < playerCount) {
            String name = view.readLine("Имя игрока " + (names.size() + 1) + ": ");

            if (name == null || name.isBlank()) {
                view.print("Имя не может быть пустым.");
                continue;
            }
            if (!usedNames.add(name)) {
                view.print("Игрок с таким именем уже есть.");
                continue;
            }

            names.add(name);
        }

        Game game = gameService.createGame(names);

        view.print("\nРоли игроков:");
        for (Player player : game.getPlayers()) {
            view.print(player.getName() + " — " + player.getRole());
        }

        addMafiaTeammateJournal(journal, game);

        while (!game.isGameOver()) {
            Optional<Team> winner = gameService.checkWinner(game);
            if (winner.isPresent()) {
                game.setWinner(winner.get());
                break;
            }

            // НОЧЬ
            game.setPhase(GamePhase.NIGHT);
            view.print("\n=== НОЧЬ " + game.getDay() + " ===");
            view.print("Живых игроков: " + game.getAlivePlayers().size());

            List<NightActionResult> actions = new ArrayList<>();
            List<Player> aliveMafia = game.getAliveMafia();

            if (!aliveMafia.isEmpty()) {
                Player shooter = aliveMafia.get(0);
                Player target = view.choosePlayer(
                        "Мафия (" + shooter.getName() + "), выберите жертву среди города: ",
                        game.getAliveCity(), null);

                actions.add(shooter.getRole().perform(shooter, target));

                addPrivate(journal, game, GamePhase.NIGHT,
                        "Выстрел делает " + shooter.getName() + ", жертва: " + target.getName(),
                        Set.copyOf(aliveMafia));
            }

            for (Player player : game.getAlivePlayers()) {
                Role role = player.getRole();

                if (!role.hasNightAction()) {
                    continue;
                }

                Player target = view.choosePlayer(
                        player.getName() + ", " + role.getNightPrompt() + ": ",
                        game.getAlivePlayers(),
                        role.canTargetSelf() ? null : player);

                actions.add(role.perform(player, target));
            }

            NightResult nightResult = nightResolver.resolve(game, actions);

            // УТРО
            game.nextPhase();
            view.print("\n=== УТРО " + game.getDay() + " ===");

            if (nightResult.hasKilled()) {
                String message = "Ночью погиб " + nightResult.getKilled().getName();
                view.print(message);
                addPublic(journal, game, GamePhase.MORNING, message);
            } else {
                view.print("Никто не погиб.");
                addPublic(journal, game, GamePhase.MORNING, "Этой ночью никто не погиб");
            }

            for (NightActionResult action : actions) {
                Player actor = action.getActor();
                Player target = action.getTarget();

                if (nightResult.getBlocked().contains(actor)) {
                    continue;
                }

                if (action.getType() == NightActionType.CHECK) {
                    Team team = action.getCheckedTeam().orElseThrow();
                    addPrivate(journal, game, GamePhase.NIGHT,
                            "Вы проверили " + target.getName() + ": " + team,
                            Set.of(actor));
                } else if (action.getType() == NightActionType.HEAL) {
                    addPrivate(journal, game, GamePhase.NIGHT,
                            "Вы лечили: " + target.getName(),
                            Set.of(actor));
                } else if (action.getType() == NightActionType.BLOCK) {
                    addPrivate(journal, game, GamePhase.NIGHT,
                            "Вы заблокировали: " + target.getName(),
                            Set.of(actor));
                }
            }

            for (Player blocked : nightResult.getBlocked()) {
                addPrivate(journal, game, GamePhase.NIGHT,
                        "Ваше ночное действие было заблокировано",
                        Set.of(blocked));
            }

            winner = gameService.checkWinner(game);
            if (winner.isPresent()) {
                game.setWinner(winner.get());
                break;
            }

            // ОБСУЖДЕНИЕ
            game.nextPhase();
            view.readLine("Обсуждение. Нажмите Enter, чтобы перейти к голосованию...");

            // ГОЛОСОВАНИЕ
            game.nextPhase();
            view.print("\n=== ГОЛОСОВАНИЕ ===");

            List<Player> alive = game.getAlivePlayers();
            List<Vote> votes = new ArrayList<>();

            for (Player voter : alive) {
                Player candidate = view.choosePlayer(
                        voter.getName() + ", за кого голосуете: ",
                        alive, voter);
                votes.add(new Vote(voter, candidate));
            }

            VotingResult votingResult = votingService.vote(votes, alive);
            addPublic(journal, game, GamePhase.VOTING,
                    "Голосование: " + formatCounts(votingResult.getCounts()));

            if (votingResult.isTie()) {
                addPublic(journal, game, GamePhase.VOTING,
                        "Ничья. Переголосование между: " + names(votingResult.getLeaders()));

                view.print("Ничья. Переголосование между:");
                for (Player leader : votingResult.getLeaders()) {
                    view.print("- " + leader.getName());
                }

                List<Vote> revotes = new ArrayList<>();

                for (Player voter : alive) {
                    Player candidate = view.choosePlayer(
                            voter.getName() + ", переголосование: ",
                            votingResult.getLeaders(), voter);
                    revotes.add(new Vote(voter, candidate));
                }

                votingResult = votingService.vote(revotes, votingResult.getLeaders());
                addPublic(journal, game, GamePhase.VOTING,
                        "Переголосование: " + formatCounts(votingResult.getCounts()));
            }

            // КАЗНЬ
            game.nextPhase();
            view.print("\n=== КАЗНЬ ===");

            if (votingResult.hasExecuted()) {
                Player executed = votingResult.getExecuted();
                executed.kill();
                String message = "Казнён: " + executed.getName();
                view.print(message);
                addPublic(journal, game, GamePhase.EXECUTION, message);
            } else {
                view.print("Никого не казнили.");
                addPublic(journal, game, GamePhase.EXECUTION, "Никого не казнили");
            }

            winner = gameService.checkWinner(game);
            if (winner.isPresent()) {
                game.setWinner(winner.get());
                break;
            }

            game.nextDay();
            game.nextPhase();
        }

        view.print("\n=== ИГРА ОКОНЧЕНА ===");
        view.print("Победила команда: " + game.getWinner());

        for (Player player : game.getPlayers()) {
            view.print("\n=== ЖУРНАЛ: " + player.getName() + " ===");
            List<JournalEntry> entries = journal.getJournalFor(player);

            if (entries.isEmpty()) {
                view.print("Записей нет.");
                continue;
            }

            for (JournalEntry entry : entries) {
                view.print("День " + entry.getDay() + " [" + entry.getPhase() + "]: " + entry.getMessage());
            }
        }
    }

    private static void addMafiaTeammateJournal(JournalService journal, Game game) {
        List<Player> mafia = game.getAliveMafia();

        if (mafia.size() < 2) {
            return;
        }

        for (Player player : mafia) {
            List<Player> teammates = new ArrayList<>();

            for (Player other : mafia) {
                if (other != player) {
                    teammates.add(other);
                }
            }

            addPrivate(journal, game, GamePhase.NIGHT,
                    "Ваши напарники: " + names(teammates),
                    Set.of(player));
        }
    }

    private static String formatCounts(Map<Player, Integer> counts) {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<Player, Integer> entry : counts.entrySet()) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(entry.getKey().getName()).append(": ").append(entry.getValue());
        }

        return result.toString();
    }

    private static String names(List<Player> players) {
        StringBuilder result = new StringBuilder();

        for (Player player : players) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(player.getName());
        }

        return result.toString();
    }

    private static void addPublic(JournalService journal, Game game, GamePhase phase, String message) {
        journal.addEntry(new JournalEntry(game.getDay(), phase, message, Set.of()));
    }

    private static void addPrivate(JournalService journal, Game game, GamePhase phase,
                                   String message, Set<Player> visibleTo) {
        journal.addEntry(new JournalEntry(game.getDay(), phase, message, visibleTo));
    }
}
