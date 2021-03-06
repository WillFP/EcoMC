package com.willfp.ecomc.trails

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.IEssentials
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.util.NumberUtils
import com.willfp.ecomc.EcoMCPlugin
import com.willfp.ecomc.lastPosition
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import kotlin.math.PI

private val essentials = JavaPlugin.getPlugin(Essentials::class.java)

fun Player.tickTrail(tick: Int) {
    val particle = this.trail ?: return

    val dist = this.lastPosition.distance(this.location.toVector())

    if (this.gameMode == GameMode.SPECTATOR || essentials.getUser(this).isVanished) {
        return
    }

    val vector = if (dist > 0.2) {
        if (tick % 4 != 0) {
            return
        }

        Vector(0.0, -0.5, 0.0)
    } else {
        val sign = if (tick % 2 == 0) -1 else 1

        val x = NumberUtils.fastCos(tick / (2 * PI) * 1) * sign * 1.2
        val y = this.height * (tick % 60) / 60
        val z = NumberUtils.fastSin(tick / (2 * PI) * 1) * sign * 1.2
        Vector(x, y, z)
    }

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
