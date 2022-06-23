package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

enum class Rank(
    val price: Int
) {
    DEFAULT(0),
    IRON(600),
    COBALT(1325),
    DIAMOND(2750),
    NETHERITE(4500),
    MANYULLYN(6750)
}

val Player.rank: Rank
    get() {
        if (this.hasPermission("ecomc.rank.manyullyn")) {
            return Rank.MANYULLYN
        }
        if (this.hasPermission("ecomc.rank.netherite")) {
            return Rank.NETHERITE
        }
        if (this.hasPermission("ecomc.rank.diamond")) {
            return Rank.DIAMOND
        }
        if (this.hasPermission("ecomc.rank.cobalt")) {
            return Rank.COBALT
        }
        if (this.hasPermission("ecomc.rank.iron")) {
            return Rank.IRON
        }
        return Rank.DEFAULT
    }

class RankSellMultiplier : Listener {
    @EventHandler
    fun onSell(event: ShopSellEvent) {
        val player = event.player
        if (player.rank == Rank.MANYULLYN) {
            event.price *= 1.5
        }
    }
}

object RankCostPlaceholder {
    fun init(plugin: EcoPlugin) {
        for (rank in Rank.values()) {
            PlaceholderManager.registerPlaceholder(
                PlayerPlaceholder(
                    plugin,
                    "${rank}_price",
                ) {
                    val currentRank = it.rank
                    val priceDiscount = currentRank.price

                    val price = rank.price - priceDiscount
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
