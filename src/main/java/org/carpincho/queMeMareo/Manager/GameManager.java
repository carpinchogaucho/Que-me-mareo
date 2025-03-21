package org.carpincho.queMeMareo.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.carpincho.queMeMareo.QueMeMareo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GameManager {

    private static GameManager instance;
    private List<Player> players;
    private List<ItemDisplayManager> obstacles;
    private ItemDisplayManager eye;
    private final Random random;
    private final int maxObstacles = 10;
    private double currentEyeSize = 3.0;
    private final double minEyeSize = 0.1;
    private final double eyeSizeDecrease = 0.05;
    private boolean gameWon = false;
    private boolean gameActive = false;
    private final JavaPlugin plugin;
    private final Map<Player, Integer> playerLaps = new HashMap<>();
    private final int requiredLaps = 3;
    private final Map<Player, Set<String>> playerQuadrants = new HashMap<>();

    private final double eyeX = 10739;
    private final double eyeZ = -23823;


    private final double minDistance = 5.0;


    private GameManager(JavaPlugin plugin) {
        this.players = new ArrayList<>();
        this.obstacles = new ArrayList<>();
        this.random = new Random();
        this.plugin = plugin;
    }


    public static GameManager getInstance(JavaPlugin plugin) {
        if (instance == null) {
            instance = new GameManager(plugin);
        }
        return instance;
    }


    public void startGame() {
        if (gameActive) {
            return;
        }
        gameActive = true;
        Bukkit.getLogger().info("Juego iniciado. Tamaño inicial del ojo: " + currentEyeSize);
        spawnEye();
        spawnObstacles();
        trackPlayer();
    }


    public void spawnEye() {
        World world = Bukkit.getWorld("world");
        double x = 10739;
        double y = -41;
        double z = -23823;

        if (eye != null) {
            eye.removeItemDisplay();
        }

        eye = new ItemDisplayManager(world, x, y, z);
        eye.setItemStack(new ItemStack(Material.ENDER_EYE));
        eye.setSize(currentEyeSize);

        Bukkit.getLogger().info("Ojo creado en la posición: " + x + ", " + y + ", " + z);
    }


    private void trackPlayer() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (gameActive && eye != null) {
                Player closestPlayer = getClosestPlayer();
                if (closestPlayer != null) {
                    eye.lookAt(closestPlayer.getLocation());
                }
            }
        }, 0L, 20L);
    }


    private Player getClosestPlayer() {
        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
        for (Player player : Bukkit.getOnlinePlayers()) {
            double distance = eye.getLocation().distance(player.getLocation());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestPlayer = player;
            }
        }
        return closestPlayer;
    }


    private void spawnObstacles() {
        World world = Bukkit.getWorld("world");
        obstacles.clear();

        for (int i = 0; i < maxObstacles; i++) {
            double x = 10731 + random.nextDouble() * (10747 - 10731);
            double y = 20;
            double z = -23831 + random.nextDouble() * (-23815 - (-23831));

            ItemDisplayManager itemDisplay = new ItemDisplayManager(world, x, y, z);
            itemDisplay.setItemStack(new ItemStack(Material.STICK));
            obstacles.add(itemDisplay);

            double fallSpeed = 0.3;
            double targetY = -42;

            new BukkitRunnable() {
                double currentY = y;

                @Override
                public void run() {
                    if (currentY > targetY) {
                        currentY -= fallSpeed;
                        itemDisplay.updatePosition(x, currentY, z);
                    } else {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }



    public void playerCompletedLap(Player player) {
        if (gameWon) return;
        if (eye == null) {
            Bukkit.getLogger().warning("El ojo aún no está inicializado.");
            return;
        }

        Location loc = player.getLocation();
        String quadrant = getQuadrant(loc);

        if (quadrant == null) return;


        Set<String> visitedQuadrants = playerQuadrants.computeIfAbsent(player, k -> new HashSet<>());
        visitedQuadrants.add(quadrant);


        if (visitedQuadrants.size() == 4) {
            playerLaps.put(player, playerLaps.getOrDefault(player, 0) + 1);
            visitedQuadrants.clear();

            int laps = playerLaps.get(player);
            Bukkit.getLogger().info(player.getName() + " ha completado " + laps + " vueltas.");

            if (laps >= requiredLaps) {
                playerLaps.put(player, 0);

                if (currentEyeSize > minEyeSize) {
                    currentEyeSize -= eyeSizeDecrease;
                    eye.setSize(currentEyeSize);
                    Bukkit.getLogger().info("Nuevo tamaño del ojo: " + currentEyeSize);
                }

                if (currentEyeSize <= minEyeSize) {
                    currentEyeSize = minEyeSize;
                    gameWon = true;
                    Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("¡El ojo ha desaparecido! ¡Has ganado el juego!"));
                }
            }
        }
    }

    private String getQuadrant(Location loc) {
        double dx = loc.getX() - eyeX;
        double dz = loc.getZ() - eyeZ;

        if (Math.sqrt(dx * dx + dz * dz) > minDistance) return null;

        if (dx > 0 && dz > 0) return "NE";
        if (dx > 0 && dz < 0) return "SE";
        if (dx < 0 && dz > 0) return "NW";
        if (dx < 0 && dz < 0) return "SW";

        return null;
    }




    public void freezePlayer(Player player) {
        player.setWalkSpeed(0);
        Bukkit.getScheduler().runTaskLater(plugin, () -> player.setWalkSpeed(0.2f), 20L * 3);
    }


    public List<ItemDisplayManager> getObstacles() {
        return obstacles;
    }

    // Detiene el juego
    public void stopGame() {
        if (!gameActive) {
            return;
        }
        gameActive = false;
        gameWon = false;
        currentEyeSize = 3.0;

        if (eye != null) {
            eye.removeItemDisplay();
            eye = null;
        }

        for (ItemDisplayManager obstacle : obstacles) {
            obstacle.removeItemDisplay();
        }
        obstacles.clear();

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("¡El juego ha sido detenido!"));
    }
}
