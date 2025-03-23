package org.carpincho.queMeMareo.MissUniverso.commandMissUniverso;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.carpincho.queMeMareo.MissUniverso.manager.GameManagerMissUnirverso;
import org.carpincho.queMeMareo.QueMeMareo;


public class BalanceStartCommand implements CommandExecutor {
    private final QueMeMareo plugin;

    public BalanceStartCommand(QueMeMareo plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        int round = Integer.parseInt(args[0]);

        GameManagerMissUnirverso gameManager = plugin.getGameManagerMissUnirverso();
        gameManager.start(round);

        sender.sendMessage("Game started! Try to balance the book.");

        return true;
    }
}

