package me.grkdev.essenceReborn.listeners;

import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AttackEffectListener implements Listener {

    @EventHandler
    public static void onPlayerAttack(EntityDamageByEntityEvent e){
        if (!(e.getDamager() instanceof Player player)){
            return;
        }


        //wither passive power
        double witherChance = 0.35;
        if (EssenceManager.getActiveEssence(player) == EssenceTypes.WITHER && PowerManager.hasPassiveUnlocked(player, EssenceTypes.WITHER) && Math.random() <= witherChance
            && !(e.getEntity() instanceof Player p && PowerManager.isTrusted(player, p))){
            LivingEntity livingEntity = (LivingEntity) e.getEntity();
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 160, 1));
        }

    }
}
