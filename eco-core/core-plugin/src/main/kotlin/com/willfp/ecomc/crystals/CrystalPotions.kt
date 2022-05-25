package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.placeholder.PlayerPlaceholder
import com.willfp.eco.util.formatEco
import com.willfp.ecomc.EcoMCPlugin
import com.willfp.ecoskills.api.PlayerSkillExpGainEvent
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import kotlin.math.floor


private fun ItemStack.setCrystalPotion() {
    this.fast().persistentDataContainer.set(
        EcoMCPlugin.instance.namespacedKeyFactory.create("crystal_potion"),
        PersistentDataType.INTEGER,
        1
    )
}

private val ItemStack.isCrystalPotion: Boolean
    get() {
        return this.fast().persistentDataContainer.has(
            EcoMCPlugin.instance.namespacedKeyFactory.create("crystal_potion"),
            PersistentDataType.INTEGER
        )
    }

private val crystalPotionKey = PersistentDataKey(
    EcoMCPlugin.instance.namespacedKeyFactory.create("crystal_potion_end_time"),
    PersistentDataKeyType.DOUBLE,
    0.0
)

private val crystalPotionActiveKey = PersistentDataKey(
    EcoMCPlugin.instance.namespacedKeyFactory.create("crystal_potion_active"),
    PersistentDataKeyType.BOOLEAN,
    false
)

object CrystalPotions {
    fun init(plugin: EcoPlugin) {
        val item = ItemStackBuilder(Material.POTION)
            .setDisplayName("&bCrystal Potion ❖")
            .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
            .addLoreLines(
                listOf(
                    "",
                    "&fDrink to gain the following",
                    "&fbuffs for &a4 Days&f:",
                    " &8» &fAccess to &a/shop",
                    " &8» &fAccess to &a/reforge",
                    " &8» &fAccess to &a/geodes",
                    " &8» &fAccess to &a/heads",
                    " &8» &a2x%&f Skill XP Boost",
                    "",
                    "&fIf you already have a",
                    "&bCrystal Potion ❖&f active, this",
                    "&fwill add 4 more days of effects!"
                ).formatEco(formatPlaceholders = true)
            ).build().apply {
                val meta = this.itemMeta as PotionMeta
                meta.color = Color.AQUA
                this.itemMeta = meta
                this.setCrystalPotion()
            }

        CustomItem(
            plugin.namespacedKeyFactory.create("crystal_potion"),
            { it.isCrystalPotion },
            item
        ).register()

        PlaceholderManager.registerPlaceholder(
            PlayerPlaceholder(
                plugin,
                "crystal_potion_info"
            ) {
                if (it.hasCrystalPotion) {
                    val secondsLeft = Math.floorDiv(
                        (it.profile.read(crystalPotionKey) - System.currentTimeMillis()).toInt(),
                        1000
                    )

                    // if you've seen this code on the internet, no you haven't. shush
                    val seconds = secondsLeft % 3600 % 60
                    val minutes = floor(secondsLeft % 3600 / 60.0).toInt()
                    val hours = floor(secondsLeft / 3600.0).toInt()

                    val hh = (if (hours < 10) "0" else "") + hours
                    val mm = (if (minutes < 10) "0" else "") + minutes
                    val ss = (if (seconds < 10) "0" else "") + seconds

                    "&fYou have a &bCrystal Potion ❖&f active! Time left: &a${hh}:${mm}:${ss}"
                } else {
                    "&cYou don't have a &bCrystal Potion ❖&c active!"
                }
            }
        )
    }
}

fun OfflinePlayer.expireCrystalPotion() {
    this.profile.write(crystalPotionKey, 0.0)
}

val OfflinePlayer.hasCrystalPotion: Boolean
    get() = this.profile.read(crystalPotionActiveKey)

fun OfflinePlayer.enableCrystalPotion() {
    var endTime = this.profile.read(crystalPotionKey)
    if (endTime < System.currentTimeMillis()) {
        endTime = System.currentTimeMillis().toDouble()
    }

    endTime += 345_600_000
    this.profile.write(crystalPotionKey, endTime)
    this.profile.write(crystalPotionActiveKey, true)
}

class CrystalPotionHandler(private val plugin: EcoPlugin) : Listener {
    @EventHandler
    fun handleDrink(event: PlayerItemConsumeEvent) {
        val player = event.player
        val item = event.item

        if (!item.isCrystalPotion) {
            return
        }

        player.enableCrystalPotion()

        player.sendMessage(
            plugin.langYml.getMessage("drank-crystal-potion")
        )

        player.playSound(
            player.location,
            Sound.UI_TOAST_CHALLENGE_COMPLETE,
            5f,
            2f
        )

        player.playSound(
            player.location,
            Sound.ENTITY_PLAYER_LEVELUP,
            5f,
            0.5f
        )
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun handleSkillXP(event: PlayerSkillExpGainEvent) {
        if (event.player.hasCrystalPotion) {
            event.amount *= 2
        }
    }

    companion object {
        fun initRunnable(plugin: EcoPlugin) {
            plugin.scheduler.runTimer(1, 1) {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (player.hasCrystalPotion && player.profile.read(crystalPotionKey) < System.currentTimeMillis()) {
                        player.profile.write(crystalPotionActiveKey, false)
                        player.sendMessage(plugin.langYml.getMessage("crystal-potion-expired"))
                        player.playSound(
                            player.location,
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            5f,
                            0.5f
                        )
                    }
                }
            }
        }
    }
}
