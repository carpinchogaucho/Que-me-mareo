package org.carpincho.queMeMareo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.carpincho.queMeMareo.Manager.GameManager;


public class ClearItemsCommand implements CommandExecutor {

    private final GameManager gameManager;

    public ClearItemsCommand(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("queMeMareo.clearitems")) {
                player.sendMessage("§cNo tienes permiso para usar este comando.");
                return true;
            }
        }

        gameManager.stopGame();
        sender.sendMessage("§aTodos los obstáculos y ojos han sido eliminados.");
        return true;
    }
}
