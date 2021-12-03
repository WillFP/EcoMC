package com.willfp.ecomc;

import com.willfp.eco.core.integrations.economy.EconomyManager;
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.util.NumberUtils;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class SchmoneyPlaceholder {
    private static final EcoMCPlugin PLUGIN = EcoMCPlugin.getInstance();
    private static final Map<UUID, Double> PREVIOUS_BALANCES = new ConcurrentHashMap<>();

    public void init() {
        PlaceholderManager.registerPlaceholder(new PlaceholderEntry(
                "money_change",
                player -> {
                    double prev = PREVIOUS_BALANCES.getOrDefault(player.getUniqueId(), 0.0);
                    if (prev == 0) {
                        return "";
                    }

                    double diff = EconomyManager.getBalance(player) - prev;
                    if (Math.abs(diff) > 0.01) {
                        if (diff > 0) {
                            return " §e+" + NumberUtils.format(diff) + "";
                        } else {
                            return " §e" + NumberUtils.format(diff) + "";
                        }
                    } else {
                        return "";
                    }
                }
        ));
    }

    public void createTheRunnable() {
        PLUGIN.getScheduler().runTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PREVIOUS_BALANCES.put(player.getUniqueId(), EconomyManager.getBalance(player));
            }
        }, 20, 60);
    }
}
