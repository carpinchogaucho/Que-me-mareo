package org.carpincho.queMeMareo.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
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

    private final double eyeX = 10740;
    private final double eyeZ = -23822;
    private final double eyeY = -41;
    private final double obstacleTargetY = -42;

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
        if (gameActive) return;

        gameActive = true;
        gameWon = false;
        currentEyeSize = 3.0;
        playerLaps.clear();
        playerQuadrants.clear();

        Bukkit.getLogger().info("Juego iniciado. Tamaño inicial del ojo: " + currentEyeSize);
        spawnEye();
        spawnObstacles();
        trackPlayer();
    }

    public void spawnEye() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        if (eye != null) {
            eye.removeItemDisplay();
        }

        eye = new ItemDisplayManager(world, eyeX, eyeY, eyeZ);
        eye.setItemStack(new ItemStack(Material.ENDER_EYE));
        eye.setSize(currentEyeSize);

        Bukkit.getLogger().info("Ojo creado en la posición: " + eyeX + ", " + eyeY + ", " + eyeZ);
    }

    private void trackPlayer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameActive || eye == null) {
                    cancel();
                    return;
                }

                Player closestPlayer = getClosestPlayer();
                if (closestPlayer != null) {
                    eye.lookAt(closestPlayer.getLocation());
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private Player getClosestPlayer() {
        if (Bukkit.getOnlinePlayers().isEmpty()) return null;

        return Bukkit.getOnlinePlayers().stream()
                .min(Comparator.comparingDouble(p -> p.getLocation().distance(eye.getLocation())))
                .orElse(null);
    }

    private void spawnObstacles() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        obstacles.clear();

        for (int i = 0; i < maxObstacles; i++) {
            double x = 10731 + random.nextDouble() * 16;
            double y = eyeY + 5;
            double z = -23831 + random.nextDouble() * 16;

            ItemDisplayManager itemDisplay = new ItemDisplayManager(world, x, y, z);
            itemDisplay.setItemStack(new ItemStack(Material.STICK));
            obstacles.add(itemDisplay);

            double fallSpeed = 0.3;
            double targetY = obstacleTargetY;

            new BukkitRunnable() {
                double currentY = y;

                @Override
                public void run() {
                    if (currentY > targetY) {
                        currentY -= fallSpeed;
                        itemDisplay.updatePosition(x, currentY, z);
                    } else {
                        cancel();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                itemDisplay.removeItemDisplay();
                                obstacles.remove(itemDisplay);
                            }
                        }.runTaskLater(plugin, 100L);
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
        }
    }

    public void playerCompletedLap(Player player) {
        if (gameWon || eye == null) return;

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
                shrinkEye();
            }
        }
    }

    private void shrinkEye() {
        if (currentEyeSize > minEyeSize) {
            currentEyeSize -= eyeSizeDecrease;
            eye.setSize(currentEyeSize);
            Bukkit.getLogger().info("Nuevo tamaño del ojo: " + currentEyeSize);
        }

        if (currentEyeSize <= minEyeSize) {
            gameWon = true;
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("¡El ojo ha desaparecido! ¡Has ganado el juego!"));
            stopGame();
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
        new BukkitRunnable() {
            @Override
            public void run() {
                player.setWalkSpeed(0.2f);
            }
        }.runTaskLater(plugin, 60L);
    }

    public List<ItemDisplayManager> getObstacles() {
        return obstacles;
    }

    public void stopGame() {
        if (!gameActive) return;

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
