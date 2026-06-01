package me.grkdev.essenceReborn;

import me.grkdev.essenceReborn.data.EssenceManager;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public class PowerManager {

    private static BukkitScheduler scheduler = plugin.getServer().getScheduler();

    public static void activateWeakPower(Player player){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        EssenceTypes essence = EssenceManager.getActiveEssence(player);


        if (!essence.power.isEnabled()){
            player.sendMessage(Component.text("Your currently selected Essence (" + essence + ") is currently disabled!", NamedTextColor.RED));
            return;
        }

        if (!onWeakCooldown(player) && hasWeakPowerUnlocked(player)){
            setWeakPowerCooldown(player);
            essence.power.onWeakPower(player);
        }
    }

    public static void activateStrongPower(Player player){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        EssenceTypes essence = EssenceManager.getActiveEssence(player);

        if (!essence.power.isEnabled()){
            player.sendMessage(Component.text("Your currently selected Essence (" + essence + ") is currently disabled!", NamedTextColor.RED));
            return;
        }

        if (!onStrongCooldown(player) && hasStrongPowerUnlocked(player)){
            essence.power.onStrongPower(player);
            setStrongPowerCooldown(player);
        }
    }



    // ------------------------------------------------------------------------------------------------------------------------------------------

    public static void setWeakPowerCooldown(Player player){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        EssenceTypes essence = EssenceManager.getActiveEssence(player);


        final Component name = Component.text(essence.weakPowerName);
        final BossBar cooldownBar = BossBar.bossBar(name, 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
        player.showBossBar(cooldownBar);

        scheduler.runTaskTimer(plugin, task ->{
            // decrement the cooldown bar by 1 second each second
            if (cooldownBar.progress() >= 0.01F){
                cooldownBar.progress(cooldownBar.progress() - 1F /essence.weakPowerCooldown / 20F);
                return;
            }

            player.hideBossBar(cooldownBar);
            task.cancel();
        }, 0, 1);

        // set the cooldown start time to be compared later
        pdc.set(new NamespacedKey(plugin, "weakCooldownStart"), PersistentDataType.LONG, System.nanoTime());
    }

    public static void setStrongPowerCooldown(Player player){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        EssenceTypes essence = EssenceManager.getActiveEssence(player);


        final Component name = Component.text(essence.strongPowerName);
        final BossBar cooldownBar = BossBar.bossBar(name, 1, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
        player.showBossBar(cooldownBar);

        scheduler.runTaskTimer(plugin, task ->{
            // decrement the cooldown bar by 1 second each second
            if (cooldownBar.progress() >= 0.01F){
                cooldownBar.progress(cooldownBar.progress() - 1F /essence.strongPowerCooldown / 20F);
                return;
            }

            player.hideBossBar(cooldownBar);
            task.cancel();
        }, 0, 1);

        // set the cooldown start time to be compared later
        pdc.set(new NamespacedKey(plugin, "strongCooldownStart"), PersistentDataType.LONG, System.nanoTime());
    }

    public static void resetWeakPowerCooldown(Player player){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "weakCooldownStart"), PersistentDataType.LONG, 0L);
    }

    public static void resetStrongPowerCooldown(Player player){
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(new NamespacedKey(plugin, "strongCooldownStart"), PersistentDataType.LONG, 0L);
    }

    public static void hideAllCooldownBars(Player player){
        for (BossBar bar : player.activeBossBars()){
            player.hideBossBar(bar);
        }
    }
    // ------------------------------------------------------------------------------------------------------------------------------------------

    //checks if the specified player is on the specified cooldown
    public static boolean onWeakCooldown(Player player){
        EssenceTypes essence = EssenceManager.getActiveEssence(player);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        long cooldownTime = pdc.getOrDefault(new NamespacedKey(plugin, "weakCooldownStart"), PersistentDataType.LONG, 0L);

        return essence.weakPowerCooldown > (System.nanoTime() - cooldownTime) / 1000000000L;
    }

    public static boolean onStrongCooldown(Player player){
        EssenceTypes essence = EssenceManager.getActiveEssence(player);
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        long cooldownTime = pdc.getOrDefault(new NamespacedKey(plugin, "strongCooldownStart"), PersistentDataType.LONG, 0L);

        return essence.strongPowerCooldown > (System.nanoTime() - cooldownTime) / 1000000000L;
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------

    //checks whether the specified power type is unlocked based on the provided thresholds
    public static boolean hasPassiveUnlocked(Player player){
        EssenceTypes essence = EssenceManager.getActiveEssence(player);
        int essenceAmount = EssenceManager.getEssence(player, essence);

        return essenceAmount >= essence.passivePowerThreshold;
    }

    public static boolean hasWeakPowerUnlocked(Player player){
        EssenceTypes essence = EssenceManager.getActiveEssence(player);
        int essenceAmount = EssenceManager.getEssence(player, essence);

        return essenceAmount >= essence.weakPowerThreshold;
    }

    public static boolean hasStrongPowerUnlocked(Player player){
        EssenceTypes essence = EssenceManager.getActiveEssence(player);
        int essenceAmount = EssenceManager.getEssence(player, essence);

        return essenceAmount >= essence.strongPowerThreshold;
    }



    //checks whether the specified power type is unlocked based on the provided thresholds
    public static boolean hasPassiveUnlocked(Player player, EssenceTypes essence){
        int essenceAmount = EssenceManager.getEssence(player, essence);

        return essenceAmount >= essence.passivePowerThreshold;
    }

    public static boolean hasWeakPowerUnlocked(Player player, EssenceTypes essence){
        int essenceAmount = EssenceManager.getEssence(player, essence);

        return essenceAmount >= essence.weakPowerThreshold;
    }

    public static boolean hasStrongPowerUnlocked(Player player, EssenceTypes essence){
        int essenceAmount = EssenceManager.getEssence(player, essence);

        return essenceAmount >= essence.strongPowerThreshold;
    }

    // ------------------------------------------------------------------------------------------------------------------------------------------


    public static boolean isTrusted(Player player, Player trustee){
        PersistentDataContainer pdc = player.getPersistentDataContainer();

        NamespacedKey key = new NamespacedKey(plugin, "trusted_players");
        List<String> names = pdc.getOrDefault(key, PersistentDataType.LIST.strings(), new ArrayList<String>());

        return names.contains(trustee.getName());
    }


}
