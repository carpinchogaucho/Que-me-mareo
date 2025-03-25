package org.carpincho.queMeMareo.Manager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class PlayerManager {
    private final JavaPlugin plugin;
    private final Set<Player> activePlayers = new HashSet<>();

    public PlayerManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToGame(Player player) {
        if ((player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE)
                && !activePlayers.contains(player)) {
            activePlayers.add(player);
            Bukkit.getLogger().info("Jugador añadido al juego: " + player.getName());
        }
    }

    public void removePlayerFromGame(Player player) {
        if (activePlayers.contains(player)) {
            activePlayers.remove(player);
            Bukkit.getLogger().info("Jugador eliminado del juego: " + player.getName());
        }
    }

    public Set<Player> getActivePlayers() {
        return new HashSet<>(activePlayers);
    }

    public boolean isPlayerInGame(Player player) {
        return activePlayers.contains(player);
    }

    public void clearPlayers() {
        activePlayers.clear();
    }
}
