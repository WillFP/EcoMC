package com.willfp.ecomc

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import java.util.UUID

private val lastPositions = mutableMapOf<UUID, Vector>()

val Player.lastPosition: Vector
    get() = lastPositions[this.uniqueId] ?: Vector(0.0, 0.0, 0.0)

class MovementListener : Listener {
    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        lastPositions[event.player.uniqueId] = event.from.toVector()
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        lastPositions[event.player.uniqueId] = event.player.location.toVector()
    }
}