package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.skills.Skills
import kotlin.math.min

object JankyPlaceholder {
    fun init(plugin: EcoPlugin) {
        for (skill in Skills.values()) {
            PlaceholderManager.registerPlaceholder(
                PlayerPlaceholder(
                    plugin,
                    "${skill.id}_levelprice",
                ) {
                    val requiredXp = EcoSkillsAPI.getInstance().getSkillProgressRequired(it, skill)
                    val level = EcoSkillsAPI.getInstance().getSkillLevel(it, skill)
                    val sneaky = if (level > 10) 1.5 else 1.0
                    val basePrice = 265 + (requiredXp * sneaky / 22500) * 35

                    val price = min(basePrice.toInt(), 21125)
                    price.toString()
                }
            )
        }
    }
}