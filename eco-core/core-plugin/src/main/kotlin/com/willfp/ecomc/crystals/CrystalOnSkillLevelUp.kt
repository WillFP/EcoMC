package com.willfp.ecomc.crystals

import com.willfp.ecoskills.api.PlayerSkillLevelUpEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class CrystalOnSkillLevelUp : Listener {
    @EventHandler
    fun onLevelUp(event: PlayerSkillLevelUpEvent) {
        val player = event.player

        if (event.level < 10) {
            return
        }

        player.crystals += 10
    }
}
