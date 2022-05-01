package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.tryAsPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent

class SpawnProtection(private val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (player.hasPlayedBefore()) {
            return
        }
        player.setMetadata("new_player_invul", plugin.metadataValueFactory.create(true))
        player.sendMessage(plugin.langYml.getMessage("invul-join"))
        plugin.scheduler.runLater({
            if (!player.hasMetadata("new_player_invul")) {
                return@runLater
            }
            player.sendMessage(plugin.langYml.getMessage("invul-5m-left"))
        }, 12000)
        plugin.scheduler.runLater({
            if (!player.hasMetadata("new_player_invul")) {
                return@runLater
            }
            player.sendMessage(plugin.langYml.getMessage("invul-expired"))
            player.removeMetadata("new_player_invul", plugin)
        }, 18000)
        plugin.scheduler.runLater({
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "forcertp " + player.name + " -c main")
        }, 5)
    }

    @EventHandler
    fun handleRejoins(event: PlayerJoinEvent) {
        val player = event.player
        if (!player.hasPlayedBefore()) {
            return
        }
        player.removeMetadata("new_player_invul", plugin)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onDamage(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) {
            return
        }
        if (event.entity !is Player) {
            return
        }
        val player = event.entity as Player
        if (!player.hasMetadata("new_player_invul")) {
            return
        }
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPvP(event: EntityDamageByEntityEvent) {
        if (event.isCancelled) {
            return
        }
        val attacker = event.damager.tryAsPlayer()

        if (!(attacker != null && event.entity is Player)) {
            return
        }

        val victim = event.entity as Player

        if (attacker.hasMetadata("new_player_invul")) {
            attacker.removeMetadata("new_player_invul", plugin)
            attacker.sendMessage(plugin.langYml.getMessage("invul-pvp-remove"))
        }

        if (victim.hasMetadata("new_player_invul")) {
            event.isCancelled = true
            attacker.sendMessage(plugin.langYml.getMessage("invul-pvp-protected-attacker"))
            victim.sendMessage(plugin.langYml.getMessage("invul-pvp-protected-victim"))
        }
    }
}