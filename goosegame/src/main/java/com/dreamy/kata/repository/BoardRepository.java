package com.dreamy.kata.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BoardRepository {
    private Map<String, Integer> boardPlacements;
    private Map<Integer, String> specialSpaces;

    public BoardRepository() {
        boardPlacements = new HashMap<>();
        specialSpaces = new HashMap<>();
        specialSpaces.put(0, "Start");
        specialSpaces.put(5, "The Goose");
        specialSpaces.put(9, "The Goose");
        specialSpaces.put(14, "The Goose");
        specialSpaces.put(18, "The Goose");
        specialSpaces.put(23, "The Goose");
        specialSpaces.put(27, "The Goose");
        specialSpaces.put(6, "The Bridge");
    }

    public Integer addPlayer(String player) {
        return boardPlacements.putIfAbsent(player, 0);
    }

    public Integer getPlayer(String player) {
        return boardPlacements.get(player);
    }

    public List<String> getPlayersInState(Integer state) {
        return boardPlacements.entrySet().stream()
                .filter(e -> e.getValue().equals(state))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Integer updatePlayer(String player, int position) {
        return boardPlacements.replace(player, position);
    }

    public boolean removePlayer(String player) {
        return boardPlacements.remove(player) != null;
    }

    public String getSpace(Integer spaceNumber) {
        String spaceName = specialSpaces.get(spaceNumber);
        return spaceName == null ? spaceNumber.toString() :
                                   spaceName.equals("Start") ? spaceName
                                                             : String.format("%s, %s", spaceNumber, spaceName);
    }
}
