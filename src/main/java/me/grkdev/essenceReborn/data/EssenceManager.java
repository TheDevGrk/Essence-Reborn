package me.grkdev.essenceReborn.data;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.grkdev.essenceReborn.EssenceReborn;
import me.grkdev.essenceReborn.PowerManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class EssenceManager {

    public static int getEssence(Player player, EssenceTypes type){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, type.name().toLowerCase() + "_essence");

        if (pdc.has(key)){ // can access normally if they already have the essence in their pdc
            return pdc.getOrDefault(key, PersistentDataType.INTEGER, 0);
        }

        // if the player hasn't yet gotten any of this essence, add it to their pdc and set it to 0
        pdc.set(key, PersistentDataType.INTEGER, 0);
        return 0;
    }

    public static void addEssence(Player player, EssenceTypes type, int amount){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, type.name().toLowerCase() + "_essence");

        if (pdc.has(key)){
            pdc.set(key, PersistentDataType.INTEGER, amount + pdc.getOrDefault(key, PersistentDataType.INTEGER, 0));
            return;
        }

        // if the player hasn't yet gotten any of this essence, add it to their pdc and set it to the amount being added
        pdc.set(key, PersistentDataType.INTEGER, amount);
    }


    public static void removeEssence(Player player, EssenceTypes type, int amount){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, type.name().toLowerCase() + "_essence");

        if (pdc.has(key)){
            pdc.set(key, PersistentDataType.INTEGER, Math.max(pdc.getOrDefault(key, PersistentDataType.INTEGER, 0) - amount, 0));
            return;
        }

        // if the player hasn't yet gotten any of this essence, add it to their pdc and set it to 0 (no negative essence allowed)
        pdc.set(key, PersistentDataType.INTEGER, 0);
    }


    //-------------------------

    // checks whether the player's essence passes the specified threshold
    public static boolean hasWeakPowerUnlocked(Player player, EssenceTypes type){
        return getEssence(player, type) >= type.weakPowerThreshold;
    }
    public static boolean hasStrongPowerUnlocked(Player player, EssenceTypes type){
        return getEssence(player, type) >= type.strongPowerThreshold;
    }
    public static boolean hasPassivePowerUnlocked(Player player, EssenceTypes type){
        return getEssence(player, type) >= type.passivePowerThreshold;
    }

    //-------------------------



    public static void setActiveEssence(Player player, EssenceTypes type){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "active_essence");

        pdc.set(key, PersistentDataType.STRING, type.toString());

        //turn off necromancy mode if switched from wither
        player.getPersistentDataContainer().set(new NamespacedKey(plugin, "necromancy_mode"), PersistentDataType.BOOLEAN, false);

        // reset enderman reach
        player.getAttribute(Attribute.BLOCK_INTERACTION_RANGE).setBaseValue(4.5);
        player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).setBaseValue(3);

        //reset warden swift sneak
        player.getAttribute(Attribute.SNEAKING_SPEED).setBaseValue(.3);

        // start passive power
        type.power.onPassivePower(player);

        // reset cooldowns
        PowerManager.hideAllCooldownBars(player);
        PowerManager.resetWeakPowerCooldown(player);
        PowerManager.resetStrongPowerCooldown(player);


    }

    public static EssenceTypes getActiveEssence(Player player){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "active_essence");

        if (pdc.has(key)){
            return EssenceTypes.getEssenceType(pdc.getOrDefault(key, PersistentDataType.STRING, "DEFAULT"));
        }

        pdc.set(key, PersistentDataType.STRING, "DEFAULT");
        return EssenceTypes.DEFAULT;
    }

    //-------------------------





    public static void showEssenceSwitchDialog(Player player, boolean switcher){
        // add each essence type to the list that can be clicked through on the singleOption button
        List<SingleOptionDialogInput.OptionEntry> essenceTypes = new ArrayList<>();
        List<DialogBody> essenceDescriptions = EssenceDescriptions.createEssenceListDialog(player);

        // loop through all essence types and properly add them to the list above to be displayed in the dialog
        for (EssenceTypes type : EssenceTypes.values()){
            if (type == EssenceTypes.DEFAULT || type == getActiveEssence(player)) continue;

            essenceTypes.add(SingleOptionDialogInput.OptionEntry.create(type.toString(), type.getFormattedName(), false));
        }


        // changes the confirm identifier sent with the dialogue depending on how the dialogue is created so that it can be differentiated in the listener
        String confirmKey;
        if (switcher){
            confirmKey = "essence_reborn:essence_switch/confirm/essence_switcher";
        }
        else{
            confirmKey = "essence_reborn:essence_switch/confirm/select_essence_command";
        }


        // build dialog to show when switching essences
        Dialog essenceSwitchDialog = Dialog.create(builder -> builder.empty()
                .base(DialogBase.builder(Component.text("Select the Essence to Switch to"))
                        .inputs(List.of(
                                DialogInput.singleOption("essenceType", 200, essenceTypes, Component.text("Essence type: ", NamedTextColor.LIGHT_PURPLE), true)
                        ))
                        .body(essenceDescriptions)
                        .build()
                )
                .type(DialogType.confirmation( //add the 2 buttons at the bottom
                        ActionButton.create(
                                Component.text("Select Essence", NamedTextColor.GREEN),
                                Component.text("Click to switch your active essence"),
                                150,
                                DialogAction.customClick(Key.key(confirmKey), null)
                        ),
                        ActionButton.create(
                                Component.text("Cancel", NamedTextColor.DARK_RED),
                                Component.text("Click to cancel the switch (will not use up Essence Switcher)"),
                                150,
                                null
                        )
                ))
        );


        player.showDialog(essenceSwitchDialog);
    }


    public static ItemStack createEssenceSwithcer(){
        ItemStack switcher = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = switcher.getItemMeta();
        meta.customName(Component.text("Essence Switcher", NamedTextColor.DARK_AQUA).decorate(TextDecoration.BOLD, TextDecoration.ITALIC));
        meta.lore(List.of(Component.text("Right click to select a new essence!")));
        meta.setGlider(true); // used to tell if a nether star is an essence switcher, have had issues in past with pdc on items

        switcher.setItemMeta(meta);
        return switcher;
    }
    //-------------------------


}
