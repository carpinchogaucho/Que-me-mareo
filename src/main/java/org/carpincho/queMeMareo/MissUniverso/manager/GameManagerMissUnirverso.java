package org.carpincho.queMeMareo.MissUniverso.manager;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.carpincho.queMeMareo.QueMeMareo;

import java.util.HashMap;
import java.util.UUID;

public class GameManagerMissUnirverso {
    private final QueMeMareo plugin;
    private int currentRound;
    private final HashMap<UUID, BalanceItemManager> playersItems;
    private final HashMap<UUID, Integer> playerScore;
    private boolean playing;
    private BukkitTask currentTask;

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

        if (currentTask != null && !currentTask.isCancelled()) {
            currentTask.cancel();
        }

        playersItems.clear();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                UUID playerUUID = player.getUniqueId();
                Location playerLocation = player.getLocation();


                BalanceItemManager balanceItemManager = new BalanceItemManager(playerUUID, playerLocation, round);
                playersItems.put(playerUUID, balanceItemManager);


                switch (round) {
                    case 1:
                        balanceItemManager.setBalanceAxis(BalanceItemManager.BalanceAxis.X);
                        balanceItemManager.setTiltSpeed(0.01 * currentRound);
                        balanceItemManager.SetCustomModelData(1007);
                        break;
                    case 2:
                        balanceItemManager.setBalanceAxis(BalanceItemManager.BalanceAxis.Z);
                        balanceItemManager.setTiltSpeed(0.01 * currentRound);
                        balanceItemManager.SetCustomModelData(1008);
                        break;
                    case 3:
                        balanceItemManager.setBalanceAxis(BalanceItemManager.BalanceAxis.X);
                        balanceItemManager.setTiltSpeed(0.02 * currentRound);
                        balanceItemManager.SetCustomModelData(1009);
                        break;
                    default:
                        balanceItemManager.setBalanceAxis(BalanceItemManager.BalanceAxis.X);
                        balanceItemManager.setTiltSpeed(0.02 * currentRound);
                        balanceItemManager.SetCustomModelData(1009);
                        break;
                }


                balanceItemManager.disappear(false);
                balanceItemManager.startBalancing();
            }
        }



        currentTask = new BukkitRunnable() {
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

            p.sendActionBar("¡La ronda " + currentRound + " ha terminado!");
            balanceItemManager.reset();
        });

        playersItems.clear();
    }

    public boolean isPlaying() {
        return playing;
    }


    public void onBookFall(Player player) {
        if (!playersItems.containsKey(player.getUniqueId())) return;

        player.sendActionBar("§c¡Perdiste el libro!");



        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restarPuntos " + player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playglow " + player.getName() + " red 1 3 1 50 75");
    }
}
