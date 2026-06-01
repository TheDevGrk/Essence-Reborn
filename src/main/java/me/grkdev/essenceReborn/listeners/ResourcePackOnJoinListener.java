package me.grkdev.essenceReborn.listeners;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ResourcePackOnJoinListener implements Listener {

    // forces players to download resource pack (for essence crystal) when joining
    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent e){
        e.getPlayer().setResourcePack(
                "https://download.mc-packs.net/pack/e4dfab320aae5898264380f65493d93aab2dcb96.zip",
                "e4dfab320aae5898264380f65493d93aab2dcb96",
                true,
                Component.text("A resource pack is required to play on this server!"));

    }
}
