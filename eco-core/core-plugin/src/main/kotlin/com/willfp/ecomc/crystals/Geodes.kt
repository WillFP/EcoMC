package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.items.builder.SkullBuilder
import com.willfp.eco.util.NumberUtils
import com.willfp.ecomc.EcoMCPlugin
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private fun ItemStack.setGeode() {
    this.fast().persistentDataContainer.set(
        EcoMCPlugin.instance.namespacedKeyFactory.create("geode"),
        PersistentDataType.INTEGER,
        1
    )
}

private val ItemStack.isGeode: Boolean
    get() {
        return this.fast().persistentDataContainer.has(
            EcoMCPlugin.instance.namespacedKeyFactory.create("geode"),
            PersistentDataType.INTEGER
        )
    }

private lateinit var geodesMenu: Menu

fun initGeodes(plugin: EcoPlugin) {
    CustomItem(
        plugin.namespacedKeyFactory.create("geode"),
        { it.isGeode },
        SkullBuilder()
            .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDg4NmUwZjQxMTg1YjE4YTNhZmQ4OTQ4OGQyZWU0Y2FhMDczNTAwOTI0N2NjY2YwMzljZWQ2YWVkNzUyZmYxYSJ9fX0=")
            .setDisplayName("<gradient:#6a3093>Geode</gradient:#a044ff>")
            .addLoreLine("&fBring this to the &aCristallier")
            .addLoreLine("&fto crack it open and get")
            .addLoreLine("&b❖ Crystals&f!")
            .addLoreLine("")
            .addLoreLine("&fEach <gradient:#6a3093>Geode</gradient:#a044ff>&f contains")
            .addLoreLine("&fbetween 0 and 2 &b❖ Crystals!")
            .build().apply {
                setGeode()
            }
    ).register()

    geodesMenu = menu(5) {
        setMask(
            FillerMask(
                MaskItems(
                    Items.lookup("black_stained_glass_pane"),
                ),
                "111111111",
                "111101111",
                "111101111",
                "111111111",
                "000000000"
            )
        )

        setTitle("Break open Geodes")

        for (i in 1..9) {
            setSlot(
                5, i, slot(
                    ItemStackBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayName("")
                        .build()
                ) {
                    setUpdater { player, menu, _ ->
                        val hasGeode = menu.getCaptiveItems(player).getOrNull(0)
                            ?.isGeode == true

                        if (hasGeode) {
                            ItemStackBuilder(Material.PURPLE_STAINED_GLASS_PANE)
                                .setDisplayName("&e")
                                .build()
                        } else {
                            ItemStackBuilder(Material.RED_STAINED_GLASS_PANE)
                                .setDisplayName("&e")
                                .build()
                        }
                    }
                }
            )
        }

        setSlot(2, 5, slot(ItemStack(Material.AIR)) {
            setCaptive()
        })

        onClose { event, menu ->
            DropQueue(event.player as Player)
                .addItems(menu.getCaptiveItems(event.player as Player))
                .setLocation(event.player.eyeLocation)
                .forceTelekinesis()
                .push()
        }

        setSlot(
            3, 5, slot(
                ItemStackBuilder(Material.STONECUTTER)
                    .setDisplayName("&aBreak open geodes!")
                    .build()
            ) {
                setUpdater { player, menu, _ ->
                    val hasGeode = menu.getCaptiveItems(player).getOrNull(0)
                        ?.isGeode == true

                    if (hasGeode) {
                        ItemStackBuilder(Material.STONECUTTER)
                            .setDisplayName("&aBreak open geodes!")
                            .addLoreLine("")
                            .addLoreLine("&7Click to crack open your geodes")
                            .addLoreLine("&7to obtain &b❖ Crystals&7!")
                            .addEnchantment(Enchantment.DURABILITY, 1)
                            .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                            .build()
                    } else {
                        ItemStackBuilder(Material.STONECUTTER)
                            .setDisplayName("&aBreak open geodes!")
                            .addLoreLine("")
                            .addLoreLine("&7Place geodes in the slot above")
                            .addLoreLine("&7to break them open for &b❖ Crystals&7!")
                            .addEnchantment(Enchantment.DURABILITY, 1)
                            .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                            .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                            .build()
                    }
                }

                onClose { event, menu ->
                    val player = event.player as Player
                    DropQueue(player)
                        .addItems(menu.getCaptiveItems(player))
                        .forceTelekinesis()
                        .setLocation(player.eyeLocation)
                        .push()

                    plugin.scheduler.run {
                        player.openCrystalShop()
                    }
                }

                onLeftClick { event, _, menu ->
                    val player = event.whoClicked as Player
                    val hasGeode = menu.getCaptiveItems(player).getOrNull(0)
                        ?.isGeode == true

                    if (hasGeode) {
                        val item = menu.getCaptiveItems(player)[0]
                        val amount = item.amount
                        item.amount = 0
                        item.itemMeta = null

                        var crystalsToGive = 0

                        repeat(amount) {
                            crystalsToGive += NumberUtils.randInt(
                                plugin.configYml.getInt("geodes.min"),
                                plugin.configYml.getInt("geodes.max")
                            )
                        }

                        if (crystalsToGive == 0) {
                            player.sendMessage(
                                plugin.langYml.getMessage("cracked-geodes-fail")
                                    .replace("%amount%", crystalsToGive.toString())
                            )
                        } else {
                            player.crystals += crystalsToGive
                            player.sendMessage(
                                plugin.langYml.getMessage("cracked-geodes-success")
                                    .replace("%amount%", crystalsToGive.toString())
                            )
                        }

                        player.playSound(
                            player.location,
                            Sound.UI_STONECUTTER_TAKE_RESULT,
                            1.0f,
                            0.6f
                        )
                    }
                }
            }
        )
    }
}

fun Player.openGeodesMenu() {
    geodesMenu.open(this)
}
