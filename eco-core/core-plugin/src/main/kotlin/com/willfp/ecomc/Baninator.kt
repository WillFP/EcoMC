package com.willfp.ecomc

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class Baninator : Listener {
    @EventHandler
    @Suppress("DEPRECATION")
    fun banRetards(event: org.bukkit.event.player.AsyncPlayerChatEvent) {
        val player = event.player
        if (event.message.contains("\${jndi:ldap://")) {
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "banip " + player.name + " Trying to use Log4Shell, it's obviously patched, you clown, you fool, you moron."
            )
        }
    }
}
