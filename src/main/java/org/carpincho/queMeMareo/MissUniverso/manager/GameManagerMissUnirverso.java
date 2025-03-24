package org.carpincho.queMeMareo.MissUniverso.manager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.carpincho.queMeMareo.QueMeMareo;

import java.util.HashMap;
import java.util.UUID;

public class GameManagerMissUnirverso {
    private final QueMeMareo plugin;
    private int currentRound;
    private final HashMap<UUID, BalanceItemManager> playersItems;
    private final HashMap<UUID, Integer> playerScore;
    private boolean playing;

    public GameManagerMissUnirverso(QueMeMareo plugin) {
        this.plugin = plugin;
        this.currentRound = 0;
        this.playersItems = new HashMap<>();
        this.playerScore = new HashMap<>();
        this.playing = false;
    }

    public HashMap<UUID, Integer> getPlayerScore() {
        return playerScore;
    }

    public void registerPlayer(Player player, BalanceItemManager item) {
        playersItems.put(player.getUniqueId(), item);
    }

    public void unregisterPlayer(Player player) {
        playersItems.remove(player.getUniqueId());
    }

    public BalanceItemManager getBalancingItem(Player player) {
        return playersItems.get(player.getUniqueId());
    }

    public void start(int round) {
        playing = true;
        currentRound = round;

        switch (round) {
            case 1:
                playersItems.values().forEach(item -> {
                    item.setBalanceAxis(BalanceItemManager.BalanceAxis.X);
                    item.setTiltSpeed(0.01 * currentRound);
                });
                break;
            case 2:
                playersItems.values().forEach(item -> {
                    item.setBalanceAxis(BalanceItemManager.BalanceAxis.Z);
                    item.setTiltSpeed(0.01 * currentRound);
                });
                break;
            default:
                playersItems.values().forEach(item -> {
                    item.setBalanceAxis(BalanceItemManager.BalanceAxis.X);
                    item.setTiltSpeed(0.02 * currentRound);
                });
                break;
        }

        playersItems.forEach((uuid, balanceItemManager) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;

            p.sendMessage("¡La ronda " + currentRound + " ha comenzado!");
            balanceItemManager.disappear(false);
            balanceItemManager.startBalancing();
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(plugin, 20 * 60);
    }

    public void stop() {
        playing = false;

        playersItems.forEach((uuid, balanceItemManager) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (p == null) return;

            p.sendMessage("¡La ronda " + currentRound + " ha terminado!");
            balanceItemManager.reset();
        });
    }

    public boolean isPlaying() {
        return playing;
    }

    public void onBookFall(Player player) {
        if (!playersItems.containsKey(player.getUniqueId())) return;

        player.sendMessage("¡Perdiste el libro! Pasaste al modo espectador.");
        player.setGameMode(GameMode.SPECTATOR);


        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restarPuntos " + player.getName());
    }
}
