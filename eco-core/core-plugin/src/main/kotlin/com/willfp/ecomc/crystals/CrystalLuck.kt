package com.willfp.ecomc.crystals

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomc.EcoMCPlugin
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.stats.Stat
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import kotlin.math.log

class CrystalLuck : Stat("crystal_luck") {
    override fun loadConfig(): Config {
        return CrystalLuckConfig()
    }

    private fun getProbability(level: Int): Double {
        val base = config.getDouble("base")
        val scalar = config.getDouble("scalar")
        val initial = config.getDouble("initial")
        return base / 15 * log(scalar * level + 1, base) + initial
    }

    private fun dropRandomGeode(player: Player, location: Location) {
        val weight = NumberUtils.randInt(0, 100)

        val item = if (weight < 70) {
            Geodes[1]
        } else if (weight < 90) {
            Geodes[2]
        } else {
            Geodes[3]
        }

        DropQueue(player)
            .addItem(item)
            .setLocation(location)
            .push()

        player.sendMessage(
            EcoMCPlugin.instance.langYml.getMessage("crystal-luck")
                .replace("%geode%", item.fast().displayName)
        )
        player.playSound(
            player.location,
            Sound.BLOCK_NOTE_BLOCK_BELL,
            1f,
            1.2f
        )
        player.playSound(
            player.location,
            Sound.ENTITY_PLAYER_LEVELUP,
            1f,
            1.8f
        )
    }

    @EventHandler
    fun handle(event: BlockBreakEvent) {
        val player = event.player

        val level = EcoSkillsAPI.getInstance().getStatLevel(player, this)

        if (level == 0) {
            return
        }

        val chance = getProbability(level)

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            dropRandomGeode(player, event.block.location)
        }
    }

    @EventHandler
    fun handle(event: EntityDeathByEntityEvent) {
        val player = event.killer.tryAsPlayer() ?: return
        val instance = event.victim.getAttribute(Attribute.GENERIC_MAX_HEALTH) ?: return
        val health = instance.value
        val multiplier = health / 20

        val level = EcoSkillsAPI.getInstance().getStatLevel(player, this)

        if (level == 0) {
            return
        }

        val chance = getProbability(level) * config.getDouble("mobs-times-more")

        if (NumberUtils.randFloat(0.0, 100.0) < chance) {
            dropRandomGeode(player, event.victim.location)
        }
    }

    private class CrystalLuckConfig : BaseConfig(
        "crystalluck",
        EcoMCPlugin.instance,
        true,
        ConfigType.YAML
    )
}
