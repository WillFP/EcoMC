package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import com.willfp.ecomc.crystals.CommandCrystals
import com.willfp.ecomc.crystals.CrystalEnchantType
import com.willfp.ecomc.crystals.CrystalLuck
import com.willfp.ecomc.crystals.CrystalPotionHandler
import com.willfp.ecomc.crystals.PreventGeodePlace
import com.willfp.ecomc.crystals.crystals
import com.willfp.ecomc.crystals.initCrystalPotions
import com.willfp.ecomc.crystals.initCrystalShop
import com.willfp.ecomc.crystals.initCustomPotions
import com.willfp.ecomc.crystals.initGeodes
import org.bukkit.event.Listener

class EcoMCPlugin : EcoPlugin() {
    override fun handleEnable() {
        CrystalLuck() // Init crystal luck
        CrystalEnchantType() // Init enchant type
        initCrystalPotions(this)
        initCustomPotions(this)
        initGeodes(this)
        LevelPlaceholder.register(this)
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
            CommandCrystals(this),
            SecretEcoMCAdminCommand(this)
        )
    }

    override fun handleReload() {
        SchmoneyPlaceholder.createTheRunnable(this)
        CrystalPotionHandler.initRunnable(this)
        initCrystalShop(this)
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            KeyDropListener(this),
            Hardinator(this),
            EntityYeeter(),
            SpawnProtection(this),
            Baninator(),
            PreventGeodePlace(),
            CrystalPotionHandler(this),
            BuyCrystalPotionsSmh(this)
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
