package com.dreamy.kata.controller;

import com.dreamy.kata.Application;
import com.dreamy.kata.service.GameService;

import java.util.Random;

public class GameController {

    private GameService gameService;
    private static volatile boolean winningMove = false;
    private Random random = new Random();

    public GameController() {
        this.gameService = new GameService();
    }

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    public String parseCommand(String command) {
        String[] tokenizedCommand = command.split(" ");
        switch (tokenizedCommand[0].trim().toUpperCase()) {
            case "ADD":
                return parseAddCommand(command, tokenizedCommand);
            case "MOVE":
                String returnStatus = parseMoveCommand(command, tokenizedCommand);
                if (winningMove) {
                    Application.turnOff();
                }
                return returnStatus;
            case "EXIT":
                Application.turnOff();
                return "Closing the goose game";
            default:
                return gameService.handleUnknownCommand(tokenizedCommand[0].trim());
        }
    }

    private String parseAddCommand(String command, String[] tokenizedCommand) {
        if (tokenizedCommand.length < 2) {
            return gameService.handleIllegalAddCommand(command);
        }
        if (tokenizedCommand[1].equalsIgnoreCase("PLAYER")) {
            if (tokenizedCommand.length < 3) {
                return gameService.handleIllegalAddCommand(command);
            }
            return gameService.addPlayer(tokenizedCommand[2].trim());
        } else {
            return gameService.handleIllegalAddCommand(command);
        }
    }

    private String parseMoveCommand(String command, String[] tokenizedCommand) {
        if (tokenizedCommand.length == 2) {
            int firstDiceRoll = random.nextInt(6) + 1;
            int secondDiceRoll = random.nextInt(6) + 1;
            return gameService.movePlayer(tokenizedCommand[1].trim(), firstDiceRoll, secondDiceRoll);
        } else if (tokenizedCommand.length == 4) {
            try {
                int firstDiceRoll = Integer.parseInt(tokenizedCommand[2].replace(",", "").trim());
                int secondDiceRoll = Integer.parseInt(tokenizedCommand[3].trim());
                if (firstDiceRoll < 1 || firstDiceRoll > 6 || secondDiceRoll < 1 || secondDiceRoll > 6) {
                    return gameService.handleIllegalRoll();
                }
                return gameService.movePlayer(tokenizedCommand[1].trim(), firstDiceRoll, secondDiceRoll);
            } catch (NumberFormatException e) {
                return gameService.handleIllegalRoll();
            }
        }
        return gameService.handleIllegalMoveCommand(command);
    }

    public static void gameWon() {
        winningMove = true;
    }
}
