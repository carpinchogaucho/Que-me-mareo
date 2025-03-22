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
    private Map<Player, ItemDisplayManager> playerEyes;
    private List<ItemDisplayManager> obstacles;
    private final Map<Player, Integer> playerLaps = new HashMap<>();
    private final Map<Player, Set<String>> playerQuadrants = new HashMap<>();
    private final Set<Player> winners = new HashSet<>();

    private final Random random;
    private final int maxObstacles = 10;
    private final double minEyeSize = 0.1;
    private final double eyeSizeDecrease = 0.05;
    private final int requiredLaps = 3;
    private final JavaPlugin plugin;

    private final double eyeX = 10740;
    private final double eyeZ = -23822;
    private final double eyeY = -41;
    private final double obstacleTargetY = -42;
    private final double minDistance = 5.0;

    private boolean gameActive = false;

    private GameManager(JavaPlugin plugin) {
        this.players = new ArrayList<>();
        this.playerEyes = new HashMap<>();
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
        playerLaps.clear();
        playerQuadrants.clear();
        winners.clear();

        Bukkit.getLogger().info("Juego iniciado para todos los jugadores.");
        spawnEyeForPlayers();
        spawnObstacles();
        trackPlayers();
    }

    private void spawnEyeForPlayers() {
        World world = Bukkit.getWorld("world");
        if (world == null) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (winners.contains(player)) continue;

            ItemDisplayManager eye = new ItemDisplayManager(world, eyeX, eyeY, eyeZ);
            eye.setItemStack(new ItemStack(Material.ENDER_EYE));
            eye.setSize(3.0);
            playerEyes.put(player, eye);

            Bukkit.getLogger().info("Ojo creado para " + player.getName() + " en la posición: " + eyeX + ", " + eyeY + ", " + eyeZ);
        }
    }

    private void trackPlayers() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameActive || playerEyes.isEmpty()) {
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (winners.contains(player)) continue;

                    ItemDisplayManager eye = playerEyes.get(player);
                    if (eye != null) {
                        eye.lookAt(player.getLocation());
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void playerCompletedLap(Player player) {
        if (winners.contains(player) || !playerEyes.containsKey(player)) return;

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
                shrinkEye(player);
            }
        }
    }

    private void shrinkEye(Player player) {
        if (!playerEyes.containsKey(player)) return;

        ItemDisplayManager eye = playerEyes.get(player);
        double currentEyeSize = eye.getSize();

        if (currentEyeSize > minEyeSize) {
            currentEyeSize -= eyeSizeDecrease;
            eye.setSize(currentEyeSize);
            Bukkit.getLogger().info("Nuevo tamaño del ojo de " + player.getName() + ": " + currentEyeSize);
        }

        if (currentEyeSize <= minEyeSize) {
            winners.add(player);
            eye.removeItemDisplay();
            playerEyes.remove(player);
            player.sendMessage("¡Tu ojo ha desaparecido! ¡Has ganado el juego!");
        }
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


                                if (obstacles.isEmpty()) {
                                    spawnObstacles();
                                }
                            }
                        }.runTaskLater(plugin, 100L);
                    }
                }
            }.runTaskTimer(plugin, 0, 1);
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
        for (ItemDisplayManager eye : playerEyes.values()) {
            eye.removeItemDisplay();
        }
        playerEyes.clear();
        obstacles.forEach(ItemDisplayManager::removeItemDisplay);
        obstacles.clear();
        winners.clear();

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage("¡El juego ha sido detenido!"));
    }
}