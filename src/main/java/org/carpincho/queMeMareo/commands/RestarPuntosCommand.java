package org.carpincho.queMeMareo.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.carpincho.queMeMareo.Manager.GameManager;


public class RestarPuntosCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("Uso: /restarPuntos <jugador>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Ese jugador no está en línea.");
            return false;
        }

        GameManager.getInstance(null).removePoints(target, 20);
        if (sender instanceof Player playerSender) {
            playerSender.sendActionBar("§e-20 puntos a §l" + target.getName());
        } else {
            sender.sendMessage("Has restado 20 puntos a " + target.getName());
        }
        return true;
    }
}
