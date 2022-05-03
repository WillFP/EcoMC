package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.command.impl.Subcommand
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.items.Items
import com.willfp.eco.util.StringUtils
import com.willfp.eco.util.savedDisplayName
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil

class CommandCrystals(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "crystals",
    "ecomc.crystals",
    true
) {
    init {
        this.addSubcommand(CommandGive(plugin))
            .addSubcommand(CommandGet(plugin))
            .addSubcommand(CommandShop(plugin))
            .addSubcommand(CommandGeodes(plugin))
            .addSubcommand(CommandExpirePotion(plugin))
    }

    override fun onExecute(player: CommandSender, args: List<String>) {
        player as Player
        player.openCrystalShop()
    }
}

private class CommandGive(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "give",
    "ecomc.crystals.give",
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
            sender.sendMessage(plugin.langYml.getMessage("must-specify-amount"))
            return
        }

        val amount = args[1].toIntOrNull()

        if (amount == null) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-amount"))
            return
        }

        if (args.getOrNull(2) == "geode") {
            val onlinePlayer = Bukkit.getPlayer(player.uniqueId) ?: return
            repeat(amount) {
                DropQueue(onlinePlayer)
                    .addItem(Items.lookup("ecomc:geode_1").item)
                    .addItem(Items.lookup("ecomc:geode_2").item)
                    .addItem(Items.lookup("ecomc:geode_3").item)
                    .forceTelekinesis()
                    .push()
            }
            sender.sendMessage(
                plugin.langYml.getMessage("gave-crystals", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%player%", player.savedDisplayName)
                    .replace("%amount%", amount.toString())
            )
        } else {
            player.crystals += amount
            sender.sendMessage(
                plugin.langYml.getMessage("gave-crystals", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%player%", player.savedDisplayName)
                    .replace("%amount%", amount.toString())
            )
        }
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
                arrayOf(1, 2, 3, 4, 5).map { it.toString() },
                completions
            )
        }

        if (args.size == 3) {
            StringUtil.copyPartialMatches(
                args[2],
                listOf("geode"),
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
    "ecomc.crystals.get",
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

        sender.sendMessage(
            plugin.langYml.getMessage("crystal-amount", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.savedDisplayName)
                .replace("%amount%", player.crystals.toString())
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

        return completions
    }
}

private class CommandShop(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "shop",
    "ecomc.crystals.shop",
    false
) {
    override fun onExecute(player: CommandSender, args: List<String>) {
        if (player !is Player) return
        player.openCrystalShop()
    }
}

private class CommandGeodes(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "geodes",
    "ecomc.crystals.geodes",
    false
) {
    override fun onExecute(player: CommandSender, args: List<String>) {
        if (player !is Player) return
        player.openGeodesMenu()
    }
}

private class CommandExpirePotion(
    plugin: EcoPlugin
) : Subcommand(
    plugin,
    "expirepotion",
    "ecomc.crystals.expirepotion",
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

        player.expireCrystalPotion()

        sender.sendMessage(
            plugin.langYml.getMessage("expired-potion", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                .replace("%player%", player.savedDisplayName)
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

        return completions
    }
}