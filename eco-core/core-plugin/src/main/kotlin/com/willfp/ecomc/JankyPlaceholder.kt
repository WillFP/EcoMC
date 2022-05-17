package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.skills.Skills
import kotlin.math.min

object JankyPlaceholder {
    private val defaultProg = arrayOf(
        50, 125, 200,
        300, 500, 750,
        1000, 1500, 2000,
        3500, 5000, 7500,
        10000, 15000, 20000,
        30000, 50000, 75000,
        100000, 200000, 300000,
        400000, 500000, 600000,
        700000, 800000, 900000,
        1000000, 1100000, 1200000,
        1300000, 1400000, 1500000,
        1600000, 1700000, 1800000,
        1900000, 2000000, 2100000,
        2200000, 2300000, 2400000,
        2500000, 2600000, 2750000,
        2900000, 3100000, 3400000,
        3700000, 4000000, 5000000,
        2147483647
    )


    fun init(plugin: EcoPlugin) {
        for (skill in Skills.values()) {
            PlaceholderManager.registerPlaceholder(
                PlayerPlaceholder(
                    plugin,
                    "${skill.id}_levelprice",
                ) {
                    val level = EcoSkillsAPI.getInstance().getSkillLevel(it, skill)
                    val progress = EcoSkillsAPI.getInstance().getSkillProgress(it, skill)
                    val requiredXp = defaultProg[level + 1] * progress

                    val sneaky = if (level > 10) 1.5 else 1.0
                    val basePrice = 265 + (requiredXp * sneaky / 22500) * 35

                    val price = min(basePrice.toInt(), 6435)
                    price.toString()
                }
            )
        }
    }
}
