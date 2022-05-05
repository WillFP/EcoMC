package com.willfp.ecomc

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class NoColonCommands : Listener {
    @EventHandler
    fun onSendCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if (player.isOp) {
            return
        }

        val cmd = event.message.split(" ")[0]

        if (!cmd.contains(":")) {
            return
        }

        event.isCancelled = true
    }
}