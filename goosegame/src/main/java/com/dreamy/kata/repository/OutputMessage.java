package com.dreamy.kata.repository;

import java.util.Arrays;
import java.util.function.Supplier;

public enum OutputMessage {
    UNKNOWN_COMMAND("%s: unknown command"),
    UNKNOWN_PLAYER("%s: not a player in this session"),
    ILLEGAL_ADD_COMMAND("%s: unsupported add command. Correct format is 'add <player>'"),
    ILLEGAL_MOVE_COMMAND("%s: unsupported move command. Correct format is 'move <player>' or 'move <player> <first dice roll>, <second dice roll>'"),
    ILLEGAL_DICE_ROLL("The user inserted something that wasn't a number in a move command with dice rolls. " +
            "Correct format: 'move <player> <first dice roll>, <second dice roll>', where dice rolls are integers between 1 and 6 included"),
    PLAYERS_LIST("players: %s"),
    DUPLICATED_PLAYER("%s: already existing player"),
    NORMAL_MOVE("%1$s rolls %2$d, %3$d. %1$s moves from %4$s to %5$s. "),
    BOUNCE("%1$s bounces! %1$s return to %2$s. "),
    BRIDGE("%1$s moves to %2$s. "),
    PRANK("On %1$s there's %2$s, who returns to %3$s. "),
    GOOSE("%1$s moves again and goes to %2$s. "),
    VICTORY("%1$s wins!");

    private String templateMessage;

    private OutputMessage(String templateMessage) {
        this.templateMessage = templateMessage;
    }

    @SafeVarargs
    public final <T> String formatSimpleMessage(T... formatParameters) {
        return String.format(templateMessage, (Object[]) formatParameters);
    }

    public <T> String formatComplexMessage(Supplier<T>[] formatParameters) {
        return String.format(templateMessage,
                Arrays.stream(formatParameters)
                        .map(Supplier::get)
                        .toArray());
    }
}
