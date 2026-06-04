package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import org.bukkit.*;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;
import static me.grkdev.essenceReborn.powers.Skeleton.*;

public class SkeletonArrowListener implements Listener {
    private static final NamespacedKey keyX = new NamespacedKey(plugin, "startingX");
    private static final NamespacedKey keyY = new NamespacedKey(plugin, "startingY");
    private static final NamespacedKey keyZ = new NamespacedKey(plugin, "startingZ");


    @EventHandler
    public static void onArrowHit(ProjectileHitEvent e){
        // !! possible issue: if passive threshold > weak threshold, weak won't activate until passive unlocked
        if (!(e.getEntity() instanceof Arrow arrow) || !(e.getEntity().getShooter() instanceof Player player)
                || EssenceManager.getActiveEssence(player) != EssenceTypes.SKELETON || !PowerManager.hasPassiveUnlocked(player)){
            return;
        }

        // find how far the arrow traveled
        double x = arrow.getPersistentDataContainer().getOrDefault(keyX, PersistentDataType.DOUBLE, arrow.getX());
        double y = arrow.getPersistentDataContainer().getOrDefault(keyY, PersistentDataType.DOUBLE, arrow.getY());
        double z = arrow.getPersistentDataContainer().getOrDefault(keyZ, PersistentDataType.DOUBLE, arrow.getZ());
        Location startingLoc = new Location(arrow.getWorld(), x, y, z);
        double travelDistance = startingLoc.distance(arrow.getLocation());


        // increase arrow damage by 1 for each 10 blocks the arrow traveled
        if (e.getHitEntity() instanceof LivingEntity livingEntity) {
            double damage = travelDistance / 10.0;
            livingEntity.damage(damage);





            // if Skeletal Grasp ability active, freeze the next entity hit
            if(player.getPersistentDataContainer().getOrDefault(skeletalGraspKey, PersistentDataType.BOOLEAN, false)){
                player.getPersistentDataContainer().set(skeletalGraspKey, PersistentDataType.BOOLEAN, false);

                // freeze entity for 15s
                Location entityStartingLoc = livingEntity.getLocation();

                //effects
                livingEntity.getWorld().playSound(entityStartingLoc, Sound.BLOCK_BONE_BLOCK_BREAK, 1f, 1f);
                livingEntity.getWorld().playSound(entityStartingLoc, Sound.ENTITY_SKELETON_HURT, 1f, .1f);
                livingEntity.getWorld().playSound(entityStartingLoc, Sound.BLOCK_NOTE_BLOCK_IMITATE_SKELETON, 1f, .1f);
                livingEntity.getWorld().playSound(entityStartingLoc, Sound.ITEM_ARMOR_EQUIP_LEATHER, 3f, .5f);


                BukkitTask freezeTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                    livingEntity.teleport(entityStartingLoc);


                    //effects
                    livingEntity.getWorld().playSound(entityStartingLoc, Sound.ENTITY_WITHER_SKELETON_AMBIENT, .01f, .1f);
                    livingEntity.getWorld().spawnParticle(Particle.ITEM, entityStartingLoc.clone().add(0, 1, 0), 5, .5, .5, .5, 0, ItemStack.of(Material.BONE));
                }, 0, 1);

                Bukkit.getScheduler().runTaskLater(plugin, task -> {
                    freezeTask.cancel();
                }, 300);
            }
        }
    }

    @EventHandler
    public static void onArrowLaunch(ProjectileLaunchEvent e){
        if (!(e.getEntity() instanceof Arrow arrow) || !(e.getEntity().getShooter() instanceof Player player)
                || EssenceManager.getActiveEssence(player) != EssenceTypes.SKELETON || !PowerManager.hasPassiveUnlocked(player)){
            return;
        }

        // mark the arrow's starting location so we know how far it traveled in other event handler
        arrow.getPersistentDataContainer().set(keyX, PersistentDataType.DOUBLE, e.getLocation().getX());
        arrow.getPersistentDataContainer().set(keyY, PersistentDataType.DOUBLE, e.getLocation().getY());
        arrow.getPersistentDataContainer().set(keyZ, PersistentDataType.DOUBLE, e.getLocation().getZ());

        // if Enchanted Quiver ability active, give arrow random negative effect
        if (player.getPersistentDataContainer().getOrDefault(enchantedQuiverKey, PersistentDataType.BOOLEAN, false)){
            int index = (int) (Math.random() * negativePotionEffectTypes.length);

            arrow.addCustomEffect(new PotionEffect(negativePotionEffectTypes[index], 40, 0), false);
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
        }

    }
}
