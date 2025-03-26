package org.carpincho.queMeMareo.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.carpincho.queMeMareo.Manager.GameManager;
import org.carpincho.queMeMareo.Manager.ItemDisplayManager;
import org.carpincho.queMeMareo.QueMeMareo;

public class PlayerListener implements Listener {

    private final QueMeMareo plugin;

    public PlayerListener(QueMeMareo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (hasCollidedWithObstacle(player)) {
            GameManager.getInstance(plugin).freezePlayer(player);
        }

        if (isNearEye(player)) {
            GameManager.getInstance(plugin).playerCompletedLap(player);
        }
    }

    private boolean hasCollidedWithObstacle(Player player) {
        for (ItemDisplayManager obstacle : GameManager.getInstance(plugin).getObstacles()) {
            if (player.getLocation().distance(obstacle.getLocation()) < 1.0) {
                return true;
            }
        }
        return false;
    }

    private boolean isNearEye(Player player) {

        ItemDisplayManager eye = GameManager.getInstance(plugin).getEye();


        if (eye == null) {
            Bukkit.getLogger().warning("⚠️ eye es null en isNearEye()");
            return false;
        }

        Location eyeLocation = eye.getLocation();
        double distance = player.getLocation().distanceSquared(eyeLocation);
        return distance <= 100;
    }
}