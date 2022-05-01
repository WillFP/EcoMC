package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import com.willfp.ecomc.crystals.CommandCrystals
import com.willfp.ecomc.crystals.crystals
import com.willfp.ecomc.crystals.initCrystalShop
import org.bukkit.event.Listener

class EcoMCPlugin : EcoPlugin() {
    override fun handleEnable() {
        LevelPlaceholder.register(this)
        initCrystalShop(this)
        SchmoneyPlaceholder.init()

        PlaceholderManager.registerPlaceholder(
            PlayerPlaceholder(
                this,
                "crystals",
            ) {
                it.crystals.toNiceString()
            }
        )
    }

    override fun loadPluginCommands(): List<PluginCommand> {
        return listOf(
            CommandCrystals(this)
        )
    }

    override fun handleReload() {
        SchmoneyPlaceholder.createTheRunnable(this)
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            KeyDropListener(this),
            Hardinator(this),
            EntityYeeter(),
            SpawnProtection(this)
        )
    }

    companion object {
        @JvmStatic
        lateinit var instance: EcoMCPlugin
            private set
    }

    init {
        instance = this
    }
}
