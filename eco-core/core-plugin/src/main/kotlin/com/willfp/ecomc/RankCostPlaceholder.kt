package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import org.bukkit.entity.Player

object RankCostPlaceholder {
    private val rankCosts = arrayOf(
        600,
        1325,
        2750,
        4500,
        6750
    )

    private val ranks = arrayOf(
        "iron",
        "cobalt",
        "diamond",
        "netherite",
        "manyullyn"
    )

    private fun Player.getRank(): String? {
        if (this.hasPermission("ecomc.rank.manyullyn")) {
            return "manyullyn"
        }
        if (this.hasPermission("ecomc.rank.netherite")) {
            return "netherite"
        }
        if (this.hasPermission("ecomc.rank.diamond")) {
            return "diamond"
        }
        if (this.hasPermission("ecomc.rank.cobalt")) {
            return "cobalt"
        }
        if (this.hasPermission("ecomc.rank.iron")) {
            return "iron"
        }
        return null
    }

    fun init(plugin: EcoPlugin) {
        for (rank in ranks) {
            PlaceholderManager.registerPlaceholder(
                PlayerPlaceholder(
                    plugin,
                    "${rank}_price",
                ) {
                    val currentRank = it.getRank()
                    var priceDiscount = 0
                    if (currentRank != null) {
                        priceDiscount = rankCosts[ranks.indexOf(currentRank)]
                    }

                    val price = rankCosts[ranks.indexOf(rank)] - priceDiscount
                    if (price <= 0) {
                        "0"
                    } else {
                        price.toString()
                    }
                }
            )
        }
    }
}
