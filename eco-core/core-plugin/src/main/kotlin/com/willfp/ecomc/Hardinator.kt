package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.util.tryAsPlayer
import org.bukkit.entity.Mob
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class Hardinator(private val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun takeMore(event: EntityDamageByEntityEvent) {
        if (event.damager is Mob) {
            event.damage = event.damage * plugin.configYml.getDouble("incoming-damage-multiplier")
        }
    }

    @EventHandler
    fun dealLess(event: EntityDamageByEntityEvent) {
        val player = event.damager.tryAsPlayer() ?: return
        event.damage = event.damage * plugin.configYml.getDouble("outgoing-damage-multiplier")
    }
}
