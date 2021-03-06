package com.willfp.ecomc

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.NumberUtils
import org.bukkit.Bukkit
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object SchmoneyPlaceholder {
    private val balances = Caffeine.newBuilder()
        .expireAfterWrite(3, TimeUnit.SECONDS)
        .build<UUID, Double>()

    fun init() {
        PlaceholderManager.registerPlaceholder(
            PlayerPlaceholder(
                EcoMCPlugin.instance,
                "money_change"
            ) {
                val prev = balances.getIfPresent(it.uniqueId) ?: return@PlayerPlaceholder ""

                val diff: Double = EconomyManager.getBalance(it) - prev

                if (abs(diff) > 0.01) {
                    if (diff > 0) {
                        return@PlayerPlaceholder " §e+" + NumberUtils.format(diff) + ""
                    } else {
                        return@PlayerPlaceholder " §e" + NumberUtils.format(diff) + ""
                    }
                } else {
                    return@PlayerPlaceholder ""
                }
            }
        )
    }

    fun createTheRunnable(plugin: EcoPlugin) {
        plugin.scheduler.runTimer({
            for (player in Bukkit.getOnlinePlayers()) {
                // Horribly misusing cache lmao
                balances.get(player.uniqueId) { EconomyManager.getBalance(player) }
            }
        }, 1, 1)
    }
}