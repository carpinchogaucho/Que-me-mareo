package org.carpincho.queMeMareo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.carpincho.queMeMareo.Manager.GameManager;

public class StopGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("queMeMareo.stop")) {
            GameManager.getInstance().stopGame();
            return true;
        }
        sender.sendMessage("No tienes permisos para ejecutar este comando.");
        return false;
    }
}
