package org.carpincho.queMeMareo.Manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;

public class ItemDisplayManager {

    private ItemDisplay itemDisplay;
    private double x, y, z;
    private float scale;
    private final float MIN_SCALE = 0.1f;
    private final float SCALE_DECREMENT = 0.2f;

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


        Bukkit.getLogger().info("Posición del ItemDisplay: " + location.toString());
    }

    public void setSize(double size) {
        this.scale = (float) size;
        updateScale();
    }

    public double getSize() {
        return this.itemDisplay.getTransformation().getScale().x();
    }

    public void setItemStack(ItemStack itemStack) {
        itemDisplay.setItemStack(itemStack);
    }

    public void lookAt(Location target) {
        Location eyeLocation = itemDisplay.getLocation();
        Vector direction = target.toVector().subtract(eyeLocation.toVector()).normalize();

        double yaw = Math.toDegrees(Math.atan2(direction.getZ(), direction.getX())) - 90;
        double pitch = Math.toDegrees(Math.asin(-direction.getY()));

        eyeLocation.setYaw((float) yaw);
        eyeLocation.setPitch((float) pitch);
        itemDisplay.teleport(eyeLocation);


        Bukkit.getLogger().info("Rotación ajustada: yaw=" + yaw + ", pitch=" + pitch);
    }

    public Location getLocation() {
        return itemDisplay.getLocation();
    }

    public void updatePosition(double x, double y, double z) {
        Location newLocation = new Location(itemDisplay.getWorld(), x, y, z);
        itemDisplay.teleport(newLocation);
        Bukkit.getLogger().info("Posición actualizada del ItemDisplay: " + newLocation.toString());
    }

    public void removeItemDisplay() {
        if (itemDisplay != null) {
            itemDisplay.remove();
        }
    }

    private void updateScale() {
        Transformation transformation = itemDisplay.getTransformation();
        transformation.getScale().set(scale, scale, scale);
        itemDisplay.setTransformation(transformation);


        Bukkit.getLogger().info("Escala actualizada del ItemDisplay: " + scale);
    }

}