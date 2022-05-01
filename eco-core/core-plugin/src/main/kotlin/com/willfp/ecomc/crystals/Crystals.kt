@file:Suppress("UNUSED_PARAMETER")

package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.fast.fast
import com.willfp.eco.util.NamespacedKeyUtils
import com.willfp.eco.util.NumberUtils
import com.willfp.ecomc.EcoMCPlugin
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

private val key = PersistentDataKey(
    EcoMCPlugin.instance.namespacedKeyFactory.create("crystals"),
    PersistentDataKeyType.INT,
    0
)

var OfflinePlayer.crystals: Int
    get() = this.profile.read(key)
    set(value) {
        this.profile.write(key, value)
    }

class CrystalLuckListener(
    private val plugin: EcoPlugin
) : Listener {
    @EventHandler
    fun handle(event: BlockBreakEvent) {
        val player = event.player
        val level = player.inventory.itemInMainHand.fast()
            .getEnchantmentLevel(Enchantment.getByKey(NamespacedKeyUtils.create("minecraft", "crystal_luck"))!!)

        if (level == 0) {
            return
        }

        if (NumberUtils.randFloat(0.0, 100.0) < level * 0.05) {
            player.crystals += 1
            player.sendMessage(plugin.langYml.getMessage("crystal-luck"))
            player.playSound(
                player.location,
                Sound.BLOCK_NOTE_BLOCK_BELL,
                1f,
                1.2f
            )
            player.playSound(
                player.location,
                Sound.ENTITY_PLAYER_LEVELUP,
                1f,
                1.8f
            )
        }
    }
}
