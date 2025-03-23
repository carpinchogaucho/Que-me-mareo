package org.carpincho.queMeMareo.MissUniverso.commandMissUniverso;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.carpincho.queMeMareo.MissUniverso.manager.BalanceItemManager;
import org.carpincho.queMeMareo.MissUniverso.manager.GameManagerMissUnirverso;
import org.carpincho.queMeMareo.QueMeMareo;


public class BalanceRegisterCommand implements CommandExecutor {
    private final QueMeMareo plugin;

    public BalanceRegisterCommand(QueMeMareo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /balance-register <player/*>

        GameManagerMissUnirverso GameManagerMissUnirverso = plugin.getGameManagerMissUnirverso();
        String target = args[0];

        if (target.equalsIgnoreCase("*")) {

            Bukkit.getOnlinePlayers().forEach(player -> {
                GameManagerMissUnirverso.registerPlayer(player, new BalanceItemManager(player.getUniqueId(), player.getLocation()));
            });

            sender.sendMessage("All players registered (OPs not registered)");

        } else {
            Player player = Bukkit.getPlayer(target);
            if (player == null) {
                sender.sendMessage("Player not found");
                return true;
            }
            GameManagerMissUnirverso.registerPlayer(player, new BalanceItemManager(player.getUniqueId(), player.getLocation()));

            sender.sendMessage("Player registered (OP ignored)");
        }

        return true;
    }
}
