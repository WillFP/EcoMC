package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.drops.DropQueue
import me.arcaniax.hdb.api.PlayerClickHeadEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class HDBCrystalPriceHandler(private val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun handle(event: PlayerClickHeadEvent) {
        val player = event.player

        if (player.isOp) {
            return
        }

        event.isCancelled = true

        if (player.crystals < 12) {
            player.sendMessage(plugin.langYml.getMessage("buy-crystals"))
            return
        }

        player.sendMessage(plugin.langYml.getMessage("bought-head"))
        player.crystals -= 12
        DropQueue(player)
            .addItem(event.head)
            .forceTelekinesis()
            .setLocation(player.eyeLocation)
            .push()
    }
}
