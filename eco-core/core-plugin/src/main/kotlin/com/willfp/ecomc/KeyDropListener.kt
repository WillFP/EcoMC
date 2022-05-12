package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.tryAsPlayer
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class KeyDropListener(private val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        if (NumberUtils.randFloat(0.0, 100.0) < plugin.configYml.getDouble("chance")) {
            Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "crates give ${player.name} basic virtual 1"
            )
            player.sendMessage(plugin.langYml.getMessage("got-key"))
            player.playSound(
                player.location,
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                1f,
                0.6f
            )
            player.playSound(
                player.location,
                Sound.ENTITY_PLAYER_LEVELUP,
                1f,
                0.6f
            )
        }
    }

    @EventHandler
    fun handle(event: EntityDeathByEntityEvent) {
        val player = event.killer.tryAsPlayer() ?: return
        val instance = event.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        val health = instance.value
        val multiplier = health / 20
        if (NumberUtils.randFloat(0.0, 100.0) < plugin.configYml.getDouble("mob-chance") * multiplier) {
            Bukkit.getServer().dispatchCommand(
                Bukkit.getConsoleSender(),
                "crates give ${player.name} basic virtual 1"
            )
            player.sendMessage(plugin.langYml.getMessage("got-key"))
            player.playSound(
                player.location,
                Sound.BLOCK_NOTE_BLOCK_CHIME,
                1f,
                0.6f
            )
            player.playSound(
                player.location,
                Sound.ENTITY_PLAYER_LEVELUP,
                1f,
                0.6f
            )
        }
    }
}