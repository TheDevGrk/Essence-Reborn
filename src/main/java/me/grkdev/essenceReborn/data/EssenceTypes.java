package me.grkdev.essenceReborn.data;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.powers.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.EntityType;

import static net.kyori.adventure.text.Component.text;

public enum EssenceTypes {
    DEFAULT(EntityType.ILLUSIONER, 0, 0,0,0, new Default(), 2, 2, "Weak Power", "Strong Power", "Passive Power", Component.text("Weak Power Description"), Component.text("Strong Power Description"), Component.text("Passive Power Description"), NamedTextColor.WHITE),
    CHICKEN(EntityType.CHICKEN, .4, 150, 300, 40, new Chicken(), 100, 240, "Feather Time", "Surprise Chickens", "Feathery", EssenceDescriptions.CHICKEN_POWER_WEAK, EssenceDescriptions.CHICKEN_POWER_STRONG, EssenceDescriptions.CHICKEN_POWER_PASSIVE, NamedTextColor.YELLOW),
    COW(EntityType.COW, .2, 200, 350, 100, new Cow(), 150, 150, "Lactic Drain", "Bull Rush", "Steroid Milk", EssenceDescriptions.COW_POWER_WEAK, EssenceDescriptions.COW_POWER_STRONG, EssenceDescriptions.COW_POWER_PASSIVE, NamedTextColor.GRAY),
    PIGLIN(EntityType.PIGLIN, .1, 160, 300, 90, new Piglin(), 200, 300, "Greed", "Gilded", "Nether-Born", EssenceDescriptions.PIGLIN_POWER_WEAK, EssenceDescriptions.PIGLIN_POWER_STRONG, EssenceDescriptions.PIGLIN_POWER_PASSIVE, NamedTextColor.GOLD),
    BREEZE(EntityType.BREEZE, .2, 35, 115, 90, new Breeze(), 20, 150, "Gust", "Whirlwind", "Being of the Wind", EssenceDescriptions.BREEZE_POWER_WEAK, EssenceDescriptions.BREEZE_POWER_STRONG, EssenceDescriptions.BREEZE_POWER_PASSIVE, NamedTextColor.WHITE),
    ENDERMAN(EntityType.ENDERMAN, .1, 130, 250, 180, new Enderman(), 180, 180, "Quantum Leap", "Hydrophobic", "Lanky Arms", EssenceDescriptions.ENDERMAN_POWER_WEAK, EssenceDescriptions.ENDERMAN_POWER_STRONG, EssenceDescriptions.ENDERMAN_POWER_PASSIVE, NamedTextColor.LIGHT_PURPLE),
    WITHER(EntityType.WITHER, .3, 10, 90, 25, new Wither(), 60, 480, "Exploding Skulls", "Necromancer Lord", "Decaying Touch", EssenceDescriptions.WITHER_POWER_WEAK, EssenceDescriptions.WITHER_POWER_STRONG, EssenceDescriptions.WITHER_POWER_PASSIVE, NamedTextColor.DARK_GRAY),
    WARDEN(EntityType.WARDEN, .2, 55, 75, 25, new Warden(), 90, 120, "Sonic Boom", "Sniff", "Sneaky", EssenceDescriptions.WARDEN_POWER_WEAK, EssenceDescriptions.WARDEN_POWER_STRONG, EssenceDescriptions.WARDEN_POWER_PASSIVE, NamedTextColor.DARK_BLUE),

    ;
//? potentially add a field for the range of essences a mob could drop
    // ? skeleton passive: arrows shot are random type of tipped arrow (can switch between two modes: good and bad)
    // ? for all hostile mob essences, once the player has reached the strong power threshold (max essence needed), that mob won't attack them anymore?

    // !! make every option for the essence types in a yaml file for much easier editing



    public final EntityType entityType;
    public final double dropChance;
    public final int weakPowerThreshold;
    public final int strongPowerThreshold;
    public final int passivePowerThreshold;
    public final Power power;
    public final int weakPowerCooldown;
    public final int strongPowerCooldown;
    public final String weakPowerName;
    public final String strongPowerName;
    public final String passivePowerName;
    public final Component weakPowerDescription;
    public final Component strongPowerDescription;
    public final Component passivePowerDescription;
    public final TextColor color;

    EssenceTypes(EntityType entityType, double dropChance, int weakPowerThreshold, int strongPowerThreshold, int passivePowerThreshold, Power power, int weakPowerCooldown, int strongPowerCooldown, String weakPowerName, String strongPowerName, String passivePowerName, Component weakPowerDescription, Component strongPowerDescription, Component passivePowerDescription, TextColor color) {
        this.entityType = entityType;
        this.dropChance = dropChance;
        this.weakPowerThreshold = weakPowerThreshold;
        this.strongPowerThreshold = strongPowerThreshold;
        this.passivePowerThreshold = passivePowerThreshold;
        this.power = power;
        this.weakPowerCooldown = weakPowerCooldown;
        this.strongPowerCooldown = strongPowerCooldown;
        this.weakPowerName = weakPowerName;
        this.strongPowerName = strongPowerName;
        this.passivePowerName = passivePowerName;
        this.weakPowerDescription = weakPowerDescription;
        this.strongPowerDescription = strongPowerDescription;
        this.passivePowerDescription = passivePowerDescription;
        this.color = color;
    }

    //checks whether the passed entity type has an essence associated with it
    public static boolean isEssence(EntityType type){

        for (EssenceTypes t : values()){
            if (t.entityType == type){
                return true;
            }
        }

        return false;
    }

    //returns the essence type enum associated with the passed entity type
    //defaults to DEFAULT essence type with zeroed stats
    public static EssenceTypes getEssenceType(EntityType type){
        for (EssenceTypes t : values()){
            if (t.entityType == type){
                return t;
            }
        }

        return DEFAULT;
    }

    public static EssenceTypes getEssenceType(String type){
        for (EssenceTypes t : values()){
            if (t.entityType.toString().equals(type.toUpperCase())){
                return t;
            }
        }

        return DEFAULT;
    }


    // returns the name of the essence as a properly formatted text component
    public static Component getFormattedName(EssenceTypes type){
        return Component.text(type.toString().charAt(0) + type.toString().toLowerCase().substring(1), type.color);
    }

    public Component getFormattedName(){
        return Component.text(this.toString().charAt(0) + this.toString().toLowerCase().substring(1), this.color).decorate(TextDecoration.BOLD);
    }
}
