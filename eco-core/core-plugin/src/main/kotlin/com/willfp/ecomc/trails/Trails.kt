package com.willfp.ecomc.trails

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.util.NumberUtils
import com.willfp.ecomc.EcoMCPlugin
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.Vector

fun Player.tickTrail(tick: Int) {
    val particle = this.trail ?: return

    val y = this.height / (tick % 20)
    val x = NumberUtils.fastCos(tick.toDouble()) * 1.5
    val z = NumberUtils.fastSin(tick.toDouble()) * 1.5

    val vector = Vector(x, y, z)
    val loc = this.location.clone().add(vector)
    this.world.spawnParticle(particle, loc, 1, 0.0, 0.0, 1.0, null)
}

private val trailKey = PersistentDataKey(
    EcoMCPlugin.instance.namespacedKeyFactory.create("trail"),
    PersistentDataKeyType.STRING,
    ""
)

var Player.trail: Particle?
    get() {
        val key = this.profile.read(trailKey)
        if (key.isEmpty()) {
            return null
        }

        return Particle.valueOf(key)
    }
    set(value) {
        if (value == null) {
            this.profile.write(trailKey, "")
        } else {
            this.profile.write(trailKey, value.name)
        }
    }
