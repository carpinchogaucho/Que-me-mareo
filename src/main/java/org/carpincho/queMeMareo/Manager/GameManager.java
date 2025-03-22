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

    private final List<Location[]> eyeAreas = Arrays.asList(
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -41, -23815), new Location(Bukkit.getWorld("world"), 10747, -41, -23831)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -41, -23797), new Location(Bukkit.getWorld("world"), 10747, -41, -23813)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10713, -41, -23813), new Location(Bukkit.getWorld("world"), 10713, -41, -23787)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23813), new Location(Bukkit.getWorld("world"), 10695, -41, -23797)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23815), new Location(Bukkit.getWorld("world"), 10695, -41, -23831)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -41, -23833), new Location(Bukkit.getWorld("world"), 10695, -41, -23849)}
    );

    private final List<Location[]> obstacleAreas = Arrays.asList(
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -43, -23815), new Location(Bukkit.getWorld("world"), 10747, -41, -23831)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10731, -43, -23797), new Location(Bukkit.getWorld("world"), 10747, -41, -23813)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10713, -43, -23813), new Location(Bukkit.getWorld("world"), 10713, -41, -23787)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23813), new Location(Bukkit.getWorld("world"), 10695, -41, -23797)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23815), new Location(Bukkit.getWorld("world"), 10695, -41, -23831)},
            new Location[]{new Location(Bukkit.getWorld("world"), 10711, -43, -23833), new Location(Bukkit.getWorld("world"), 10695, -41, -23849)}
    );

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

        for (Location[] area : eyeAreas) {
            Location firstPos = area[0];
            Location secondPos = area[1];

            double centerX = (firstPos.getX() + secondPos.getX()) / 2;
            double centerZ = (firstPos.getZ() + secondPos.getZ()) / 2;
            double centerY = firstPos.getY();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (winners.contains(player)) continue;

                ItemDisplayManager eye = new ItemDisplayManager(world, centerX, centerY, centerZ);
                eye.setItemStack(new ItemStack(Material.ENDER_EYE));
                eye.setSize(3.0);
                playerEyes.put(player, eye);

                Bukkit.getLogger().info("Ojo creado en la posición: " + centerX + ", " + centerY + ", " + centerZ);
            }
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

        for (Location[] area : obstacleAreas) {
            Location firstPos = area[0];
            Location secondPos = area[1];

            double minX = Math.min(firstPos.getX(), secondPos.getX());
            double maxX = Math.max(firstPos.getX(), secondPos.getX());
            double minZ = Math.min(firstPos.getZ(), secondPos.getZ());
            double maxZ = Math.max(firstPos.getZ(), secondPos.getZ());

            double x = minX + random.nextDouble() * (maxX - minX);
            double z = minZ + random.nextDouble() * (maxZ - minZ);
            double y = firstPos.getY() + 5; // Empieza cayendo desde arriba

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