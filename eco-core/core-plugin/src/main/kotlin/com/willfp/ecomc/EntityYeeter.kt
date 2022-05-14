package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

class EntityYeeter : Listener {
    @EventHandler
    fun handle(event: EntitySpawnEvent) {
        if (event.entityType == EntityType.BAT) {
            event.isCancelled = true
        }
        if (event.entityType == EntityType.ENDERMAN) {
            if (NumberUtils.randFloat(0.0, 100.0) < 50) {
                event.isCancelled = true
            }
        }
        if (event.entityType == EntityType.BLAZE) {
            if (NumberUtils.randFloat(0.0, 100.0) < 50) {
                event.isCancelled = true
            }
        }
        if (event.entityType == EntityType.CAVE_SPIDER) {
            if (NumberUtils.randFloat(0.0, 100.0) > 60) {
                event.isCancelled = true
            }
        }
    }

    companion object {
        fun pollForTPS(plugin: EcoPlugin) {
            // Run every 10 seconds
            plugin.scheduler.runTimer(200, 200) {
                if (Bukkit.getTPS().any { it < 19.9 }) {
                    plugin.logger.info("TPS is dropping - Killing spawner mobs!")
                    for (world in Bukkit.getWorlds()) {
                        if (world.name.lowercase().contains("spawn")) {
                            continue
                        }

                        for (entity in world.entities.toList()) {
                            if (entity.fromMobSpawner()) {
                                entity.remove()
                            }
                        }
                    }
                }
            }
        }
    }
}
