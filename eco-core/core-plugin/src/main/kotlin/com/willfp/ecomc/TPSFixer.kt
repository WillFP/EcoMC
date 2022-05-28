package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.NumberUtils
import org.bukkit.Bukkit
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Tameable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent
import java.lang.Double.min

class TPSFixer : Listener {
    @EventHandler
    fun handle(event: EntitySpawnEvent) {
        if (NumberUtils.randFloat(0.0, 1.0) > spawnRate) {
            event.isCancelled = true
        }

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
        private var spawnRate = 1.0

        fun pollForTPS(plugin: EcoPlugin) {
            // Run every 10 seconds
            plugin.scheduler.runTimer(200, 200) {
                val tps = min(Bukkit.getTPS()[0], Bukkit.getTPS()[1]) // Ignore 15m tps
                val lastTps = Bukkit.getTPS()[0]

                if (tps < 19.9) {
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

                spawnRate = if (tps > 19) {
                    1.0
                } else if (tps > 18) {
                    plugin.logger.info("TPS is $tps; spawn rate = 0.5")
                    0.5
                } else if (tps > 17) {
                    plugin.logger.info("TPS is $tps; spawn rate = 0.25")
                    0.25
                } else if (tps > 15) {
                    plugin.logger.info("TPS is $tps; spawn rate = 0.1")
                    0.1
                } else {
                    plugin.logger.info("TPS is $tps; spawn rate = 0")
                    0.0
                }

                if (lastTps < 18) {
                    plugin.logger.info("Most recent TPS is $lastTps; killing 25% of mobs!")
                    for (world in Bukkit.getWorlds()) {
                        if (world.name.lowercase().contains("spawn")) {
                            continue
                        }

                        for (entity in world.entities.toList()) {
                            if (NumberUtils.randFloat(0.0, 1.0) > 0.25) {
                                continue
                            }

                            if (entity is Tameable || entity is ItemFrame || entity is ArmorStand) {
                                continue
                            }

                            if (entity is LivingEntity) {
                                if (entity.isCustomNameVisible) {
                                    continue
                                }
                            }

                            entity.remove()
                        }
                    }
                }
            }
        }
    }
}
