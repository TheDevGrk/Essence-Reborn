package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FallDamageListener implements Listener {

    @EventHandler
    public void onFallDamage(EntityDamageEvent e){
        if (!(e.getEntity() instanceof Player player)
                || EssenceManager.getActiveEssence((Player) e.getEntity()) != EssenceTypes.CHICKEN
                || !PowerManager.hasPassiveUnlocked((Player) e.getEntity())
                || e.getDamageSource().getDamageType() != DamageType.FALL){
            return;
        }

        e.setCancelled(true);
    }
}
