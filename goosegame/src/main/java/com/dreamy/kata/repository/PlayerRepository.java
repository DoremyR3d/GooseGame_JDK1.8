package com.dreamy.kata.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlayerRepository {
    private Set<String> players;

    public PlayerRepository() {
        this.players = new HashSet<>();
    }

    public List<String> getPlayers() {
        return Collections.unmodifiableList(new ArrayList<>(players));
    }

    public boolean containsPlayer(String player) {
        return players.contains(player);
    }

    public boolean addPlayer(String player) {
        return players.add(player);
    }

    public boolean removePlayer(String player) {
        return players.remove(player);
    }

    public void removeAllPlayers() {
        players.clear();
    }
}
