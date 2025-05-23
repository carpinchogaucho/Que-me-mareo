package org.carpincho.queMeMareo.Manager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.carpincho.queMeMareo.QueMeMareo;
import org.joml.Vector3f;

public class ItemDisplayManager {

    private ItemDisplay itemDisplay;
    private double x, y, z;
    private float scale;
    private final float MIN_SCALE = 0.1f;
    private final float SCALE_DECREMENT = 0.3f;

    public ItemDisplayManager(World world, double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.scale = 1.0f;
        spawnItemDisplay(world);
    }

    private void spawnItemDisplay(World world) {
        Location location = new Location(world, x, y, z);
        itemDisplay = location.getWorld().spawn(location, ItemDisplay.class);


        itemDisplay.setItemStack(new ItemStack(Material.ENDER_EYE));
        itemDisplay.setBillboard(ItemDisplay.Billboard.CENTER);
        itemDisplay.setGlowing(false);
        itemDisplay.setGravity(false);


        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(scale, scale, scale);
        itemDisplay.setTransformation(transformation);



    }

    public void decreaseScale() {
        if (scale > MIN_SCALE) {
            scale -= SCALE_DECREMENT;
            if (scale < MIN_SCALE) {
                scale = MIN_SCALE;
            }
            updateScale();
        }
    }

    public void startShrinkingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (scale <= MIN_SCALE) {
                    this.cancel();
                    return;
                }
                decreaseScale();
            }
        }.runTaskTimer(QueMeMareo.getInstance(), 10L, 10L);
    }

    public void setSize(double size) {
        this.scale = (float) size;
        updateScale();
        Bukkit.getLogger().info("Nueva escala asignada al ItemDisplay: " + size);
    }

    public double getSize() {
        return this.itemDisplay.getTransformation().getScale().x();
    }

    public void setItemStack(ItemStack itemStack) {
        itemDisplay.setItemStack(itemStack);
    }

    public void setCustomName(String name) {
        if (itemDisplay != null) {
            itemDisplay.customName(Component.text(name));
            itemDisplay.setCustomNameVisible(false);
        }
    }

    public void setCustomNameVisible(boolean visible) {
        if (itemDisplay != null) {
            itemDisplay.setCustomNameVisible(visible);
        }
    }

    public void lookAt(Location target) {
        Location eyeLocation = itemDisplay.getLocation();
        Vector direction = target.toVector().subtract(eyeLocation.toVector()).normalize();

        double yaw = Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
        double pitch = Math.toDegrees(Math.asin(-direction.getY()));

        eyeLocation.setYaw((float) yaw);
        eyeLocation.setPitch((float) pitch);
        itemDisplay.teleport(eyeLocation);



    }

    public Location getLocation() {
        return itemDisplay.getLocation();
    }

    public void updatePosition(double x, double y, double z) {
        Location newLocation = new Location(itemDisplay.getWorld(), x, y, z);
        itemDisplay.teleport(newLocation);

    }

    public void removeItemDisplay() {
        if (itemDisplay != null) {
            itemDisplay.remove();
        }
    }

    private void updateScale() {
        if (itemDisplay != null) {
            Transformation currentTransformation = itemDisplay.getTransformation();

            Vector3f newScale = new Vector3f(scale, scale, scale);

            Transformation newTransformation = new Transformation(
                    currentTransformation.getTranslation(),
                    currentTransformation.getLeftRotation(),
                    newScale,
                    currentTransformation.getRightRotation()
            );

            itemDisplay.setTransformation(newTransformation);

            Bukkit.getLogger().info("Nueva escala aplicada al ItemDisplay: " + scale);
        }
    }
}