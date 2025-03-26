package org.carpincho.queMeMareo;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.carpincho.queMeMareo.Listener.PlayerListener;
import org.carpincho.queMeMareo.Manager.GameManager;
import org.carpincho.queMeMareo.MissUniverso.Listener.GameListener;
import org.carpincho.queMeMareo.MissUniverso.commandMissUniverso.BalanceRegisterCommand;
import org.carpincho.queMeMareo.MissUniverso.commandMissUniverso.BalanceStartCommand;
import org.carpincho.queMeMareo.MissUniverso.commandMissUniverso.StopMissUniverso;
import org.carpincho.queMeMareo.MissUniverso.manager.GameManagerMissUnirverso;
import org.carpincho.queMeMareo.commands.RestarPuntosCommand;
import org.carpincho.queMeMareo.commands.StartGameCommand;
import org.carpincho.queMeMareo.commands.StopGameCommand;


public class QueMeMareo extends JavaPlugin {

    private static QueMeMareo instance;
    private GameManager gameManager;
    public GameManagerMissUnirverso GameManagerMissUnirverso;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Que me mareo plugin enabled!");

        gameManager = GameManager.getInstance(this);

        GameManagerMissUnirverso = new GameManagerMissUnirverso(this);



        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);


        //MISS UNIVERSO
        this.getCommand("balance-start").setExecutor(new BalanceStartCommand(this));
        this.getCommand("balance-register").setExecutor(new BalanceRegisterCommand(this));
        getCommand("stopmissuniverso").setExecutor(new StopMissUniverso(GameManagerMissUnirverso));

        //QUE ME MAREO
        this.getCommand("startgame").setExecutor(new StartGameCommand(this));
        getCommand("stopgame").setExecutor(new StopGameCommand(this));
        getCommand("restarPuntos").setExecutor(new RestarPuntosCommand());
    }

    public GameManagerMissUnirverso getGameManagerMissUnirverso() {
        return GameManagerMissUnirverso;
    }

    @Override
    public void onDisable() {
        getLogger().info("Que me mareo plugin disabled!");
    }

    public static QueMeMareo getInstance() {
        return instance;
    }
}
