package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.util.formatEco
import com.willfp.ecomc.EcoMCPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

private interface ShopItem {
    fun giveTo(player: Player)
}

private class ItemShopItem(
    private val item: TestableItem
) : ShopItem {
    override fun giveTo(player: Player) =
        DropQueue(player)
            .addItem(item.item)
            .forceTelekinesis()
            .push()
}

private class CommandShopItem(
    private val command: String
) : ShopItem {
    override fun giveTo(player: Player) {
        Bukkit.dispatchCommand(
            Bukkit.getConsoleSender(),
            command.replace("%player%", player.name)
        )
    }
}

private fun buySlot(config: Config, isSingleUse: Boolean = false): Slot {
    val id = config.getString("id")
    val key = if (isSingleUse) {
        PersistentDataKey(
            EcoMCPlugin.instance.namespacedKeyFactory.create("crystals_${id}_purchases"),
            PersistentDataKeyType.INT,
            0
        ).player()
    } else null

    val display = Items.lookup(config.getString("display"))
    val price = config.getInt("price")
    val item = if (config.has("command")) {
        CommandShopItem(config.getString("command"))
    } else {
        ItemShopItem(Items.lookup(config.getString("item")))
    }

    return slot(display.item) {
        onLeftClick { event, _, _ ->
            val player = event.whoClicked as Player

            if (key != null) {
                if (player.profile.read(key) > 0) {
                    return@onLeftClick
                }
            }

            if (player.crystals >= price) {
                player.crystals -= price

                item.giveTo(player)
                player.playSound(
                    player.location,
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.5f
                )

                player.closeInventory()
            }
        }

        setUpdater { player, _, previous ->
            val lore = mutableListOf(
                "&fPrice: &b${price}❖",
                ""
            )

            if (key != null) {
                if (player.profile.read(key) > 0) {
                    lore.add("&c&oYou have already purchased")
                    lore.add("&c&othis item")
                }
            } else {
                if (player.crystals >= price) {
                    lore.add("&e&oLeft click to buy!")
                } else {
                    lore.add("&c&oYou cannot afford this!")
                    lore.add("&c&oYou need &b&o${price - player.crystals}❖&c&o more crystals!")
                    lore.add("&c&oGet some at &a&ostore.ecomc.net")
                }
            }

            previous.fast().lore = display.item.fast().lore + lore.formatEco(player)
            previous
        }
    }
}

private lateinit var crystalShopPage1: Menu

private lateinit var crystalShopPage2: Menu

fun initCrystalShop(plugin: EcoPlugin) {
    crystalShopPage1 = menu(4) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("black_stained_glass_pane")
                ),
                "100000001",
                "100000001",
                "100000001",
                "111111011",
            )
        )

        setSlot(
            4, 7, slot(
                ItemStackBuilder(Material.ARROW)
                    .setDisplayName("&aNext Page ->")
                    .build()
            ) {
                onLeftClick { event, _, _ ->
                    val player = event.whoClicked as Player
                    crystalShopPage2.open(player)
                }
            }
        )

        for (config in plugin.configYml.getSubsections("crystalshop")) {
            if (config.getInt("gui.page") != 1) {
                continue
            }

            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }
    }

    crystalShopPage2 = menu(4) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("black_stained_glass_pane")
                ),
                "100000001",
                "100000001",
                "100000001",
                "110111111",
            )
        )

        setSlot(
            4, 3, slot(
                ItemStackBuilder(Material.ARROW)
                    .setDisplayName("&a<- Previous Page")
                    .build()
            ) {
                onLeftClick { event, _, _ ->
                    val player = event.whoClicked as Player
                    crystalShopPage1.open(player)
                }
            }
        )

        for (config in plugin.configYml.getSubsections("crystalshop")) {
            if (config.getInt("gui.page") != 2) {
                continue
            }

            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }
    }
}

fun Player.openCrystalShop() {
    crystalShopPage1.open(this)
}
