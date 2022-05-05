package com.willfp.ecomc.trails

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.util.NumberUtils
import com.willfp.ecomc.EcoMCPlugin
import com.willfp.ecomc.lastPosition
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.PI

fun Player.tickTrail(tick: Int) {
    val particle = this.trail ?: return

    val dist = this.lastPosition.distance(this.location.toVector())

    if (dist > 0.2) {
        return
    }

    val sign = if (tick % 2 == 0) -1 else 1

    val x = NumberUtils.fastCos(tick / (2 * PI) * 1) * sign * 1.2
    val y = this.height * (tick % 60) / 60
    val z = NumberUtils.fastSin(tick / (2 * PI) * 1) * sign * 1.2
    val vector = Vector(x, y, z)

    val loc = this.location.clone().add(vector)
    this.world.spawnParticle(particle, loc, 1, 0.0, 0.0, 0.0, 0.0, null)
}

private val trailKey = PersistentDataKey(
    EcoMCPlugin.instance.namespacedKeyFactory.create("trail"),
    PersistentDataKeyType.STRING,
    ""
)

var OfflinePlayer.trail: Particle?
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
