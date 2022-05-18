package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.toNiceString
import com.willfp.ecomc.crystals.CommandCrystals
import com.willfp.ecomc.crystals.CommandGeodes
import com.willfp.ecomc.crystals.CrystalEnchantType
import com.willfp.ecomc.crystals.CrystalLuck
import com.willfp.ecomc.crystals.CrystalOnSkillLevelUp
import com.willfp.ecomc.crystals.CrystalPotionHandler
import com.willfp.ecomc.crystals.CrystalPotions
import com.willfp.ecomc.crystals.Geodes
import com.willfp.ecomc.crystals.HDBCrystalPriceHandler
import com.willfp.ecomc.crystals.PreventGeodePlace
import com.willfp.ecomc.crystals.crystals
import com.willfp.ecomc.trails.CommandTrails
import com.willfp.ecomc.trails.TrailGUI
import com.willfp.ecomc.trails.tickTrail
import org.bukkit.Bukkit
import org.bukkit.event.Listener

class EcoMCPlugin : EcoPlugin() {
    override fun handleEnable() {
        CrystalLuck() // Init crystal luck
        BossFortune() // Init boss fortune
        CrystalEnchantType() // Init enchant type
        JankyPlaceholder.init(this)
        TrailGUI.init(this)
        CrystalPotions.init(this)
        Geodes.init(this)
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
            SecretEcoMCAdminCommand(this),
            CommandGeodes(this),
            CommandTrails(this),
            CommandBosses(this)
        )
    }

    override fun handleReload() {
        SchmoneyPlaceholder.createTheRunnable(this)
        CrystalPotionHandler.initRunnable(this)
        EntityYeeter.pollForTPS(this)
        this.scheduler.runTimer(1200, 1200) { CrystalLuck.resetLimiter() }

        var tick = 0
        this.scheduler.runAsyncTimer(1, 1) {
            tick++
            for (player in Bukkit.getOnlinePlayers()) {
                player.tickTrail(tick)
            }
        }
    }

    override fun loadListeners(): List<Listener> {
        return listOf(
            KeyDropListener(this),
            Hardinator(this),
            EntityYeeter(),
            SpawnProtection(this),
            Baninator(),
            PreventGeodePlace(),
            CrystalOnSkillLevelUp(),
            CrystalPotionHandler(this),
            BuyCrystalPotionsSmh(this),
            HDBCrystalPriceHandler(this),
            MovementListener(),
            NoColonCommands()
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
