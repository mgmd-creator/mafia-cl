package com.mafia.view;

import com.mafia.model.Player;

import java.util.List;
import java.util.Scanner;

/**
 * Консольный ввод и вывод.
 */
public class ConsoleView {

    private final Scanner scanner = new Scanner(System.in);

    public void printHeader() {
        print("=== ИГРА «МАФИЯ» ===");
    }

    public void print(String message) {
        System.out.println(message);
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine();

            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                print("Введите число.");
            }
        }
    }

    public Player choosePlayer(String prompt, List<Player> candidates, Player actor) {
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException("Нет доступных игроков");
        }

        while (true) {
            print("Доступные игроки:");
            for (int i = 0; i < candidates.size(); i++) {
                print((i + 1) + ". " + candidates.get(i).getName());
            }

            int choice = readInt(prompt);

            if (choice < 1 || choice > candidates.size()) {
                print("Неверный номер.");
                continue;
            }

            Player selected = candidates.get(choice - 1);

            if (actor != null && selected == actor) {
                print("Нельзя выбрать себя.");
                continue;
            }

            return selected;
        }
    }
}