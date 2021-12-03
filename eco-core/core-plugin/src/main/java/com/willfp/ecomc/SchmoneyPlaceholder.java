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
    private static final Map<UUID, String> CHANGES = new ConcurrentHashMap<>();

    public void init() {
        PlaceholderManager.registerPlaceholder(new PlaceholderEntry(
                "money_change",
                player -> {
                    return CHANGES.getOrDefault(player.getUniqueId(), "");
                }
        ));
    }

    public void createTheRunnable() {
        PLUGIN.getScheduler().runTimer(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                double prev = PREVIOUS_BALANCES.getOrDefault(player.getUniqueId(), 0.0);
                if (prev == 0) {
                    CHANGES.put(player.getUniqueId(), "");
                    continue;
                }

                double diff = EconomyManager.getBalance(player) - prev;
                PREVIOUS_BALANCES.put(player.getUniqueId(), EconomyManager.getBalance(player));
                if (Math.abs(diff) > 0.01) {
                    CHANGES.put(player.getUniqueId(), " §e(" + NumberUtils.format(diff) + ")");
                } else {
                    CHANGES.put(player.getUniqueId(), "");
                }
            }
        }, 20, 20);
    }
}
