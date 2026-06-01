package me.grkdev.essenceReborn.data;

import io.papermc.paper.registry.data.dialog.body.DialogBody;
import me.grkdev.essenceReborn.PowerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EssenceDescriptions {

    public static final Component CHICKEN_POWER_WEAK = Component.text("All players in a 15 block radius experience time like a falling feather does: slowly.");
    public static final Component CHICKEN_POWER_STRONG = Component.text("S-s-surprise ch-ch-ch-chickens, Steve's Surprise Chickens yea they're surprising as hell!\n" +
            "Throws a volley of eggs around you. When an egg hits a player, a surprise chicken takes the player for a joyride before exploding!");
    public static final Component CHICKEN_POWER_PASSIVE = Component.text("Chickens don't take fall damage, why should you?");

    public static final Component COW_POWER_WEAK = Component.text("Drain the lactic-life from each nearby player! You steal a heart from every player in a 5 block radius for 30 seconds.");
    public static final Component COW_POWER_STRONG = Component.text("MOOOOOOOOOOOve or be moved! Summons 3 Bulls that rush forward, damaging any player in their way!");
    public static final Component COW_POWER_PASSIVE = Component.text("Your milky nature prevents you from being affected by various negative effects whilst making you extra beefy!");

    public static final Component PIGLIN_POWER_WEAK = Component.text("The essence of gold surrounds you, enveloping your body in solid plated gold making you invulnerable for 10 seconds.\" +\n" +
            "            \"\\n The weight of the gold weakens you and makes you unable to attack whilst gilded!");
    public static final Component PIGLIN_POWER_STRONG = Component.text("Shiny? Shiny... Shiny! Ooooh shiny... My precious shiny, my precious... It's mine, all mine!" +
            "\nWhilst active, players within a 7 block radius are unable to eat golden foods.");
    public static final Component PIGLIN_POWER_PASSIVE = Component.text("The nether is your home. Therefore, you gain several positive effects whilst in the nether.");

    public static final Component BREEZE_POWER_WEAK = Component.text("Whoooosh!! Your breezy nature allows you to make fast, fluid, and powerful dashes through the air.");
    public static final Component BREEZE_POWER_STRONG = Component.text("The wind is an element of utter chaos! " +
            "\nA powerful whirlwind bursts forth, blowing nearby players high into the air and scrambling their inventories!");
    public static final Component BREEZE_POWER_PASSIVE = Component.text("As a creature of the wind, you thrive at higher elevations. " +
            "\nAs you gain elevation you gain increasingly powerful effects!");

    public static final Component ENDERMAN_POWER_WEAK = Component.text("An enderman's eyes are the center of its being, and within those eyes lies the secret to its teleportation:" +
            "\nQuantum Leaps! Look at another player to swap positions with them.");
    public static final Component ENDERMAN_POWER_STRONG = Component.text("Endermen hate water and have recently developed a technology to destroy it completely! " +
            "\nWhile this ability is active, all water within a certain radius of you is DESTROYED!");
    public static final Component ENDERMAN_POWER_PASSIVE = Component.text("Endermen are very tall and lanky cheaters, and so are you! You have reach hacks!");

    public static final Component WITHER_POWER_WEAK = Component.text("The power of the Wither instilled within you allows you to shoot powerful wither skulls as if you yourself were a wither!");
    public static final Component WITHER_POWER_STRONG = Component.text("Withers are the powerful leaders of the undead armies. Therefore, you too can command the armies of the undead." +
            "\nSneak + Left Click to toggle Necromancy Mode On/Off. Whilst in necromancy mode, any mobs you kill become part of your undead army (up to 15 mobs)." +
            "\nActivate this ability to summon your undead army to fight for you! Whilst in necromancy mode, killing any of your summoned mobs will remove them from your army.");
    public static final Component WITHER_POWER_PASSIVE = Component.text("Your decrepit hands bring decay to all that they touch. Any entity you hit will have a chance to gain the Wither effect.");

    public static final Component WARDEN_POWER_WEAK = Component.text("Harnesses the immense power of sound and fires a powerful Sonic Blast where you are looking!");
    public static final Component WARDEN_POWER_STRONG = Component.text("Wardens have a very keen sense of smell. This ability allows you to sniff out all of your opponents' locations.");
    public static final Component WARDEN_POWER_PASSIVE = Component.text("As a warden, sound is your specialty, and therefore you know how to sneak around very well. " +
            "\nYour walking speed is not decreased when you are sneaking.");


    // creates a list of all the essence names and descriptions to be used in a dialog
    public static List<DialogBody> createEssenceListDialog(Player player){
        List<DialogBody> essenceDialogs = new ArrayList<>();
        for (EssenceTypes type : EssenceTypes.values()){
            if (type == EssenceTypes.DEFAULT) continue;



            int essence = EssenceManager.getEssence(player, type);
            Component text = type.getFormattedName().append(Component.text( " essence: " + essence + "\n"));
            // add weak power description if unlocked
            text = text.append(Component.text("\n\n" + type.weakPowerName + "").decorate(TextDecoration.UNDERLINED, TextDecoration.BOLD),
                    Component.text("\nUnlocks at: " + type.weakPowerThreshold + " essence\n"));


            Component weakDescription = Component.text("Description: \n");
            if (PowerManager.hasWeakPowerUnlocked(player, type)){
                weakDescription = weakDescription.append(type.weakPowerDescription );
                weakDescription = weakDescription.append(Component.text("\nCooldown: " + type.weakPowerCooldown + " seconds"));
            }
            else{
                weakDescription = weakDescription.append(Component.text("???", NamedTextColor.DARK_RED));
                weakDescription = weakDescription.append(Component.text("\nCooldown: ???", NamedTextColor.DARK_RED));
            }

            text = text.append(weakDescription);

            // add strong power description if unlocked
            text = text.append(Component.text("\n\n" + type.strongPowerName + "").decorate(TextDecoration.UNDERLINED, TextDecoration.BOLD),
                    Component.text("\nUnlocks at: " + type.strongPowerThreshold + " essence\n"));


            Component strongDescription = Component.text("Description: \n");
            if (PowerManager.hasWeakPowerUnlocked(player, type)){
                strongDescription = strongDescription.append(type.strongPowerDescription );
                strongDescription = strongDescription.append(Component.text("\nCooldown: " + type.strongPowerCooldown + " seconds"));
            }
            else{
                strongDescription = strongDescription.append(Component.text("???", NamedTextColor.DARK_RED));
                strongDescription = strongDescription.append(Component.text("\nCooldown: ???", NamedTextColor.DARK_RED));
            }

            text = text.append(strongDescription);

            // add passive power description if unlocked
            text = text.append(Component.text("\n\n" + type.passivePowerName + "").decorate(TextDecoration.UNDERLINED, TextDecoration.BOLD),
                    Component.text("\nUnlocks at: " + type.passivePowerThreshold + " essence\n"));


            Component passiveDescription = Component.text("Description: \n");
            if (PowerManager.hasWeakPowerUnlocked(player, type)){
                passiveDescription = passiveDescription.append(type.passivePowerDescription );
            }
            else{
                passiveDescription = passiveDescription.append(Component.text("???", NamedTextColor.DARK_RED));
                passiveDescription = passiveDescription.append(Component.text("\nCooldown: ???", NamedTextColor.DARK_RED));
            }

            text = text.append(passiveDescription);



            essenceDialogs.add(DialogBody.plainMessage(text, 800));
        }
        return essenceDialogs;
    }
}
