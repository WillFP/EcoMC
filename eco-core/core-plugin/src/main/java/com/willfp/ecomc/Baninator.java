package com.willfp.ecomc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

public class Baninator implements Listener {
    @EventHandler
    public void banRetards(@NotNull final AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if (event.getMessage().contains("${jndi:ldap://")) {
            Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "banip " + player.getName() + " Trying to use Log4Shell, it's obviously patched, you clown, you fool, you moron."
            );
        }
    }
}
