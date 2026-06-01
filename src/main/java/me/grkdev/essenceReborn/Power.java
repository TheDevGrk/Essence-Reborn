package me.grkdev.essenceReborn;

import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import static me.grkdev.essenceReborn.EssenceReborn.overWorld;
import static me.grkdev.essenceReborn.EssenceReborn.plugin;

public abstract class Power {
//    private boolean enabled = overWorld.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, this.getEssenceType() + "_enabled"), PersistentDataType.BOOLEAN, true);

    public boolean isEnabled() {
        return overWorld.getPersistentDataContainer().getOrDefault(new NamespacedKey(plugin, this.getEssenceType() + "_enabled"), PersistentDataType.BOOLEAN, true);
    }

    public void setEnabled(boolean enabled) {
        overWorld.getPersistentDataContainer().set(new NamespacedKey(plugin, this.getEssenceType() + "_enabled"), PersistentDataType.BOOLEAN, enabled);
    }





    public abstract void onWeakPower(Player player);
    public abstract void onStrongPower(Player player);
    public abstract void onPassivePower(Player player);
    public abstract EssenceTypes getEssenceType();
    public abstract void setEssenceType(EssenceTypes essenceType);

}
