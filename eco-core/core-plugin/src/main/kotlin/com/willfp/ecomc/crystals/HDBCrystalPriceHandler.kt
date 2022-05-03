package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.fast
import me.arcaniax.hdb.api.PlayerClickHeadEvent
import org.bukkit.Sound
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

        if (player.crystals < event.price) {
            player.sendMessage(plugin.langYml.getMessage("buy-crystals"))
            return
        }

        player.sendMessage(
            plugin.langYml.getMessage("bought-from-crystal")
                .replace("%item%", event.head.fast().displayName)
        )
        player.playSound(
            player.location,
            Sound.BLOCK_NOTE_BLOCK_PLING,
            1f,
            1.5f
        )
        player.crystals -= event.price.toInt()
        DropQueue(player)
            .addItem(event.head)
            .forceTelekinesis()
            .setLocation(player.eyeLocation)
            .push()
    }
}
