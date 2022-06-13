package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.util.StringUtils
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender

class CommandAnnouncePurchase(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "announcepurchase",
    "ecomc.announcepurchase",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.size < 2) {
            return
        }

        val playerName = args[0]
        val pkg = args.subList(1, args.size).joinToString(" ")

        Bukkit.getServer().broadcast(Component.empty())
        Bukkit.getServer().broadcast(
            StringUtils.toComponent(
                plugin.langYml.getMessage("purchase", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%player%", playerName)
                    .replace("%package%", pkg)
            )
        )
        Bukkit.getServer().broadcast(Component.empty())

        for (player in Bukkit.getOnlinePlayers()) {
            player.playSound(
                player.location,
                Sound.ENTITY_PLAYER_LEVELUP,
                10000f,
                1f
            )
        }
    }
}
