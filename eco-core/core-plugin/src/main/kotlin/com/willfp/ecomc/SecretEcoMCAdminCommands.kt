package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
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
