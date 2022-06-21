package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.ecomc.crystals.hasCrystalPotion
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent

class BuyCrystalPotionsSmh(
    private val plugin: EcoPlugin
) : Listener {
    private val commands = listOf(
        "/reforge",
        "/reforges",
        "/shop",
        "/shopgui",
        "/guishop",
        "/hdb",
        "/heads",
        "/headdb"
    )

    @EventHandler
    fun onSendCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player

        if (player.hasCrystalPotion || player.isOp) {
            return
        }

        if (commands.none { event.message.startsWith(it) }) {
            return
        }

        event.isCancelled = true
        player.sendMessage(plugin.langYml.getMessage("buy-crystal-potion"))
    }
}