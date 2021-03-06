package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.savedDisplayName
import com.willfp.eco.util.toComponent
import com.willfp.ecomc.crystals.crystals
import com.willfp.ecoskills.api.EcoSkillsAPI
import com.willfp.ecoskills.skills.Skills
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class SecretEcoMCAdminCommand(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "secretecomcadmincommand",
    "ecomc.secretecomcadminpermission",
    false
) {
    init {
        this.addSubcommand(CommandReload(plugin))
            .addSubcommand(CommandGet(plugin))
            .addSubcommand(CommandUpgradeSkill(plugin))
            .addSubcommand(CommandKickAll(plugin))
            .addSubcommand(CommandBroadcastPurchase(plugin))
            .addSubcommand(CommandMostCrystals(plugin))
    }

    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}

private class CommandReload(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "reload",
    "ecomc.secretecomcadminpermission",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        sender.sendMessage(
            plugin.langYml.getMessage("reloaded").replace(
                "%time%", plugin.reloadWithTime().toString()
            )
        )
    }
}

private class CommandMostCrystals(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "mostcrystals",
    "ecomc.secretecomcadminpermission",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val top = Bukkit.getOfflinePlayers().sortedByDescending { it.crystals }
            .subList(0, 10)
            .mapIndexed { index, offlinePlayer -> "${index + 1}: ${offlinePlayer.savedDisplayName}&r: ${offlinePlayer.crystals}" }
            .map { it.formatEco() }
        for (s in top) {
            sender.sendMessage(s)
        }
    }
}

private class CommandUpgradeSkill(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "upgradeskill",
    "ecomc.secretecomcadminpermission",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        if (args.isEmpty()) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-player"))
            return
        }

        val player = Bukkit.getPlayer(args[0])


        if (player == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-player"))
            return
        }

        if (args.size < 2) {
            sender.sendMessage(plugin.langYml.getMessage("must-specify-skill"))
            return
        }

        val skill = Skills.getByID(args[1])

        if (skill == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-skill"))
            return
        }

        val api = EcoSkillsAPI.getInstance()
        api.giveSkillExperience(player, skill, api.getSkillProgressRequired(player, skill).toDouble(), false)

        sender.sendMessage(
            plugin.langYml.getMessage("upgraded-skill", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.savedDisplayName)
                .replace("%skill%", skill.name)
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
                Skills.values().map { it.id },
                completions
            )
        }

        return completions
    }
}

private class CommandGet(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "get",
    "ecomc.secretecomcadminpermission",
    true
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val item = Items.lookup(args.joinToString(" ")).item

        sender as Player

        DropQueue(sender)
            .addItem(item)
            .forceTelekinesis()
            .push()

        sender.sendMessage("Here you go mate")
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        val completions = mutableListOf<String>()

        if (args.isEmpty()) {
            return Items.getCustomItems().map { it.key.toString() }
        }

        if (args.size == 1) {
            StringUtil.copyPartialMatches(
                args[0],
                Items.getCustomItems().map { it.key.toString() },
                completions
            )
        }

        return completions
    }
}


private class CommandKickAll(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "kickall",
    "ecomc.secretecomcadminpermission",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        for (player in Bukkit.getOnlinePlayers()) {
            player.kick(
                "<g:#00ff00>EcoMC</g:#00ffff>&f is having a scheduled restart, come back in a couple minutes!"
                    .formatEco()
                    .toComponent()
            )
        }
    }
}

private class CommandBroadcastPurchase(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "broadcastpurchase",
    "ecomc.broadcastpurchase",
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
