package com.willfp.ecomc.crystals

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.events.EntityDeathByEntityEvent
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.tryAsPlayer
import com.willfp.ecomc.EcoMCPlugin
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.stats.Stat
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

private class CrystalLuckConfig : BaseConfig(
    "crystalluck",
    EcoMCPlugin.instance,
    true,
    ConfigType.YAML
)

class CrystalLuck : Stat("crystal_luck") {
    override fun loadConfig(): Config {
        return CrystalLuckConfig()
    }

    @EventHandler
    fun handle(event: BlockBreakEvent) {
        val player = event.player

        val level = EcoSkillsAPI.getInstance().getStatLevel(player, this)

        if (level == 0) {
            return
        }

        if (NumberUtils.randFloat(0.0, 100.0) < level * config.getDouble("chance-per-level")) {
            DropQueue(player)
                .addItem(Items.lookup("ecomc:geode").item)
                .forceTelekinesis()
                .push()
            player.sendMessage(EcoMCPlugin.instance.langYml.getMessage("crystal-luck"))
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

        if (NumberUtils.randFloat(0.0, 100.0) < level * config.getDouble("chance-per-level-mobs") * multiplier) {
            DropQueue(player)
                .addItem(Items.lookup("ecomc:geode").item)
                .forceTelekinesis()
                .push()
            player.sendMessage(EcoMCPlugin.instance.langYml.getMessage("crystal-luck"))
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
    }
}
