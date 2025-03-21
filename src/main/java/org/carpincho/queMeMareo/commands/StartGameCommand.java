package org.carpincho.queMeMareo.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.carpincho.queMeMareo.QueMeMareo;
import org.carpincho.queMeMareo.Manager.GameManager;

public class StartGameCommand implements CommandExecutor {

    private final QueMeMareo plugin;


    public StartGameCommand(QueMeMareo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("queMeMareo.iniciarJuego")) {
                player.sendMessage("¡El juego ha comenzado!");


                GameManager.getInstance(plugin).startGame();

                Bukkit.broadcastMessage("El juego Que me Mareo ha comenzado. ¡Buena suerte a todos!");
            } else {
                player.sendMessage("No tienes permiso para iniciar el juego.");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("Este comando solo puede ser ejecutado por un jugador.");
        }
        return true;
    }
}