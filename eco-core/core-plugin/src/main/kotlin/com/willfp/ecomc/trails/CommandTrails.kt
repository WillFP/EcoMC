package com.willfp.ecomc.trails

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.savedDisplayName
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class CommandTrails(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "trails",
    "ecomc.trails",
    true
) {
    init {
        this.addSubcommand(CommandSet(plugin))
    }

    override fun onExecute(player: CommandSender, args: List<String>) {
        player as Player
        TrailGUI.open(player)
    }
}

private class CommandSet(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "set",
    "ecomc.trails.set",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-player"))
            return
        }

        @Suppress("DEPRECATION")
        val player = Bukkit.getOfflinePlayer(args[0])

        if (!player.hasPlayedBefore()) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        if (args.size < 2) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-trail"))
            return
        }

        val particle = try {
            Particle.valueOf(args[1].uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }

        if (particle == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-trail"))
            return
        }

        player.trail = particle
        sender.sendMessage(
            plugin.langYml.getMessage("set-trail", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.savedDisplayName)
                .replace("%trail%", particle.toString())
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.isEmpty()) {
            return Bukkit.getOnlinePlayers().map { it.name }
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Bukkit.getOnlinePlayers().map { it.name },
                completions
            )
        }

        if (args.size == 2) {
            StringUtil.copyPartialMatches(
                args[1],
                Particle.values().map { it.name },
                completions
            )
        }

        return completions
    }
}
