package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.PowerManager;
import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.time.Duration;
import java.util.ArrayList;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Cow extends Power {

    private EssenceTypes essenceType = EssenceTypes.COW;

    public EssenceTypes getEssenceType() {
        return essenceType;
    }

    public void setEssenceType(EssenceTypes essenceType) {
        this.essenceType = essenceType;
    }


    @Override
    public void onPassivePower(Player player) {
        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (player.isOnline() && EssenceManager.getActiveEssence(player) == EssenceTypes.COW){
                if(PowerManager.hasPassiveUnlocked(player)){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 40, 1));
                    //immunities
                    player.removePotionEffect(PotionEffectType.DARKNESS);
                    player.removePotionEffect(PotionEffectType.HUNGER);
                    player.removePotionEffect(PotionEffectType.WITHER);
                    player.removePotionEffect(PotionEffectType.MINING_FATIGUE);
                }
                return;
            }

            task.cancel();
        }, 0, 20);
    }

    @Override
    public void onStrongPower(Player player) {
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_COW_HURT, 10f, 1f);

        ArrayList<Player> damagedPlayers = new ArrayList<>();

        Vector direction = player.getEyeLocation().getDirection().normalize();
        float rushDistance = 15.0f;

        // just in case cows get stuck or go past for some reason
        long startTime = System.nanoTime();
        float timeout = 2f;

        // make 3 cows charge forward and hit the frozen players
        Location endLocation = player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(rushDistance + 1));
        boolean[] atEnd = {false};
        direction.setY(-1);

        player.getWorld().spawn(player.getLocation(), org.bukkit.entity.Cow.class, cow -> {
            cow.setVariant(org.bukkit.entity.Cow.Variant.COLD);
            cow.customName(Component.text("Bull"));
            cow.setCustomNameVisible(true);

            // moves cow and checks for when the cow has reached end of the line
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                if (cow.getLocation().distance(endLocation) < 1.5 || timeout <= (float) (System.nanoTime() - startTime) / 1000000000L){
                    cow.remove();
                    atEnd[0] = true;
                    task.cancel();
                }

                checkBullCollision(player, damagedPlayers, direction, cow);

                cow.addPassenger(player);
                cow.setVelocity(direction);
            }, 0, 1);

        });
        // right cow
        Location spawnLoc = player.getLocation().add(player.getLocation().getDirection().rotateAroundY(-Math.PI / 2)).add(0, .5, 0);
        player.getWorld().spawn(spawnLoc, org.bukkit.entity.Cow.class, cow -> {
            cow.setVariant(org.bukkit.entity.Cow.Variant.COLD);
            cow.customName(Component.text("Bull"));
            cow.setCustomNameVisible(true);

            // moves cow and checks for when the cow has reached end of the line
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                if (atEnd[0]){
                    cow.remove();
                    task.cancel();
                }

                checkBullCollision(player, damagedPlayers, direction, cow);


                cow.setVelocity(direction);
            }, 0, 1);

        });
        // left cow
        spawnLoc = player.getLocation().add(player.getLocation().getDirection().rotateAroundY(Math.PI / 2)).add(0, .5, 0);
        player.getWorld().spawn(spawnLoc, org.bukkit.entity.Cow.class, cow -> {
            cow.setVariant(org.bukkit.entity.Cow.Variant.COLD);
            cow.customName(Component.text("Bull"));
            cow.setCustomNameVisible(true);

            // moves cow and checks for when the cow has reached end of the line
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                if (atEnd[0]){
                    cow.remove();
                    task.cancel();
                }

                checkBullCollision(player, damagedPlayers, direction, cow);


                cow.setVelocity(direction);
            }, 0, 1);

        });


    }

    // checks if the provided bull has collided with any nearby players (each tick)
    private void checkBullCollision(Player player, ArrayList<Player> damagedPlayers, Vector direction, org.bukkit.entity.Cow cow) {
        for (Player p : player.getWorld().getNearbyPlayers(player.getLocation(), 1, 1, 1)){
            if (cow.wouldCollideUsing(BoundingBox.of(p.getLocation(), 1, 1, 1)) && !damagedPlayers.contains(p) && p != player && !PowerManager.isTrusted(player, p)){
                p.damage(8);
                p.setVelocity(new Vector(0, 1.5, 0));
                p.getWorld().playSound(p.getLocation(), Sound.BLOCK_BELL_USE, 5f, 1f);
                damagedPlayers.add(p);

                int[] i = {0};
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (i[0] >= 8){
                        task.cancel();
                    }

                    Component titleComponent;
                    // alternate between red and white
                    if (i[0] % 2 == 0){
                        titleComponent = Component.text("K.O.", NamedTextColor.RED).decoration(TextDecoration.BOLD, true);
                        i[0]++;
                    }
                    else{
                        titleComponent = Component.text("K.O.", NamedTextColor.WHITE).decoration(TextDecoration.BOLD, true);
                        i[0]++;
                    }


                    Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofMillis(100), Duration.ZERO);
                    Title koTitle =  Title.title(titleComponent, Component.empty(), times);

                    p.showTitle(koTitle);
                }, 0, 2);
            }
        }

    }

    @Override
    public void onWeakPower(Player player) {
        // display milk bucket drinking effect for both the player activating the effect
        ItemDisplay milkBucketDisplay = player.getWorld().spawn(player.getEyeLocation().add(player.getEyeLocation().getDirection()), ItemDisplay.class, entity -> {
            entity.setItemStack(ItemStack.of(Material.MILK_BUCKET));


            int[] i = {0};

            // keep bucket in front of face
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                if (i[0] <= 5){
                    entity.teleport(player.getEyeLocation().add(player.getEyeLocation().getDirection()));
                    return;
                }

                task.cancel();
            }, 0, 1);

            // rotate bucket back and forth
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                if (i[0] <= 5){
                    // rotate bucket 50 degrees
                    entity.setTransformation(new Transformation(
                            new Vector3f(),
                            new AxisAngle4f((float) -Math.toRadians(25), 0, 0, 1),
                            new Vector3f(1, 1, 1),
                            new AxisAngle4f()
                    ));
                    // play drinking sound
                    player.playSound(player, Sound.ENTITY_GENERIC_DRINK, 3f, 1f);
                    // increment so we do animation right amount of times
                    i[0]++;
                    return;
                }

                task.cancel();

            }, 0, 10);
            Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                if (i[0] <= 5){
                    // rotate bucket 50 degrees
                    entity.setTransformation(new Transformation(
                            new Vector3f(),
                            new AxisAngle4f((float) Math.toRadians(25), 0, 0, 1),
                            new Vector3f(1,1, 1),
                            new AxisAngle4f()
                    ));
                    // play drinking sound
                    player.playSound(player, Sound.ENTITY_GENERIC_DRINK, 3f, 1f);
                    // increment so we do animation right amount of times
                    i[0]++;
                    return;
                }

                entity.remove();
                task.cancel();
            }, 5, 10);

        });

        for (Player p : player.getLocation().getNearbyPlayers(5, 5, 5)){
            if (PowerManager.isTrusted(player, p) || p.equals(player)) continue;


            // display bucket drinking effect for the effected players
            ItemDisplay bucketDisplay = p.getWorld().spawn(p.getEyeLocation().add(p.getEyeLocation().getDirection()), ItemDisplay.class, entity -> {
                entity.setItemStack(ItemStack.of(Material.BUCKET));


                int[] i = {0};

                // keep bucket in front of face
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (i[0] <= 5){
                        entity.teleport(p.getEyeLocation().add(p.getEyeLocation().getDirection()));
                        return;
                    }

                    task.cancel();
                }, 0, 1);

                // rotate bucket back and forth
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (i[0] <= 5){
                        // rotate bucket 50 degrees
                        entity.setTransformation(new Transformation(
                                new Vector3f(),
                                new AxisAngle4f((float) -Math.toRadians(25), 0, 0, 1),
                                new Vector3f(1, 1, 1),
                                new AxisAngle4f()
                        ));
                        // play drinking sound
                        p.playSound(p, Sound.ENTITY_GENERIC_DRINK, 3f, 1f);
                        // increment so we do animation right amount of times
                        i[0]++;
                        return;
                    }

                    task.cancel();

                }, 0, 10);
                Bukkit.getScheduler().runTaskTimer(plugin, task -> {
                    if (i[0] <= 5){
                        // rotate bucket 50 degrees
                        entity.setTransformation(new Transformation(
                                new Vector3f(),
                                new AxisAngle4f((float) Math.toRadians(25), 0, 0, 1),
                                new Vector3f(1,1, 1),
                                new AxisAngle4f()
                        ));
                        // play drinking sound
                        p.playSound(p, Sound.ENTITY_GENERIC_DRINK, 3f, 1f);
                        // increment so we do animation right amount of times
                        i[0]++;
                        return;
                    }

                    entity.remove();
                    task.cancel();
                }, 5, 10);

            });


            // take 1 heart from targeted player and give it to attacker
            p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - 2.0);
            player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() + 2.0);

            Bukkit.getScheduler().runTaskLater(plugin, task -> {
                // swap the hearts back (note: don't just set to 20 because could be multiple of this ability used on a person)
                p.getAttribute(Attribute.MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.MAX_HEALTH).getBaseValue() + 2.0);
                player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(player.getAttribute(Attribute.MAX_HEALTH).getBaseValue() - 2.0);
            }, 600);

        }
    }
}

