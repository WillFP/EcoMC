package com.willfp.ecomc

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class Baninator : Listener {
    @EventHandler
    fun banRetards(event: AsyncPlayerChatEvent) {
        val player = event.player
        if (event.message.contains("\${jndi:ldap://")) {
            Bukkit.dispatchCommand(
                Bukkit.getConsoleSender(),
                "banip " + player.name + " Trying to use Log4Shell, it's obviously patched, you clown, you fool, you moron."
            )
        }
    }
}