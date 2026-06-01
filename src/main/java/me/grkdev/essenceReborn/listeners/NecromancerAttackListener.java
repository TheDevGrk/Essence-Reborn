package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import me.grkdev.essenceReborn.powers.Wither;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class NecromancerAttackListener implements Listener {

    @EventHandler
    public static void onPlayerAttack(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Player player)
                || !player.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "necromancyActive"), PersistentDataType.BOOLEAN, false)
                || (e.getEntity() instanceof Player p && PowerManager.isTrusted(player, p))){
            return;
        }

        // set each mob owned by the player to target the hit entity
        ArrayList<Mob> targetingMobs = new ArrayList<>();
        for (Mob mob : player.getWorld().getNearbyEntitiesByType(Mob.class, player.getLocation(), 20, 20, 20)){
            if (Wither.isOwned(mob, player)){
                Wither.setTargetUUID(mob, e.getEntity());
                targetingMobs.add(mob);

            }
        }

        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "targetStartTime"), PersistentDataType.LONG, System.nanoTime());

    }
}
