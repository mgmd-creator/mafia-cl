package com.mafia;

import com.mafia.model.Game;
import com.mafia.model.GamePhase;
import com.mafia.model.JournalEntry;
import com.mafia.model.NightResult;
import com.mafia.model.Player;
import com.mafia.model.Team;
import com.mafia.model.Vote;
import com.mafia.model.VotingResult;
import com.mafia.model.role.Commissioner;
import com.mafia.model.role.Doctor;
import com.mafia.model.role.Lover;
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

/**
 * Точка входа в игру.
 */
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

            Player mafiaTarget = null;
            List<Player> aliveMafia = game.getAliveMafia();

            if (!aliveMafia.isEmpty()) {
                mafiaTarget = view.choosePlayer(
                        "Мафия, выберите жертву среди города: ",
                        game.getAliveCity(), null);

                for (Player mafia : aliveMafia) {
                    actions.add(mafia.getRole().perform(mafia, mafiaTarget));
                }

                addPrivate(journal, game, GamePhase.NIGHT,
                        "Вы выбрали жертву: " + mafiaTarget.getName(),
                        Set.copyOf(aliveMafia));
            }

            Player commissionerActor = null;
            Player commissionerTarget = null;
            Player doctorActor = null;
            Player doctorTarget = null;
            Player loverActor = null;
            Player loverTarget = null;

            for (Player player : game.getAlivePlayers()) {
                Role role = player.getRole();

                if (role instanceof Commissioner) {
                    commissionerActor = player;
                    commissionerTarget = view.choosePlayer("Комиссар " + player.getName() + ", кого проверить: ", game.getAlivePlayers(), player);
                    actions.add(role.perform(player, commissionerTarget));

                } else if (role instanceof Doctor) {
                    doctorActor = player;
                    doctorTarget = view.choosePlayer("Доктор " + player.getName() + ", кого лечить: ", game.getAlivePlayers(), null);
                    actions.add(role.perform(player, doctorTarget));

                } else if (role instanceof Lover) {
                    loverActor = player;
                    loverTarget = view.choosePlayer("Любовница " + player.getName() + ", кого заблокировать: ", game.getAlivePlayers(), player);
                    actions.add(role.perform(player, loverTarget));
                }
            }

            NightResult nightResult = nightResolver.resolve(game, actions);

            // УТРО
            game.setPhase(GamePhase.MORNING);
            view.print("\n=== УТРО " + game.getDay() + " ===");

            if (nightResult.hasKilled()) {
                String message = "Ночью погиб " + nightResult.getKilled().getName();
                view.print(message);
                addPublic(journal, game, GamePhase.MORNING, message);
            } else {
                view.print("Никто не погиб.");
                addPublic(journal, game, GamePhase.MORNING, "Этой ночью никто не погиб");
            }

            for (Map.Entry<Player, Team> check : nightResult.getChecks().entrySet()) {
                String message = "Комиссар проверил " + check.getKey().getName() + ": " + check.getValue();

                if (commissionerActor != null) {
                    addPrivate(journal, game, GamePhase.NIGHT, message, Set.of(commissionerActor));
                }
            }

            if (doctorActor != null && doctorTarget != null
                    && !nightResult.getBlocked().contains(doctorActor)) {
                addPrivate(journal, game, GamePhase.NIGHT,
                        "Вы лечили: " + doctorTarget.getName(),
                        Set.of(doctorActor));
            }

            if (loverActor != null && loverTarget != null
                    && !nightResult.getBlocked().contains(loverActor)) {
                addPrivate(journal, game, GamePhase.NIGHT,
                        "Вы заблокировали: " + loverTarget.getName(),
                        Set.of(loverActor));
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
            game.setPhase(GamePhase.DISCUSSION);
            view.readLine("Обсуждение. Нажмите Enter, чтобы перейти к голосованию...");

            // ГОЛОСОВАНИЕ
            game.setPhase(GamePhase.VOTING);
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

            if (votingResult.isTie()) {
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
            }

            // КАЗНЬ
            game.setPhase(GamePhase.EXECUTION);
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
        }

        view.print("\n=== ИГРА ОКОНЧЕНА ===");
        view.print("Победила команда: " + game.getWinner());

        for (Player player : game.getPlayers()) {
            view.print("\n=== ЖУРНАЛ: " + player.getName() + " ===");
            List<JournalEntry> entries = journal.getJournalFor(player);

            if (entries.isEmpty()) {
                view.print("Записей нет.");
            } else {
                for (JournalEntry entry : entries) {
                    view.print("День " + entry.getDay() + " [" + entry.getPhase() + "]: " + entry.getMessage());
                }
            }
        }
    }

    private static void addPublic(JournalService journal, Game game, GamePhase phase, String message) {
        journal.addEntry(new JournalEntry(game.getDay(), phase, message, Set.of()));
    }

    private static void addPrivate(JournalService journal, Game game, GamePhase phase,
                                   String message, Set<Player> visibleTo) {
        journal.addEntry(new JournalEntry(game.getDay(), phase, message, visibleTo));
    }
}