package com.dreamy.kata.service;

import com.dreamy.kata.controller.GameController;
import com.dreamy.kata.repository.BoardRepository;
import com.dreamy.kata.repository.PlayerRepository;

import java.util.List;

import static com.dreamy.kata.repository.OutputMessage.BOUNCE;
import static com.dreamy.kata.repository.OutputMessage.BRIDGE;
import static com.dreamy.kata.repository.OutputMessage.DUPLICATED_PLAYER;
import static com.dreamy.kata.repository.OutputMessage.GOOSE;
import static com.dreamy.kata.repository.OutputMessage.ILLEGAL_ADD_COMMAND;
import static com.dreamy.kata.repository.OutputMessage.ILLEGAL_DICE_ROLL;
import static com.dreamy.kata.repository.OutputMessage.ILLEGAL_MOVE_COMMAND;
import static com.dreamy.kata.repository.OutputMessage.NORMAL_MOVE;
import static com.dreamy.kata.repository.OutputMessage.PLAYERS_LIST;
import static com.dreamy.kata.repository.OutputMessage.PRANK;
import static com.dreamy.kata.repository.OutputMessage.UNKNOWN_COMMAND;
import static com.dreamy.kata.repository.OutputMessage.UNKNOWN_PLAYER;
import static com.dreamy.kata.repository.OutputMessage.VICTORY;

public class GameService {
    private static final int maxStatus = 63;
    private final PlayerRepository playerRepository;
    private final BoardRepository boardRepository;

    public GameService() {
        this.playerRepository = new PlayerRepository();
        this.boardRepository = new BoardRepository();
    }

    public GameService(PlayerRepository playerRepository, BoardRepository boardRepository) {
        this.playerRepository = playerRepository;
        this.boardRepository = boardRepository;
    }

    public String handleUnknownCommand(String command) {
        return UNKNOWN_COMMAND.formatSimpleMessage(command);
    }

    public String handleIllegalAddCommand(String command) {
        return ILLEGAL_ADD_COMMAND.formatSimpleMessage(command);
    }

    public String handleIllegalMoveCommand(String command) {
        return ILLEGAL_MOVE_COMMAND.formatSimpleMessage(command);
    }

    public String handleIllegalRoll() {
        return ILLEGAL_DICE_ROLL.formatSimpleMessage((Object) null);
    }

    public String addPlayer(String player) {
        if (playerRepository.addPlayer(player)) {
            boardRepository.addPlayer(player);
            String players = playerRepository.getPlayers()
                    .stream()
                    .reduce((first, second) -> first + ", " + second)
                    .orElse("");
            return PLAYERS_LIST.formatSimpleMessage(players);
        } else {
            return DUPLICATED_PLAYER.formatSimpleMessage(player);
        }
    }

    public String movePlayer(String player, int firstDiceRoll, int secondDiceRoll) {
        if (!playerRepository.containsPlayer(player)) {
            return UNKNOWN_PLAYER.formatSimpleMessage(player);
        }
        Integer previousStatus = boardRepository.getPlayer(player);

        assert previousStatus != null;

        int supposedStatus = previousStatus + firstDiceRoll + secondDiceRoll;
        if (supposedStatus > maxStatus) {
            int bounce = supposedStatus - maxStatus;
            boardRepository.updatePlayer(player, maxStatus - bounce);
            String newSpace = boardRepository.getSpace(maxStatus - bounce);
            String move = NORMAL_MOVE.formatSimpleMessage(player, firstDiceRoll, secondDiceRoll, previousStatus, maxStatus);
            String bouncingMove = move + BOUNCE.formatSimpleMessage(player, newSpace);
            String specialMove = handleSpecialSpace(player, newSpace, firstDiceRoll, secondDiceRoll, bouncingMove);
            return handlePrank(player, previousStatus, boardRepository.getPlayer(player), specialMove);
        } else if (supposedStatus == maxStatus) {
            boardRepository.updatePlayer(player, maxStatus);
            String move = NORMAL_MOVE.formatSimpleMessage(player, firstDiceRoll, secondDiceRoll, previousStatus, maxStatus);
            GameController.gameWon();
            return move + VICTORY.formatSimpleMessage(player);
        } else {
            boardRepository.updatePlayer(player, supposedStatus);
            String newSpace = boardRepository.getSpace(supposedStatus);
            String moveString = NORMAL_MOVE.formatSimpleMessage(player, firstDiceRoll, secondDiceRoll, previousStatus, newSpace);
            String specialSpaceString = handleSpecialSpace(player, newSpace, firstDiceRoll, secondDiceRoll, moveString);
            return handlePrank(player, previousStatus, supposedStatus, specialSpaceString);
        }
    }

    private String handleSpecialSpace(String player, String spaceName, int firstDiceRoll, int secondDiceRoll, String actualReturnString) {
        if (spaceName.contains("The Bridge")) {
            boardRepository.updatePlayer(player, 12);
            return actualReturnString + BRIDGE.formatSimpleMessage(player, 12);
        } else if (spaceName.contains("The Goose")) {
            int previousStatus = boardRepository.getPlayer(player);
            int supposedStatus = previousStatus + firstDiceRoll + secondDiceRoll;
            if (supposedStatus > maxStatus) {
                int bounce = supposedStatus - maxStatus;
                boardRepository.updatePlayer(player, maxStatus - bounce);
                String jumpString = actualReturnString + GOOSE.formatSimpleMessage(player, maxStatus);
                String newSpace = boardRepository.getSpace(maxStatus - bounce);
                String bounceString = jumpString + BOUNCE.formatSimpleMessage(player, newSpace);
                return handleSpecialSpace(player, newSpace, firstDiceRoll, secondDiceRoll, bounceString);
            } else if (supposedStatus == maxStatus) {
                boardRepository.updatePlayer(player, maxStatus);
                String jumpString = actualReturnString + GOOSE.formatSimpleMessage(player, maxStatus);
                String returnString = jumpString + VICTORY.formatSimpleMessage(player);
                GameController.gameWon();
                return returnString;
            } else {
                boardRepository.updatePlayer(player, supposedStatus);
                String newSpace = boardRepository.getSpace(supposedStatus);
                String jumpString = actualReturnString + GOOSE.formatSimpleMessage(player, newSpace);
                return handleSpecialSpace(player, newSpace, firstDiceRoll, secondDiceRoll, jumpString);
            }
        } else {
            return actualReturnString;
        }
    }

    private String handlePrank(String player, int previousStatus, int supposedStatus, String actualReturnString) {
        List<String> players = boardRepository.getPlayersInState(supposedStatus);
        players.remove(player);
        if (!players.isEmpty()) {
            StringBuilder returnString = new StringBuilder(actualReturnString);
            for (String prankedPlayer : players) {
                boardRepository.updatePlayer(prankedPlayer, previousStatus);
                returnString.append(PRANK.formatSimpleMessage(supposedStatus, prankedPlayer, previousStatus));
            }
            return returnString.toString();
        }
        return actualReturnString;
    }

}
