package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.integrations.shop.ShopSellEvent
import com.willfp.ecomc.crystals.openCrystalShop
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import com.willfp.ecoskills.tryAsPlayer
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

private val pvpEnabledKey = PersistentDataKey(
    EcoMCPlugin.instance.namespacedKeyFactory.create("pvp"),
    PersistentDataKeyType.BOOLEAN,
    true
).player()

class CommandPvptoggle(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "pvptoggle",
    "ecomc.pvptoggle",
    true
) {
    override fun onExecute(player: CommandSender, args: List<String>) {
        player as Player
        val current = player.profile.read(pvpEnabledKey)
        if (!current) {
            player.sendMessage(plugin.langYml.getMessage("disabling-pvp"))

            plugin.scheduler.runLater(200) {
                player.profile.write(pvpEnabledKey, !current)
                player.sendMessage(plugin.langYml.getMessage("disabled-pvp"))
            }
        } else {
            player.profile.write(pvpEnabledKey, !current)
            player.sendMessage(plugin.langYml.getMessage("enabled-pvp"))
        }
    }
}

class PVPListener(
    private val plugin: EcoMCPlugin
): Listener {
    @EventHandler
    fun onPVP(event: EntityDamageByEntityEvent) {
        val victim = event.entity as? Player ?: return
        val attacker = event.damager.tryAsPlayer() ?: return

        if (!victim.profile.read(pvpEnabledKey)) {
            event.isCancelled = true
            attacker.sendMessage(plugin.langYml.getMessage("player-has-pvp-disabled"))
        }
    }

    @EventHandler
    fun debuff(event: PlayerSkillExpGainEvent) {
        val player = event.player

        if (!player.profile.read(pvpEnabledKey)) {
            event.amount *= 0.25
        }
    }

    @EventHandler
    fun debuff(event: ShopSellEvent) {
        val player = event.player

        if (!player.profile.read(pvpEnabledKey)) {
            event.price *= 0.75
        }
    }
}
