package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class NoColonCommands(
    private val plugin: EcoPlugin
) : Listener {
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
        player.sendMessage(plugin.langYml.getMessage("unknown-command"))
    }
}