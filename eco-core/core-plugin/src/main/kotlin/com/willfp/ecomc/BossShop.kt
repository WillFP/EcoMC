package com.willfp.ecomc

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.updating.ConfigUpdater
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.integrations.economy.EconomyManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.util.formatEco
import com.willfp.ecobosses.bosses.Bosses
import com.willfp.ecomc.crystals.crystals
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

class CommandBosses(
    plugin: EcoPlugin
) : PluginCommand(
    plugin,
    "bosses",
    "ecomc.bosses",
    true
) {
    override fun onExecute(player: CommandSender, args: List<String>) {
        player as Player
        player.openBossShop()
    }
}

private fun buySlot(config: Config): Slot? {
    val boss = Bosses.getByID(config.getString("boss")) ?: return null
    val item = boss.spawnEgg?.clone() ?: return null

    return slot(item) {
        onLeftClick { event, _, _ ->
            val player = event.whoClicked as Player

            val price = ceil(config.getDoubleFromExpression("price", player))

            if (EconomyManager.getBalance(player) >= price) {
                EconomyManager.removeMoney(player, price)

                EcoMCPlugin.instance.logger.info("${player.name} bought a boss egg for $$price")

                DropQueue(player)
                    .setLocation(player.location)
                    .addItem(item)
                    .forceTelekinesis()
                    .push()

                player.playSound(
                    player.location,
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.5f
                )
                player.sendMessage(
                    EcoMCPlugin.instance.langYml.getMessage("bought-boss-egg")
                        .replace("%boss%", item.fast().displayName)
                        .formatEco()
                )
            } else {
                player.sendMessage(EcoMCPlugin.instance.langYml.getMessage("not-enough-money"))
                player.playSound(
                    player.location,
                    Sound.ENTITY_VILLAGER_NO,
                    1f,
                    0.9f
                )
            }
        }

        onRightClick { event, _, _ ->
            val player = event.whoClicked as Player

            val price = ceil(config.getDoubleFromExpression("crystal-price", player)).toInt()

            if (player.crystals >= price) {
                player.crystals -= price

                EcoMCPlugin.instance.logger.info("${player.name} bought a boss egg for $price crystals")

                DropQueue(player)
                    .setLocation(player.location)
                    .addItem(item)
                    .forceTelekinesis()
                    .push()

                player.playSound(
                    player.location,
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.5f
                )
                player.sendMessage(
                    EcoMCPlugin.instance.langYml.getMessage("bought-boss-egg")
                        .replace("%boss%", item.fast().displayName)
                        .formatEco()
                )
            } else {
                player.sendMessage(EcoMCPlugin.instance.langYml.getMessage("buy-crystals"))
                player.playSound(
                    player.location,
                    Sound.ENTITY_VILLAGER_NO,
                    1f,
                    0.9f
                )
            }
        }

        setUpdater { player, _, _ ->
            val price = ceil(config.getDoubleFromExpression("price", player)).toInt()
            val crystalPrice = ceil(config.getDoubleFromExpression("crystal-price", player)).toInt()

            val lore = mutableListOf(
                "",
                "&fPrice: &a$${price} &for &b${crystalPrice}???",
                ""
            )

            if (EconomyManager.getBalance(player) >= price || player.crystals >= crystalPrice) {
                lore.add("&e&oLeft click to buy with money,")
                lore.add("&e&oRight click to buy with &bCrystals ???!")
            } else {
                lore.add("&c&oYou cannot afford this!")
            }

            ItemStackBuilder(item.clone())
                .addLoreLines(lore)
                .build()
        }
    }
}

private lateinit var menu: Menu

object BossShop {
    @JvmStatic
    @ConfigUpdater
    fun initBossShop(plugin: EcoMCPlugin) {
        menu = menu(3) {
            setMask(
                FillerMask(
                    MaskItems(
                        Items.lookup("red_stained_glass_pane"),
                        Items.lookup("black_stained_glass_pane"),
                        Items.lookup("gray_stained_glass_pane"),
                    ),
                    "221111122",
                    "333333333",
                    "221111122"
                )
            )

            setTitle("Boss Shop")

            for (config in plugin.configYml.getSubsections("bosses.shop")) {
                setSlot(
                    config.getInt("row"),
                    config.getInt("column"),
                    buySlot(config) ?: slot(ItemStack(Material.BARRIER)) { }
                )
            }
        }
    }
}

fun Player.openBossShop() {
    menu.open(this)
    this.playSound(
        this.location,
        Sound.ITEM_ARMOR_EQUIP_GENERIC,
        2.0f,
        0.5f
    )
}
