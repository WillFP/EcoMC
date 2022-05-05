package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.StringUtils
import org.bukkit.potion.PotionEffectType

object LevelPlaceholder {
    fun register(plugin: EcoPlugin) {
        PlaceholderManager.registerPlaceholder(
            PlayerPlaceholder(
                plugin,
                "level"
            ) {

                val level: Int
                level = try {
                    PlaceholderManager.translatePlaceholders("%battlepass_tier%", it).toInt()
                } catch (e: NumberFormatException) {
                    return@PlayerPlaceholder "§80✯"
                }
                val color: String
                color = if (level == 1) {
                    "§8"
                } else if (level < 10) {
                    "§7"
                } else if (level < 20) {
                    "§f"
                } else if (level < 30) {
                    "§6"
                } else if (level < 40) {
                    "§3"
                } else if (level < 50) {
                    "§c"
                } else if (level < 60) {
                    "§e"
                } else if (level < 70) {
                    "§9"
                } else if (level < 80) {
                    "§b"
                } else if (level < 90) {
                    "§a"
                } else if (level < 100) {
                    "§d"
                } else {
                    "§a§l"
                }
                "$color$level✯"
            }
        )
        PlaceholderManager.registerPlaceholder(PlayerPlaceholder(
            plugin,
            "color"
        ) {
            PlaceholderManager.translatePlaceholders("%luckperms_prefix%", it).substring(0, 2)
        })
        PlaceholderManager.registerPlaceholder(PlayerPlaceholder(
            plugin,
            "rank"
        ) {
            val prefix = PlaceholderManager.translatePlaceholders("%luckperms_prefix%", it)
            prefix.replace(" ", "")
                .replace("|", "")
        })
        PlaceholderManager.registerPlaceholder(PlayerPlaceholder(
            plugin,
            "tag"
        ) {
            val tag = PlaceholderManager.translatePlaceholders("%deluxetags_tag%", it)
            if (tag == null || tag.isEmpty() || tag.isBlank()) {
                ""
            } else {
                " $tag"
            }
        })
        PlaceholderManager.registerPlaceholder(PlayerPlaceholder(
            plugin,
            "heart"
        ) {
            if (it.hasPotionEffect(PotionEffectType.WITHER)) {
                StringUtils.format("&0❤")
            } else if (it.hasPotionEffect(PotionEffectType.POISON)) {
                StringUtils.format("&3❤")
            } else if (it.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                StringUtils.format("&6❤")
            } else {
                StringUtils.format("&c❤")
            }
        })
    }
}
