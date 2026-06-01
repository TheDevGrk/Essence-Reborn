package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Chicken extends Power {

    private EssenceTypes essenceType = EssenceTypes.CHICKEN;

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
        for (Player p : player.getLocation().getNearbyPlayers(15)){
            if (!PowerManager.isTrusted(player, p) && !player.equals(p)){
                // add potion effects and change attributes to make all nearby non trusted players slower
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 400, 4));
                p.getAttribute(Attribute.GRAVITY).setBaseValue(.005);
                p.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(.13);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 4));
                p.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 2));


                //reset the attributes when the potion effects run out
                Bukkit.getScheduler().runTaskLater(plugin, () ->{
                    p.getAttribute(Attribute.GRAVITY).setBaseValue(.08);
                    p.getAttribute(Attribute.JUMP_STRENGTH).setBaseValue(0.41999998688697815);
                }, 400);


                // create an item display of a feather to show over the effected players' heads
                ItemDisplay featherDisplay = p.getWorld().spawn(p.getLocation().add(0, 3, 0), ItemDisplay.class, entity -> {
                    entity.setItemStack(ItemStack.of(Material.FEATHER));
                });

                // display particles around effected players for the duration of the ability
                int[] i = {0};
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    // cancel particles and item display once effects run out
                    if (i[0] > 200){
                        featherDisplay.remove();
                        task.cancel();
                    }

                    //display particles
                    p.getWorld().spawnParticle(Particle.WHITE_ASH, p.getLocation().add(0, 1, 0), 10, 1, 1, 1, .5);
                    p.getWorld().spawnParticle(Particle.ITEM, p.getLocation().add(0, 1, 0),
                            3, 1, 1, 1, 0, ItemStack.of(Material.FEATHER));
                    p.getWorld().spawnParticle(Particle.CLOUD, p.getLocation().add(0, 1, 0),
                            1, 1, 1, 1, 0);
                    i[0]++;

                    // move the feather item display to constantly over their head
                    featherDisplay.teleport(p.getLocation().add(0, 3, 0));


                    // cap the player's velocity so they can't move quickly through air
                    Vector vel = p.getVelocity();
                    vel.setX(Math.clamp(vel.getX(), -0.2, 0.2));
                    vel.setY(Math.clamp(vel.getY(), -0.2, 0.2));
                    vel.setZ(Math.clamp(vel.getZ(), -0.2, 0.2));
                    p.setVelocity(vel);

                }, 0, 2);
            }
        }
    }

    @Override
    public void onStrongPower(Player player) {
        // ?? make eggs not spawn chickens?
        int eggs = 60;
        for (int i = 0; i < eggs; i++){
            // shoot eggs out in a circle
            double angle = (2 * Math.PI / eggs) * i;
            double xVel = Math.cos(angle);
            double zVel = Math.sin(angle);


            int finalI = i;
            player.getWorld().spawn(player.getLocation(), Egg.class, egg -> {
                egg.setVelocity(new Vector(xVel, .5, zVel));
                egg.setShooter(player);

                //every third egg is a cooked chicken, a feather, or an egg
                if (finalI % 2 == 0){
                    egg.setItem(ItemStack.of(Material.COOKED_CHICKEN));
                }
                else if (finalI % 3 ==0){
                    egg.setItem(ItemStack.of(Material.FEATHER));
                }

                egg.getPersistentDataContainer().set(new NamespacedKey(plugin, "is_surpise_egg"), PersistentDataType.BOOLEAN, true);
            });


        }
    }
}
