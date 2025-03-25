package org.carpincho.queMeMareo.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.carpincho.queMeMareo.Manager.GameManager;
import org.carpincho.queMeMareo.QueMeMareo;

public class StopGameCommand implements CommandExecutor {

    private final QueMeMareo plugin;

    public StopGameCommand(QueMeMareo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.hasPermission("queMeMareo.stop") || sender instanceof ConsoleCommandSender) {
            GameManager.getInstance(plugin).stopGame();
            return true;
        }

        sender.sendMessage("No tienes permisos para ejecutar este comando.");
        return false;
    }
}
