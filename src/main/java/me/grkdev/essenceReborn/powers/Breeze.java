package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Breeze extends Power {
    private EssenceTypes essenceType = EssenceTypes.BREEZE;

    public void setEssenceType(EssenceTypes essenceType) {
        this.essenceType = essenceType;
    }

    public EssenceTypes getEssenceType() {
        return essenceType;
    }



    @Override
    public void onPassivePower(Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            // give increasingly good effects as elevation increases
            if (player.isOnline() && EssenceManager.getActiveEssence(player) == EssenceTypes.BREEZE && PowerManager.hasPassiveUnlocked(player)){
                if(player.getY() >= 220){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 40, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 1));
                }
                else if(player.getY() >= 170){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 40, 1));
                }
                else if(player.getY() >= 130){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 40, 0));
                }
                else if(player.getY() >= 100){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0));

                }
                return;
            }

            task.cancel();
        }, 0, 20);
    }

    @Override
    public void onWeakPower(Player player) {
        player.setVelocity(player.getLocation().getDirection().multiply(2));


        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_JUMP, 3f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_DEFLECT, .5f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_WHIRL, 1f, 1f);
        player.getWorld().spawnParticle(Particle.CLOUD, player.getLocation(), 25, 0, 0,0, 1);

    }

    @Override
    public void onStrongPower(Player player) {
//        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, 3f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_SHOOT, 1f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_WHIRL, 2f, 1f);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BREEZE_WIND_BURST, .5f, 1f);

        for (Player p : player.getWorld().getNearbyPlayers(player.getLocation(), 7, 7, 7)){
            if (p == player || PowerManager.isTrusted(player, p)) continue;

            // fling player in air (away from activating player)
            p.getWorld().spawnParticle(Particle.GUST_EMITTER_LARGE, p.getLocation(), 3);


            p.setVelocity(p.getLocation().subtract(player.getLocation()).toVector().normalize().add(new Vector(0, 1, 0)).multiply(3.25));
            p.damage(4);

            // shuffle player's inventory
            ArrayList<ItemStack> inv = new ArrayList<>();
            for (int i = 0; i < 9; i++){
                inv.add(p.getInventory().getItem(i));
            }

            Collections.shuffle(inv);
            for (int i = 0; i < 9; i++){
                p.getInventory().setItem(i, inv.get(i));
            }
        }
    }


}
