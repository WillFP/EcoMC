package com.willfp.ecomc;

import com.willfp.eco.core.integrations.placeholder.PlaceholderManager;
import com.willfp.eco.core.placeholder.PlayerPlaceholder;
import com.willfp.eco.util.StringUtils;
import org.bukkit.potion.PotionEffectType;

public class LevelPlaceholder {
    public static void register() {
        PlaceholderManager.registerPlaceholder(new PlayerPlaceholder(
                EcoMCPlugin.getInstance(),
                "level",
                player -> {
                    int level;
                    try {
                        level = Integer.parseInt(PlaceholderManager.translatePlaceholders("%battlepass_tier%", player));
                    } catch (NumberFormatException e) {
                        return "§80✯";
                    }
                    String color;

                    if (level == 1) {
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
                    } else if (level < 100) {
                        color = "§d";
                    } else {
                        color = "§a§l";
                    }

                    return color + level + "✯";
                }
        ));

        PlaceholderManager.registerPlaceholder(new PlayerPlaceholder(
                EcoMCPlugin.getInstance(),
                "color",
                player -> {
                    return PlaceholderManager.translatePlaceholders("%luckperms_prefix%", player).substring(0, 2);
                }
        ));

        PlaceholderManager.registerPlaceholder(new PlayerPlaceholder(
                EcoMCPlugin.getInstance(),
                "rank",
                player -> {
                    String prefix = PlaceholderManager.translatePlaceholders("%luckperms_prefix%", player);
                    return prefix.replace(" ", "")
                            .replace("|", "");
                }
        ));

        PlaceholderManager.registerPlaceholder(new PlayerPlaceholder(
                EcoMCPlugin.getInstance(),
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

        PlaceholderManager.registerPlaceholder(new PlayerPlaceholder(
                EcoMCPlugin.getInstance(),
                "heart",
                player -> {
                    if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                        return StringUtils.format("&6❤");
                    } else {
                        return StringUtils.format("&c❤");
                    }
                }
        ));
    }
}
