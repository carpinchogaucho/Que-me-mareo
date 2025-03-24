package org.carpincho.queMeMareo.MissUniverso.manager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
        playersItems.clear(); // Limpiar datos previos


        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {

                UUID playerUUID = player.getUniqueId();
                Location playerLocation = player.getLocation();


                BalanceItemManager balanceItemManager = new BalanceItemManager(playerUUID, playerLocation);
                playersItems.put(playerUUID, balanceItemManager);

                switch (round) {
                    case 1:
                        balanceItemManager.setBalanceAxis(BalanceItemManager.BalanceAxis.X);
                        balanceItemManager.setTiltSpeed(0.01 * currentRound);
                        break;
                    case 2:
                        balanceItemManager.setBalanceAxis(BalanceItemManager.BalanceAxis.Z);
                        balanceItemManager.setTiltSpeed(0.01 * currentRound);
                        break;
                    default:
                        balanceItemManager.setBalanceAxis(BalanceItemManager.BalanceAxis.X);
                        balanceItemManager.setTiltSpeed(0.02 * currentRound);
                        break;
                }

                player.sendMessage("¡La ronda " + currentRound + " ha comenzado!");
                balanceItemManager.disappear(false);
                balanceItemManager.startBalancing();
            }
        }

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

        playersItems.clear();
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
