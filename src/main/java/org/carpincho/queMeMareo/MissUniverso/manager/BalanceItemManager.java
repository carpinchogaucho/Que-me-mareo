package org.carpincho.queMeMareo.MissUniverso.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.carpincho.queMeMareo.QueMeMareo;

import java.util.UUID;

public class BalanceItemManager {

    public enum BalanceAxis {
        X, Z;
    }

    private final ItemDisplay itemDisplay;
    private final UUID playerUuid;
    private double tilt;
    private double tiltSpeed;
    private final double maxTilt = Math.toRadians(45);
    private final double balanceThreshold = Math.toRadians(5);

    private int stableTicks;
    private int stableTicksThreshold;
    private boolean isStable;

    private BalanceAxis balanceAxis = BalanceAxis.X;

    public BalanceItemManager(UUID uuid, Location location, int round) {
        this.playerUuid = uuid;
        this.itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);
        this.tilt = 0.0;
        this.tiltSpeed = 0.01;
        this.stableTicks = 0;
        this.isStable = false;
        this.stableTicksThreshold = 60 + (int) (Math.random() * 41);
        disappear(true);

        int customModelData;
        switch (round) {
            case 1:
                customModelData = 1007;
                break;
            case 2:
                customModelData = 1008;
                break;
            case 3:
                customModelData = 1009;
                break;
            default:
                customModelData = 1000 + (int) (Math.random() * 10);
                break;
        }

        SetCustomModelData(customModelData);
    }

    public ItemDisplay getItemDisplay() {
        return this.itemDisplay;
    }

    public double getTilt() {
        return this.tilt;
    }

    public BalanceAxis getBalanceAxis() {
        return balanceAxis;
    }

    public void SetCustomModelData(int CustomModelData) {
        ItemStack itemStack = new ItemStack(Material.IRON_NUGGET);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(CustomModelData);
            itemStack.setItemMeta(meta);
        }
        this.itemDisplay.setItemStack(itemStack);
    }

    public void setTilt(double tilt) {
        this.tilt = tilt;
        float tiltDegrees = (float) Math.toDegrees(tilt);
        if (balanceAxis == BalanceAxis.X) {
            this.itemDisplay.setRotation(270, tiltDegrees);
        } else if (balanceAxis == BalanceAxis.Z) {
            this.itemDisplay.setRotation(0, tiltDegrees);
        }
    }

    public void setTiltSpeed(double tiltSpeed) {
        this.tiltSpeed = tiltSpeed;
    }

    public void setBalanceAxis(BalanceAxis axis) {
        this.balanceAxis = axis;
        setTilt(this.tilt);
    }

    public void reset() {
        this.tilt = 0.0;
        this.stableTicks = 0;
        this.isStable = false;
        setTilt(this.tilt);
        this.tiltSpeed = 0.01;
        disappear(true);
    }

    public void update() {

        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;

        Location newLoc = player.getLocation().clone().add(0, 2.3, 0);
        this.itemDisplay.teleport(newLoc);

        if (Math.abs(this.tilt) < balanceThreshold) {
            if (!isStable) {
                isStable = true;
                stableTicks = 0;
                stableTicksThreshold = 60 + (int) (Math.random() * 41);
                tiltSpeed = 0;
            }

            stableTicks++;
            if (stableTicks % 20 == 0) {
                player.sendActionBar("§a+1 punto por equilibrio");

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playglow " + player.getName() + " green 1 1 1 50 50");

                GameManagerMissUnirverso GameManagerMissUnirverso = QueMeMareo.getInstance().getGameManagerMissUnirverso();

                GameManagerMissUnirverso.getPlayerScore().put(
                        playerUuid,
                        GameManagerMissUnirverso.getPlayerScore().getOrDefault(playerUuid, 0) + 1);
            }

            if (stableTicks >= stableTicksThreshold) {
                isStable = false;
                stableTicks = 0;
                double sign = Math.random() < 0.5 ? 1.0 : -1.0;
                tilt = sign * (balanceThreshold + 0.01);
                tiltSpeed = sign * 0.01;
            }
        } else {
            isStable = false;
            stableTicks = 0;
            tilt += tiltSpeed;
        }

        if (Math.abs(tilt) > maxTilt) {
            player = Bukkit.getPlayer(playerUuid);
            if (player != null) {
                GameManagerMissUnirverso gameManager = QueMeMareo.getInstance().getGameManagerMissUnirverso();
                gameManager.onBookFall(player);
            }
            disappear(true);
            return;
        }

        setTilt(tilt);
    }

    public void adjustTilt(double adjustment) {
        if (Math.abs(adjustment) > 0.01) {
            isStable = false;
            stableTicks = 0;
        }
        this.tilt -= adjustment;
        if (Math.abs(this.tilt) > maxTilt) {
            disappear(true);
        } else {
            setTilt(this.tilt);
        }
    }

    public void disappear(boolean value) {
        itemDisplay.setVisibleByDefault(!value);
    }

    public void remove() {
        itemDisplay.remove();
    }

    public void startBalancing() {
        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!itemDisplay.isVisibleByDefault() || !player.isOnline()) {
                    cancel();
                    return;
                }
                update();
            }
        }.runTaskTimer(QueMeMareo.getInstance(), 0L, 1L);
    }
}
