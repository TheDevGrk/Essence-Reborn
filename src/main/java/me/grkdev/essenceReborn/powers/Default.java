package me.grkdev.essenceReborn.powers;

import me.grkdev.essenceReborn.Power;
import me.grkdev.essenceReborn.data.EssenceTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class Default extends Power {

    private EssenceTypes essenceType = EssenceTypes.DEFAULT;

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
    public void onWeakPower(Player player) {

    }

    @Override
    public void onStrongPower(Player player) {

    }
}
