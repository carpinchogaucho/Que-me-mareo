package org.carpincho.queMeMareo.MissUniverso.commandMissUniverso;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.carpincho.queMeMareo.MissUniverso.manager.GameManagerMissUnirverso;

public class StopMissUniverso implements CommandExecutor {
    private final GameManagerMissUnirverso GameManagerMissUnirverso;

    public StopMissUniverso(GameManagerMissUnirverso GameManagerMissUnirverso) {
        this.GameManagerMissUnirverso = GameManagerMissUnirverso;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por un jugador.");
            return true;
        }

        if (!sender.hasPermission("queMeMareo.stop")) {
            sender.sendMessage("No tienes permisos para ejecutar este comando.");
            return true;
        }

        if (!GameManagerMissUnirverso.isPlaying()) {
            sender.sendMessage("No hay una partida en curso.");
            return true;
        }

        GameManagerMissUnirverso.stop();
        sender.sendMessage("El juego ha sido detenido.");
        return true;
    }
}


