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

                if (key != null) {
                    player.profile.write(key, player.profile.read(key) + 1)
                }

                item.giveTo(player)
                player.playSound(
                    player.location,
                    Sound.BLOCK_NOTE_BLOCK_PLING,
                    1f,
                    1.5f
                )
                player.sendMessage(
                    EcoMCPlugin.instance.langYml.getMessage("bought-from-crystal")
                        .replace("%item%", display.item.fast().displayName)
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
            val lore = mutableListOf(
                "",
                "&fPrice: &b${price}❖",
                ""
            )

            if (key != null && player.profile.read(key) > 0) {
                lore.add("&c&oYou have already purchased")
                lore.add("&c&othis item")
            } else {
                if (player.crystals >= price) {
                    lore.add("&e&oLeft click to buy!")
                } else {
                    lore.add("&c&oYou cannot afford this!")
                    lore.add("&c&oYou need &b&o${price - player.crystals}❖&c&o more crystals")
                    lore.add("&c&oGet some at &a&ostore.ecomc.net")
                }
            }

            ItemStackBuilder(display.item.clone())
                .addLoreLines(lore)
                .build()
        }
    }
}

private lateinit var tagsShop: Menu
private lateinit var trackersShop: Menu
private lateinit var statsShop: Menu
private lateinit var upgradesShop: Menu
private lateinit var sellwandShop: Menu

private lateinit var mainMenu: Menu

