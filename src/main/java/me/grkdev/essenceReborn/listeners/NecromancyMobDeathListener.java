package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.powers.Wither;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.grkdev.essenceReborn.EssenceReborn.*;

public class NecromancyMobDeathListener implements Listener {
    private static final int maxMobs = 15;
    private static final List<EntityType> disallowedMobTypes = List.of(EntityType.WITHER, EntityType.ENDER_DRAGON, EntityType.WARDEN);

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent e){
        if (!(e.getEntity() instanceof Mob mob)){
            return;
        }

        // check if the killer has necromancy mode enabled
        if (!disallowedMobTypes.contains(mob.getType()) && mob.getKiller() != null && mob.getKiller().getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "necromancy_mode"), PersistentDataType.BOOLEAN, false)){
            NamespacedKey ownerKey = new NamespacedKey(plugin, "ownerUUID");
            NamespacedKey countKey = new NamespacedKey(plugin, "necromancedMobCount");
            Player player = mob.getKiller();
            PersistentDataContainer mobPDC = mob.getPersistentDataContainer();
            PersistentDataContainer playerPDC = player.getPersistentDataContainer();
            int mobCount = playerPDC.getOrDefault(countKey, PersistentDataType.INTEGER, 0);

            // simply kill the mob in the storage if it was killed by its owner whilst in necromancy mode
            if (Wither.isOwned(mob, player)){
                Bukkit.getEntity(UUID.fromString(mobPDC.getOrDefault(new NamespacedKey(plugin, "parentUUID"), PersistentDataType.STRING, ""))).remove();
                playerPDC.set(countKey, PersistentDataType.INTEGER, mobCount - 1);
                return;
            }



            // adds killed mob to the necromanced mobs for the player if player isn't at max mobs
            //checks if player is already at max necromanced mobs
            if (mobCount >= maxMobs){
                player.sendMessage(Component.text("You cannot bind anymore mobs into your servitude!", NamedTextColor.RED));
            }
            // store copied mob in storage dimension
            Mob copiedMob = (Mob) mob.copy(new Location(overWorld, 0, 15000, 0));
            copiedMob.setHealth(copiedMob.getAttribute(Attribute.MAX_HEALTH).getValue());
            copiedMob.setAI(false);
            copiedMob.setPersistent(true);


            copiedMob.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());
            playerPDC.set(countKey, PersistentDataType.INTEGER, mobCount + 1);
        }
    }
}
