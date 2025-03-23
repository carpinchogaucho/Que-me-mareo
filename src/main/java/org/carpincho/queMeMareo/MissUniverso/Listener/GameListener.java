package org.carpincho.queMeMareo.MissUniverso.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.carpincho.queMeMareo.MissUniverso.manager.BalanceItemManager;
import org.carpincho.queMeMareo.QueMeMareo;

public class GameListener implements Listener {
    private final QueMeMareo plugin;

    public GameListener(QueMeMareo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        BalanceItemManager item = plugin.getGameManagerMissUnirverso().getBalancingItem(player);

        if (item != null) {

            if (!plugin.getGameManagerMissUnirverso().isPlaying()) return;

            BalanceItemManager.BalanceAxis axis = item.getBalanceAxis();
            double adjustment = 0.0;
            double adjustmentFactor = 0.3;

            if (axis == BalanceItemManager.BalanceAxis.X) {
                double deltaX = event.getTo().getX() - event.getFrom().getX();
                adjustment = deltaX * adjustmentFactor;
            } else if (axis == BalanceItemManager.BalanceAxis.Z) {
                double deltaZ = event.getTo().getZ() - event.getFrom().getZ();
                adjustment = deltaZ * adjustmentFactor;
            }

            item.adjustTilt(adjustment);
        }
    }
}
