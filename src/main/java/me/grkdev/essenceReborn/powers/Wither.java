package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import static me.grkdev.essenceReborn.EssenceReborn.*;

public class Wither extends Power {
    // Namespaced keys so don't keep creating new ones
    private static NamespacedKey targetUUIDKey = new NamespacedKey(plugin, "targetUUID");
    private static NamespacedKey targetStartTimeKey = new NamespacedKey(plugin, "targetStartTime");

    private EssenceTypes essenceType = EssenceTypes.WITHER;

    public EssenceTypes getEssenceType() {
        return essenceType;
    }

    public void setEssenceType(EssenceTypes essenceType) {
        this.essenceType = essenceType;
    }


    @Override
    public void onPassivePower(Player player) {

    }

    @Override
    public void onWeakPower(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 3f, 1f);

        // swap player helmet with wither skull during ability
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack skull = new ItemStack(Material.WITHER_SKELETON_SKULL);
        ItemMeta meta = skull.getItemMeta();
        meta.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        player.getInventory().setHelmet(skull);


        int[] i = {0};
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (i[0] >= 5){
                task.cancel();
                player.getInventory().setHelmet(helmet); // give back helmet
                return;
            }
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 3f, 1f);
            WitherSkull witherSkull = player.launchProjectile(WitherSkull.class);
            witherSkull.setCharged(true);
            witherSkull.setShooter(player);
            i[0]++;

        }, 0, 10);
    }








    @Override
    public void onStrongPower(Player player) {
        NamespacedKey key = new NamespacedKey(plugin, "necromancyActive");
        player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);


        Random random = new Random();
        double spawnRadius = 7.0;


        ArrayList<Mob> necromancedMobs = new ArrayList<>();

        // spawn all necromanced mobs that belong to the player
        Location mobLoc = new Location(overWorld, 0, 15000, 0);
        for (Mob mob : overWorld.getNearbyEntitiesByType(Mob.class, mobLoc, 16)){
            if (isOwned(mob, player)){
                // vary spawn location for each mob, ensuring that mob is spawned on surface
                double angle = random.nextDouble() * 2 * Math.PI;
                double distance = random.nextDouble() * spawnRadius;
                Location spawnLoc = player.getLocation().add(Math.cos(angle) * distance, 0, Math.sin(angle) * distance);
                RayTraceResult result = player.getWorld().rayTraceBlocks(spawnLoc.clone().add(0, 15, 0), new Vector(0, -1, 0), 50.0, FluidCollisionMode.NEVER, true);
                if (result != null && result.getHitBlock() != null){
                    spawnLoc.setY(result.getHitBlock().getY() + 1);
                }

                // spawn mob
                Mob copiedMob = (Mob) mob.copy(spawnLoc);
                necromancedMobs.add(copiedMob);


                // store uuid of parent mob to be used if this mob is to be removed from necromancy list for this player
                copiedMob.getPersistentDataContainer().set(new NamespacedKey(plugin, "parentUUID"), PersistentDataType.STRING, mob.getUniqueId().toString());


                // make necromanced mobs glow gray
                Bukkit.getScoreboardManager().getMainScoreboard().getTeam("necromanced").addEntity(copiedMob);
                copiedMob.setGlowing(true);


                // make mob rise from ground
                playEmergeAnimation(copiedMob);
            }
        }






        // mob targeting logic
        BukkitTask pathfindTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Mob mob : necromancedMobs){
                if (mob.getLocation().distanceSquared(player.getLocation()) > 1600) {
                    mob.teleport(player);
                }
                // no matter what, if mob is >20 blocks away from player, make it come back
                if (mob.getLocation().distanceSquared(player.getLocation()) > 400){
                    mob.getPathfinder().moveTo(player.getLocation(), 2);
                    continue;
                }

                // if the mob has a target, go to that target
                if(getTargetUUID(mob) != null){
                    LivingEntity target = (LivingEntity) Bukkit.getEntity(getTargetUUID(mob));


                    // only attack if within 20 blocks of player (prevents back and forth on 40 block border)
                    if (target.getLocation().distanceSquared(player.getLocation()) <= 400){
                        mob.setTarget(target);

                        // make passive mobs attack
                        if (!(mob instanceof Monster)) {
                            passiveAttack(mob, target);
                        }
                    }

                    // remove the target if it is dead or if 2 minutes have passed since the player last hit the target
                    if (target.getHealth() == 0.0 || ((System.nanoTime() - player.getPersistentDataContainer().getOrDefault(targetStartTimeKey, PersistentDataType.LONG, System.nanoTime())) / 1000000000L) >= 120){
                        setTargetUUID(mob, "");
                    }
                    continue;
                }



                // if mob has no target and is further than 10 blocks away, bring it back
                else if (mob.getLocation().distanceSquared(player.getLocation()) > 100){
                    mob.getPathfinder().moveTo(player.getLocation(), 1.5);
                    continue;
                }

                mob.setTarget(null);


            }
        }, 0, 1);


        // remove all necromanced mobs after 2 minutes
        Bukkit.getScheduler().runTaskLater(plugin, task -> {
            for (Mob mob : necromancedMobs){
                mob.remove();
            }

            pathfindTask.cancel();
            player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, false);
        }, 20 * 120);

    }


    //! AI made animation
    public void playEmergeAnimation(LivingEntity mob) {
        Location surface = mob.getLocation();
        double depth = 3;

        // move mob underground to start
        mob.teleport(surface.clone().add(0, -depth, 0));
        mob.setAI(false);
        mob.setInvulnerable(true);
        mob.getWorld().playSound(surface, Sound.ENTITY_WARDEN_EMERGE, .5f, 1f);

        double[] progress = {0};
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (progress[0] >= depth) {
                // fully emerged
                mob.setAI(true);
                mob.setInvulnerable(false);

                // surface shockwave
                for (int i = 0; i < 60; i++) {
                    double angle = (2 * Math.PI / 60) * i;
                    Location pos = surface.clone().add(Math.cos(angle) * 2, 0.1, Math.sin(angle) * 2);
                    mob.getWorld().spawnParticle(Particle.BLOCK, pos, 5, 0.2, 0.1, 0.2, 0.1, Bukkit.createBlockData(Material.DIRT));
                }
                task.cancel();
                return;
            }

            progress[0] += 0.1;
            Location current = surface.clone().add(0, progress[0] - depth, 0);
            mob.teleport(current);

            // dirt particles around mob as it rises
            for (int i = 0; i < 8; i++) {
                double angle = (2 * Math.PI / 8) * i;
                Location pos = current.clone().add(Math.cos(angle) * 0.8, 0, Math.sin(angle) * 0.8);
                mob.getWorld().spawnParticle(Particle.BLOCK, pos, 3, 0.2, 0.2, 0.2, 0.05, Bukkit.createBlockData(Material.DIRT));
            }

            // rumble sound periodically
            if (progress[0] % 0.5 < 0.1) {
                mob.getWorld().playSound(current, Sound.BLOCK_ROOTED_DIRT_BREAK, 0.8f, 0.5f);
            }

        }, 0, 2);
    }

    //checks if the passed entity's owner is the passed player
    public static boolean isOwned(Entity entity, Player player){
        return entity.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, "ownerUUID"), PersistentDataType.STRING, "").equals(player.getUniqueId().toString());
    }


    // methods for handling target UUID in PDC
    public static UUID getTargetUUID(Entity mob){
        String targetUUIDString = mob.getPersistentDataContainer().getOrDefault(targetUUIDKey, PersistentDataType.STRING, "");
        if(targetUUIDString.isEmpty()){
            return null;
        }

        return UUID.fromString(targetUUIDString);
    }

    public static void setTargetUUID(Entity mob, Entity target){
        mob.getPersistentDataContainer().set(targetUUIDKey, PersistentDataType.STRING, target.getUniqueId().toString());
    }

    public static void setTargetUUID(Entity mob, String target){
        mob.getPersistentDataContainer().set(targetUUIDKey, PersistentDataType.STRING, target);
    }

    //make passed passive mob attack target
    public static void passiveAttack(Mob mob, LivingEntity target){
        mob.getPathfinder().moveTo(target, 1.2);
        if (mob.getLocation().distanceSquared(target.getLocation()) <= 1 && !target.getUniqueId().equals(mob.getUniqueId())) {
            target.damage(1);
            target.knockback(.7, mob.getX(), mob.getZ());
        }
    }
}

