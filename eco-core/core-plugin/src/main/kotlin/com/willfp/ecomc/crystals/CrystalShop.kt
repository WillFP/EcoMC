package com.willfp.ecomc.crystals

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.config.updating.ConfigUpdater
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
import com.willfp.eco.core.integrations.placeholder.PlaceholderManager
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.items.builder.SkullBuilder
import com.willfp.eco.util.formatEco
import com.willfp.ecomc.EcoMCPlugin
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import kotlin.math.ceil

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

private fun shopMenu(rows: Int, configKey: String, title: String): Menu {
    return menu(rows) {
        val maskPattern = mutableListOf<String>()

        repeat(rows - 1) {
            maskPattern.add("100000001")
        }

        maskPattern.add("111101111")

        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("light_blue_stained_glass_pane")
                ),
                *maskPattern.toTypedArray()
            )
        )

        setSlot(rows, 5, slot(
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

        for (config in EcoMCPlugin.instance.configYml.getSubsections("crystalshop.$configKey")) {
            setSlot(
                config.getInt("gui.row"),
                config.getInt("gui.column"),
                buySlot(config, isSingleUse = config.getBool("singleUse"))
            )
        }

        setTitle("Crystal Shop ❖ - $title")

        onClose { event, _ ->
            val player = event.player as Player
            EcoMCPlugin.instance.scheduler.run { mainMenu.open(player) }
        }
    }
}

private fun shopSlot(item: ItemStack, shop: Menu): Slot {
    return slot(item) {
        onLeftClick { event, _, _ ->
            val player = event.whoClicked as Player
            player.playClickSound()
            shop.open(player)
        }
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

    val notifPlaceholder = config.getStringOrNull("not-if.placeholder")
    val notifEquals = config.getStringOrNull("not-if.equals")

    val display = Items.lookup(config.getString("display"))
    val item = if (config.has("command")) {
        CommandShopItem(config.getString("command"))
    } else {
        ItemShopItem(Items.lookup(config.getString("item")))
    }

    return slot(display.item) {
        onLeftClick { event, _, _ ->
            val player = event.whoClicked as Player

            val price = ceil(config.getDoubleFromExpression("price", player)).toInt()

            if (key != null) {
                if (player.profile.read(key) > 0) {
                    return@onLeftClick
                }
            }

            if (notifPlaceholder != null) {
                if (PlaceholderManager.translatePlaceholders(notifPlaceholder, player) == notifEquals) {
                    return@onLeftClick
                }
            }

            if (player.crystals >= price) {
                player.crystals -= price

                if (key != null) {
                    player.profile.write(key, player.profile.read(key) + 1)
                }

                EcoMCPlugin.instance.logger.info("${player.name} bought $id for $price crystals")
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
            val price = ceil(config.getDoubleFromExpression("price", player)).toInt()

            val lore = mutableListOf(
                "",
                "&fPrice: &b${price}❖",
                ""
            )

            if (notifPlaceholder != null && PlaceholderManager.translatePlaceholders(
                    notifPlaceholder,
                    player
                ) == notifEquals
            ) {
                lore.add("&c&oYou cannot purchase")
                lore.add("&c&othis item!")
            } else {
                if (key != null && player.profile.read(key) > 0) {
                    lore.add("&c&oYou have already purchased")
                    lore.add("&c&othis item!")
                } else {
                    if (player.crystals >= price) {
                        lore.add("&e&oLeft click to buy!")
                    } else {
                        lore.add("&c&oYou cannot afford this!")
                        lore.add("&c&oYou need &b&o${price - player.crystals}❖&c&o more crystals")
                        lore.add("&c&oGet some at &a&ostore.ecomc.net")
                    }
                }
            }

            ItemStackBuilder(display.item.clone())
                .addLoreLines(lore)
                .build()
        }
    }
}

private lateinit var mainMenu: Menu

object CrystalShop {
    @JvmStatic
    @ConfigUpdater
    fun initCrystalShop() {
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
                    .setDisplayName("&fWhat are crystals and geodes?")
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
                        "&fmining blocks, killing mobs,",
                        "&flevelling up skills and quests,",
                        "&fand on &astore.ecomc.net",
                        "",
                        "&fThe %ecoskills_crystal_luck_name%&f stat",
                        "&fincreases the chance of mobs",
                        "&fand blocks dropping <g:#6a3093>Geodes</g:#a044ff>",
                        "&fYour %ecoskills_crystal_luck_name%&f: &a%ecoskills_crystal_luck%"
                    ).formatEco(player = player, formatPlaceholders = true)

                    item
                }
            })

            setSlot(
                2, 2, shopSlot(
                    ItemStackBuilder(Material.NAME_TAG)
                        .setDisplayName("&bTags")
                        .build(),
                    shopMenu(4, "tags", "Tags")
                )
            )

            setSlot(
                2, 3, shopSlot(
                    ItemStackBuilder(Material.COMPASS)
                        .setDisplayName("&bStat Trackers")
                        .build(),
                    shopMenu(3, "trackers", "Stat Trackers")
                )
            )

            setSlot(
                2, 9, shopSlot(
                    ItemStackBuilder(Material.AMETHYST_SHARD)
                        .setDisplayName("&bStats")
                        .build(),
                    shopMenu(3, "stats", "Stats")
                )
            )

            setSlot(
                2, 6, shopSlot(
                    ItemStackBuilder(Material.ENCHANTED_BOOK)
                        .setDisplayName("&bUpgrades")
                        .build(),
                    shopMenu(2, "upgrades", "Upgrades")
                )
            )

            setSlot(
                2, 8, shopSlot(
                    ItemStackBuilder(Material.BLAZE_ROD)
                        .setDisplayName("&bSellwands")
                        .build(),
                    shopMenu(2, "sellwands", "Sellwands")
                )
            )

            setSlot(
                2, 1, shopSlot(
                    ItemStackBuilder(Material.NETHER_STAR)
                        .setDisplayName("&bTrails")
                        .build(),
                    shopMenu(3, "trails", "Trails")
                )
            )

            setSlot(
                2, 4, slot(
                    SkullBuilder()
                        .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTFjNTFiOTQ2Y2I0ODRiOWM3NmIyYzViZjVlYWIwYzc0YzljZWQ5NWYzNWFhODFlNjk5YmQ1ZDliNTdlMjBmIn19fQ==")
                        .setDisplayName("&bHeads")
                        .build()
                ) {
                    onLeftClick { event, _, _ ->
                        val player = event.whoClicked as Player
                        player.playClickSound()
                        Bukkit.dispatchCommand(player, "heads")
                    }
                }
            )

            setSlot(
                2, 7, shopSlot(
                    ItemStackBuilder(Material.ENCHANTING_TABLE)
                        .setDisplayName("&bSkill Upgrades")
                        .build(),
                    shopMenu(3, "skills", "Skill Upgrades")
                )
            )

            setSlot(
                2, 5, shopSlot(
                    ItemStackBuilder(Material.POTION)
                        .setDisplayName("&bCrystal Potions ❖")
                        .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                        .build()
                        .apply {
                            val meta = this.itemMeta as PotionMeta
                            meta.color = Color.AQUA
                            this.itemMeta = meta
                        },
                    shopMenu(2, "crystal-potions", "Crystal Potions")
                )
            )
        }
    }
}

fun Player.playClickSound() {
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