fun initCrystalShop(plugin: EcoPlugin) {
    mainMenu = menu(3) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("light_blue_stained_glass_pane"),
                    Items.lookup("black_stained_glass_pane"),
                    Items.lookup("gray_stained_glass_pane"),
                ),
                "221111122",
                "333333333",
                "221101122"
            )
        )

        setTitle("Crystal Shop ❖")

        setSlot(3, 5, slot(
            ItemStackBuilder(Material.DIAMOND)
                .setDisplayName("&fWhat are crystals?")
                .build()
        ) {
            setUpdater { player, _, previous ->
                val item = previous.clone()

                item.fast().lore = listOf(
                    "",
                    "&bCrystals ❖&f are a special",
                    "&fcurrency used to buy exclusive",
                    "&fcosmetics, upgrades, and items.",
                    "",
                    "&fYou can get &bCrystals ❖&f by",
                    "&fmining blocks, killing mobs",
                    "&fand on &astore.ecomc.net",
                    "",
                    "&fThe %ecoskills_crystal_luck_name%&f stat",
                    "&fincreases the chance of mobs",
                    "&fand blocks dropping &bCrystals ❖",
                    "&fYour %ecoskills_crystal_luck_name%&f: &a%ecoskills_crystal_luck%"
                ).formatEco(player = player, formatPlaceholders = true)

                item
            }
        })

        setSlot(
            2, 3, slot(
                ItemStackBuilder(Material.NAME_TAG)
                    .setDisplayName("&bTags")
                    .build()
            ) {
                onLeftClick { event, _, _ ->
                    val player = event.whoClicked as Player
                    player.playClickSound()
                    tagsShop.open(player)
                }
            }
        )

        setSlot(
            2, 4, slot(
                ItemStackBuilder(Material.COMPASS)
                    .setDisplayName("&bStat Trackers")
                    .build()
            ) {
                onLeftClick { event, _, _ ->
                    val player = event.whoClicked as Player
                    player.playClickSound()
                    trackersShop.open(player)
                }
            }
        )

        setSlot(
            2, 6, slot(
                ItemStackBuilder(Material.AMETHYST_SHARD)
                    .setDisplayName("&bStats")
                    .build()
            ) {
                onLeftClick { event, _, _ ->
                    val player = event.whoClicked as Player
                    player.playClickSound()
                    statsShop.open(player)
                }
            }
        )

        setSlot(
            2, 7, slot(
                ItemStackBuilder(Material.ENCHANTED_BOOK)
                    .setDisplayName("&bUpgrades")
                    .build()
            ) {
                onLeftClick { event, _, _ ->
                    val player = event.whoClicked as Player
                    player.playClickSound()
                    upgradesShop.open(player)
                }
            }
        )

        setSlot(
            2, 5, slot(
                ItemStackBuilder(Material.BLAZE_ROD)
                    .setDisplayName("&bSellwands")
                    .build()
            ) {
                onLeftClick { event, _, _ ->
                    val player = event.whoClicked as Player
                    player.playClickSound()
                    sellwandShop.open(player)
                }
            }
        )
    }

    tagsShop = menu(4) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("light_blue_stained_glass_pane")
                ),
                "100000001",
                "100000001",
                "100000001",
                "111101111",
            )
        )

        setSlot(4, 5, slot(
            ItemStackBuilder(Material.DIAMOND)
                .setDisplayName("&fYour Balance:")
                .build()
        ) {
            setUpdater { player, _, previous ->
                previous.fast().lore = listOf(
                    "&b${player.crystals}❖ &fCrystals",
                    "",
                    "&eGet more at &astore.ecomc.net"
                ).formatEco()

                previous
            }
        })

        for (config in plugin.configYml.getSubsections("crystalshop.tags")) {
            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }

        setTitle("Crystal Shop ❖ - Tags")

        onClose { event, _ ->
            val player = event.player as Player
            plugin.scheduler.run { mainMenu.open(player) }
        }
    }

    trackersShop = menu(3) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("light_blue_stained_glass_pane")
                ),
                "100000001",
                "100000001",
                "111101111",
            )
        )

        setSlot(3, 5, slot(
            ItemStackBuilder(Material.DIAMOND)
                .setDisplayName("&fYour Balance:")
                .build()
        ) {
            setUpdater { player, _, previous ->
                previous.fast().lore = listOf(
                    "&b${player.crystals}❖ &fCrystals",
                    "",
                    "&eGet more at &astore.ecomc.net"
                ).formatEco()

                previous
            }
        })

        for (config in plugin.configYml.getSubsections("crystalshop.trackers")) {
            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }

        setTitle("Crystal Shop ❖ - Stat Trackers")

        onClose { event, _ ->
            val player = event.player as Player
            plugin.scheduler.run { mainMenu.open(player) }
        }
    }

    statsShop = menu(3) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("light_blue_stained_glass_pane")
                ),
                "000000000",
                "000000000",
                "111101111",
            )
        )

        setSlot(3, 5, slot(
            ItemStackBuilder(Material.DIAMOND)
                .setDisplayName("&fYour Balance:")
                .build()
        ) {
            setUpdater { player, _, previous ->
                previous.fast().lore = listOf(
                    "&b${player.crystals}❖ &fCrystals",
                    "",
                    "&eGet more at &astore.ecomc.net"
                ).formatEco()

                previous
            }
        })

        for (config in plugin.configYml.getSubsections("crystalshop.stats")) {
            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }

        setTitle("Crystal Shop ❖ - Stats")

        onClose { event, _ ->
            val player = event.player as Player
            plugin.scheduler.run { mainMenu.open(player) }
        }
    }

    upgradesShop = menu(2) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("light_blue_stained_glass_pane")
                ),
                "000000000",
                "111101111",
            )
        )

        setSlot(2, 5, slot(
            ItemStackBuilder(Material.DIAMOND)
                .setDisplayName("&fYour Balance:")
                .build()
        ) {
            setUpdater { player, _, previous ->
                previous.fast().lore = listOf(
                    "&b${player.crystals}❖ &fCrystals",
                    "",
                    "&eGet more at &astore.ecomc.net"
                ).formatEco()

                previous
            }
        })

        for (config in plugin.configYml.getSubsections("crystalshop.upgrades")) {
            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }

        setTitle("Crystal Shop ❖ - Upgrades")

        onClose { event, _ ->
            val player = event.player as Player
            plugin.scheduler.run { mainMenu.open(player) }
        }
    }

    sellwandShop = menu(2) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("light_blue_stained_glass_pane")
                ),
                "000000000",
                "111101111",
            )
        )

        setSlot(2, 5, slot(
            ItemStackBuilder(Material.DIAMOND)
                .setDisplayName("&fYour Balance:")
                .build()
        ) {
            setUpdater { player, _, previous ->
                previous.fast().lore = listOf(
                    "&b${player.crystals}❖ &fCrystals",
                    "",
                    "&eGet more at &astore.ecomc.net"
                ).formatEco()

                previous
            }
        })

        for (config in plugin.configYml.getSubsections("crystalshop.sellwands")) {
            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }

        setTitle("Crystal Shop ❖ - Sellwands")

        onClose { event, _ ->
            val player = event.player as Player
            plugin.scheduler.run { mainMenu.open(player) }
        }
    }
}

private fun Player.playClickSound() {
    this.playSound(this.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
}

fun Player.openCrystalShop() {
    mainMenu.open(this)
    this.playSound(
        this.location,
        Sound.ITEM_ARMOR_EQUIP_GENERIC,
        2.0f,
        0.5f
    )
}
