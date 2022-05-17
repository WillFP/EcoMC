package com.willfp.ecomc

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecobosses.events.BossTryDropItemEvent
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.stats.Stat
import org.bukkit.event.EventHandler

class BossFortune : Stat("boss_fortune") {
    override fun loadConfig(): Config {
        return BossFortuneConfig()
    }

    @EventHandler
    fun handle(event: BossTryDropItemEvent) {
        val player = event.player ?: return

        val level = EcoSkillsAPI.getInstance().getStatLevel(player, this)

        if (level == 0) {
            return
        }

        event.chance *= 1 + (config.getDouble("bonus") * level)
    }

    private class BossFortuneConfig : BaseConfig(
        "bossfortune",
        EcoMCPlugin.instance,
        true,
        ConfigType.YAML
    )
}
