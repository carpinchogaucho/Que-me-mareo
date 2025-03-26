package org.carpincho.queMeMareo.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.carpincho.queMeMareo.Manager.GameManager;
import org.carpincho.queMeMareo.QueMeMareo;


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
        } else if (sender instanceof ConsoleCommandSender) {

            Bukkit.getConsoleSender().sendMessage("El juego ha sido iniciado desde la consola.");
            GameManager.getInstance(plugin).startGame();
            Bukkit.broadcastMessage("El juego Que me Mareo ha comenzado. ¡Buena suerte a todos!");
        } else {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador o desde la consola.");
        }
        return true;
    }
}