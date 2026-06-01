package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Warden extends Power {
    private EssenceTypes essenceType = EssenceTypes.WARDEN;

    public EssenceTypes getEssenceType() {
        return essenceType;
    }

    public void setEssenceType(EssenceTypes essenceType) {
        this.essenceType = essenceType;
    }


    @Override
    public void onPassivePower(Player player) {
        player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(1); // quadruple sneaking speed
    }

    @Override
    public void onWeakPower(Player player) {
        NamespacedKey key = new NamespacedKey(plugin, "sonicBlasted");

        final int SONIC_BOOM_DISTANCE = 30;


        Vector dir = player.getEyeLocation().getDirection();
        Location loc = player.getLocation();
        Entity[] armorStands = new Entity[SONIC_BOOM_DISTANCE];

        // play sonic boom charge sound effect and freeze player
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_CHARGE, 3f, 1f);
        player.setWalkSpeed(0);
        // lock where player is looking
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(6, 47, 122), 4f);
        BukkitTask lockTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            player.teleport(loc);
            player.getWorld().spawnParticle(Particle.DUST, player.getLocation(), 5, 0, 0, 0, dustOptions);
        }, 0, 2);


        // delay until charge sound effect is done
        Bukkit.getScheduler().runTaskLater(plugin, task -> {
            for (int i = 0; i < SONIC_BOOM_DISTANCE; i++){
                int finalI = i;
                player.getWorld().spawn(player.getEyeLocation().add(dir.clone().multiply(i)), ArmorStand.class, armorStand -> {
                    armorStand.setCanMove(false);
                    armorStand.setVisible(false);
                    armorStands[finalI] = armorStand;
                });
            }

            // play sonic boom sound and do particle effects
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 3f, 1f);
            for (Entity armorStand : armorStands){
                armorStand.getWorld().spawnParticle(Particle.SONIC_BOOM, armorStand.getLocation(), 1);
                // damage and knockback all entities in beam, making sure they can't be damaged more than once
                for (LivingEntity e : armorStand.getWorld().getNearbyLivingEntities(armorStand.getLocation(), 1, 1, 1)){
                    if ((e instanceof Player p && (p.equals(player) || PowerManager.isTrusted(player, p))) || e.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BOOLEAN, false)) continue;

                    e.damage(10);
                    e.setVelocity(dir.normalize().add(new Vector(0, 0.05f, 0)).multiply(3)); //knockback players
                    e.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
                }


                armorStand.remove();
            }

            //unlock player
            player.setWalkSpeed(0.2f);
            lockTask.cancel();

            //reset all entities so they can be hit again
            for (LivingEntity e : player.getWorld().getNearbyLivingEntities(player.getLocation(), SONIC_BOOM_DISTANCE + 20, SONIC_BOOM_DISTANCE + 20, SONIC_BOOM_DISTANCE + 20)){
                e.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, false);
            }
        }, 40);



    }

    @Override
    public void onStrongPower(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SNIFF, 10f, 1f);
        Runnable sniffPlayers = () -> {
            for (Player p : player.getWorld().getNearbyPlayers(player.getLocation(), 40)){
                if (p.equals(player) || PowerManager.isTrusted(player, p)) continue;


                // spawn particles that move towards nearby players
                // have to keep count at 0 to make the velocity go towards the player
                // spawn multiple to make it more noticeable
                Location loc1 = player.getLocation();
                Location loc2 = p.getLocation();
                Vector vel = new Vector(loc2.getX() - loc1.getX(), loc2.getY() - loc1.getY(), loc2.getZ() - loc1.getZ()).multiply(.05);
                player.spawnParticle(Particle.SCULK_CHARGE_POP, player.getLocation().add(0, 1, 0), 0, vel.getX(), vel.getY(), vel.getZ(), 1);
                player.spawnParticle(Particle.SCULK_CHARGE_POP, player.getLocation().add(0, 1, 0), 0, vel.getX(), vel.getY(), vel.getZ(), 1);
                player.spawnParticle(Particle.SCULK_CHARGE_POP, player.getLocation().add(0, 1, 0), 0, vel.getX(), vel.getY(), vel.getZ(), 1);


            }
        };

        BukkitTask sniffPlayersTask = Bukkit.getScheduler().runTaskTimer(plugin, sniffPlayers, 0, 6);

        // stop ability after 30 seconds
        Bukkit.getScheduler().runTaskLater(plugin, task -> {
            sniffPlayersTask.cancel();
        }, 600);
    }

}


