package com.willfp.ecomc.trails

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandTrails(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "trails",
    "ecomc.trails",
    true
) {
    override fun onExecute(player: CommandSender, args: List<String>) {
        player as Player
        TrailGUI.open(player)
    }
}
