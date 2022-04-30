package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.NumberUtils
import org.bukkit.Bukkit
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object SchmoneyPlaceholder {
    private val PREVIOUS_BALANCES: MutableMap<UUID, Double> = ConcurrentHashMap<UUID, Double>()

    fun init() {
        PlaceholderManager.registerPlaceholder(
            PlayerPlaceholder(
                EcoMCPlugin.instance,
                "money_change"
            ) {
                val prev = PREVIOUS_BALANCES.getOrDefault(it.uniqueId, 0.0)
                if (prev == 0.0) {
                    return@PlayerPlaceholder ""
                }
                val diff: Double = EconomyManager.getBalance(it) - prev
                if (Math.abs(diff) > 0.01) {
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
                PREVIOUS_BALANCES[player.uniqueId] = EconomyManager.getBalance(player)
            }
        }, 20, 60)
    }
}