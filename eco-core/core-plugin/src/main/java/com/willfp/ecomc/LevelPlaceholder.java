package com.willfp.ecomc;

import com.willfp.eco.core.integrations.economy.EconomyManager;
import com.willfp.eco.core.integrations.placeholder.PlaceholderEntry;
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.util.NumberUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LevelPlaceholder {
    public static void register() {
        PlaceholderManager.registerPlaceholder(new PlaceholderEntry(
                "level",
                player -> {
                    int level;
                    try {
                        level = Integer.parseInt(PlaceholderManager.translatePlaceholders("%battlepass_tier%", player));
                    } catch (NumberFormatException e) {
                        return "§80✯";
                    }
                    String color;

                    if(level == 1) {
                        color = "§8";
                    } else if (level < 10) {
                        color = "§7";
                    } else if (level < 20) {
                        color = "§f";
                    } else if (level < 30) {
                        color = "§6";
                    } else if (level < 40) {
                        color = "§3";
                    } else if (level < 50) {
                        color = "§c";
                    } else if (level < 60) {
                        color = "§e";
                    } else if (level < 70) {
                        color = "§9";
                    } else if (level < 80) {
                        color = "§b";
                    } else if (level < 90) {
                        color = "§a";
                    } else if(level < 100) {
                        color = "§d";
                    } else {
                        color = "§a§l";
                    }

                    return color + level + "✯";
                },
                true
                ));

        PlaceholderManager.registerPlaceholder(new PlaceholderEntry(
                "color",
                 player -> {
                     return PlaceholderManager.translatePlaceholders("%luckperms_prefix%", player).substring(0, 2);
                 }
        ));

        PlaceholderManager.registerPlaceholder(new PlaceholderEntry(
                "rank",
                 player -> {
                    String prefix = PlaceholderManager.translatePlaceholders("%luckperms_prefix%", player);
                    return prefix.replace(" ", "")
                            .replace("|", "");
                 }
        ));

        PlaceholderManager.registerPlaceholder(new PlaceholderEntry(
                "tag",
                 player -> {
                     String tag = PlaceholderManager.translatePlaceholders("%deluxetags_tag%", player);
                     if (tag == null || tag.isEmpty() || tag.isBlank()) {
                         return "";
                     } else {
                         return " " + tag;
                     }
                 }
        ));
    }
}
