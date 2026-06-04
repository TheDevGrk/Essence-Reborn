package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class Skeleton extends Power {
    private EssenceTypes essenceType = EssenceTypes.SKELETON;
    public static NamespacedKey enchantedQuiverKey = new NamespacedKey(plugin, "enchantedQuiver");
    public static NamespacedKey skeletalGraspKey = new NamespacedKey(plugin, "skeletalGraspActive");
    public static PotionEffectType[] negativePotionEffectTypes = {PotionEffectType.BLINDNESS, PotionEffectType.DARKNESS, PotionEffectType.HUNGER, PotionEffectType.INFESTED, PotionEffectType.INSTANT_DAMAGE,
            PotionEffectType.MINING_FATIGUE, PotionEffectType.LEVITATION, PotionEffectType.NAUSEA, PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS,
            PotionEffectType.WITHER, PotionEffectType.WEAVING, PotionEffectType.GLOWING, PotionEffectType.SLOW_FALLING};



    public EssenceTypes getEssenceType() {
        return essenceType;
    }

    public void setEssenceType(EssenceTypes essenceType) {
        this.essenceType = essenceType;
    }





    @Override
    public void activatePassivePower(Player player) {

    }

    @Override
    public void deactivatePassivePower(Player player) {
    }







    @Override
    public void onWeakPower(Player player){
        // sound effects
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SKELETON_CONVERTED_TO_STRAY, 25f, 0.5f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, .5f, 10f);




        player.getPersistentDataContainer().set(enchantedQuiverKey, PersistentDataType.BOOLEAN, true);

        // turn off enchanted quiver after 30s
        Bukkit.getScheduler().runTaskLater(plugin, task -> {
            player.getPersistentDataContainer().set(enchantedQuiverKey, PersistentDataType.BOOLEAN, false);
        }, 600);
    }


    @Override
    public void onStrongPower(Player player){
        // sound effects
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_CROSSBOW_QUICK_CHARGE_3, 2f, 0.5f);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IMITATE_SKELETON, 5f, 1f);
        Bukkit.getScheduler().runTaskLater(plugin, task -> { //delay sound
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SKELETON_HURT, .6f, .1f);
        }, 20);




        player.getPersistentDataContainer().set(skeletalGraspKey, PersistentDataType.BOOLEAN, true);


    }
}
