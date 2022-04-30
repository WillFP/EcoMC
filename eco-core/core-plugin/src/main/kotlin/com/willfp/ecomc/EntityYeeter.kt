package com.willfp.ecomc

import com.willfp.eco.util.NumberUtils
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
        if (event.entityType == EntityType.CAVE_SPIDER) {
            if (NumberUtils.randFloat(0.0, 100.0) > 60) {
                event.isCancelled = true
            }
        }
    }
}