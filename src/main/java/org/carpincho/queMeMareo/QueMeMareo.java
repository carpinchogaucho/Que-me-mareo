package org.carpincho.queMeMareo;

import org.bukkit.plugin.java.JavaPlugin;
import org.carpincho.queMeMareo.Listener.PlayerListener;
import org.carpincho.queMeMareo.Manager.GameManager;
import org.carpincho.queMeMareo.commands.StartGameCommand;
import org.carpincho.queMeMareo.commands.StopGameCommand;

public class QueMeMareo extends JavaPlugin {

    private static QueMeMareo instance;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Que me mareo plugin enabled!");

        gameManager = GameManager.getInstance(this);
        gameManager.startGame();


        getServer().getPluginManager().registerEvents(new PlayerListener(), this);


        this.getCommand("startgame").setExecutor(new StartGameCommand(this));
        getCommand("stopgame").setExecutor(new StopGameCommand());
    }

    @Override
    public void onDisable() {
        getLogger().info("Que me mareo plugin disabled!");
    }

    public static QueMeMareo getInstance() {
        return instance;
    }
}
