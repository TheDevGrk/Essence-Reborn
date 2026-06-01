package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class EggHitListener implements Listener {

    @EventHandler
    public void onEggHit(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Egg egg) ||
                !e.getDamageSource().getDirectEntity().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "is_surpise_egg"), PersistentDataType.BOOLEAN, false)
                || !(e.getEntity() instanceof Player player)){
            return;
        }



        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "has_surprise_chicken");

        if (!PowerManager.isTrusted((Player) egg.getShooter(), player) && !pdc.getOrDefault(key, PersistentDataType.BOOLEAN, false)){
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CHICKEN_HURT, 10f, 1f);

            // ensure a player doesn't get multiple chickens
            pdc.set(key, PersistentDataType.BOOLEAN, true);

            Chicken surpriseChicken = player.getWorld().spawn(player.getLocation().add(0, 3, 0), Chicken.class, chicken -> {
                chicken.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 250, 10));
            });

            // make player blind and play lava chicken
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 240, 0));
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                surpriseChicken.addPassenger(player);
                if (!surpriseChicken.isValid()){
                    task.cancel();
                }
            }, 0, 1);
            player.playSound(player, Sound.MUSIC_DISC_LAVA_CHICKEN, 3f, 1f);


            // make chicken explode after 12 seconds
            Bukkit.getScheduler().runTaskLater(plugin, task -> {
                player.getWorld().spawnParticle(Particle.ITEM, surpriseChicken.getLocation().add(0, 0, 0),
                        70, 0, 0, 0, .9, ItemStack.of(Material.FEATHER));
                player.getWorld().spawnParticle(Particle.FIREWORK, surpriseChicken.getLocation(), 35);
                surpriseChicken.remove();
                player.getWorld().createExplosion(player.getLocation().subtract(0, .5, 0), 2f);
                pdc.set(key, PersistentDataType.BOOLEAN, false);
            }, 249);
        }

    }
}
