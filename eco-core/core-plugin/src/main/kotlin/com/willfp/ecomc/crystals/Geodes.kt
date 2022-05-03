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
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

private fun ItemStack.setGeode(level: Int) {
    this.fast().persistentDataContainer.set(
        EcoMCPlugin.instance.namespacedKeyFactory.create("geode"),
        PersistentDataType.INTEGER,
        level
    )
}

private val ItemStack.geodeLevel: Int
    get() {
        return this.fast().persistentDataContainer.getOrDefault(
            EcoMCPlugin.instance.namespacedKeyFactory.create("geode"),
            PersistentDataType.INTEGER,
            0
        )
    }

private lateinit var geodesMenu: Menu

class PreventGeodePlace: Listener {
    @EventHandler
    fun handle(event: BlockPlaceEvent) {
        if (event.itemInHand.geodeLevel > 0) {
            event.isCancelled = true
        }
    }
}

fun initGeodes(plugin: EcoPlugin) {
    CustomItem(
        plugin.namespacedKeyFactory.create("geode_1"),
        { it.geodeLevel == 1 },
        SkullBuilder()
            .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGNkNTE5NDgzZGQwNDFiMzRlYTAwNmVlNjY4NTkyY2VhNmQxZWNhZmE2YWViODJmN2M3MzI4MWVhMzM5Y2JjZCJ9fX0=")
            .setDisplayName("<gradient:#6a3093>Geode</gradient:#a044ff>")
            .addLoreLine("&fBring this to the &aGemcutter")
            .addLoreLine("&fto crack it open and get")
            .addLoreLine("&b❖ Crystals&f!")
            .addLoreLine("")
            .addLoreLine("&fThis <gradient:#6a3093>Geode</gradient:#a044ff>&f contains")
            .addLoreLine("&fbetween 0 and 1 &b❖ Crystals!")
            .build().apply {
                setGeode(1)
            }
    ).register()

    CustomItem(
        plugin.namespacedKeyFactory.create("geode_2"),
        { it.geodeLevel == 2 },
        SkullBuilder()
            .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODA2ZjYyYzcyMDllOWYyYmMzNTI1MmE0Mzg2ZDBjNjdhNGE3MTAzYjQwM2RkZmM5ZDk0MzNjNDNhYjRmYzdjNiJ9fX0=")
            .setDisplayName("&#11998eDecent <gradient:#6a3093>Geode</gradient:#a044ff>")
            .addLoreLine("&fBring this to the &aGemcutter")
            .addLoreLine("&fto crack it open and get")
            .addLoreLine("&b❖ Crystals&f!")
            .addLoreLine("")
            .addLoreLine("&fThis <gradient:#6a3093>Geode</gradient:#a044ff>&f contains")
            .addLoreLine("&fbetween 0 and 2 &b❖ Crystals!")
            .build().apply {
                setGeode(2)
            }
    ).register()

    CustomItem(
        plugin.namespacedKeyFactory.create("geode_3"),
        { it.geodeLevel == 3 },
        SkullBuilder()
            .setSkullTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTZkZmZmOTc1YjYxMjAwZTdhNDZmNjRhOTM0OTc1MzQ0YzQ5NjIxNmE4ZmE5Yzk4NTA2Y2NhNDdkOGI3OWVhYyJ9fX0=")
            .setDisplayName("&#b91d73Remarkable <gradient:#6a3093>Geode</gradient:#a044ff>")
            .addLoreLine("&fBring this to the &aGemcutter")
            .addLoreLine("&fto crack it open and get")
            .addLoreLine("&b❖ Crystals&f!")
            .addLoreLine("")
            .addLoreLine("&fThis <gradient:#6a3093>Geode</gradient:#a044ff>&f contains")
            .addLoreLine("&fbetween 1 and 4 &b❖ Crystals!")
            .build().apply {
                setGeode(3)
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
                        val geodeLevel = menu.getCaptiveItems(player).getOrNull(0)?.geodeLevel ?: 0

                        if (geodeLevel > 0) {
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
                    val geodeLevel = menu.getCaptiveItems(player).getOrNull(0)?.geodeLevel ?: 0

                    if (geodeLevel > 0) {
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
                    val geodeLevel = menu.getCaptiveItems(player).getOrNull(0)?.geodeLevel ?: 0

                    if (geodeLevel > 0) {
                        val item = menu.getCaptiveItems(player)[0]
                        val amount = item.amount
                        item.amount = 0
                        item.itemMeta = null

                        var crystalsToGive = 0

                        repeat(amount) {
                            crystalsToGive += when (geodeLevel) {
                                1 -> NumberUtils.randInt(0, 1)
                                2 -> NumberUtils.randInt(0, 2)
                                3 -> NumberUtils.randInt(1, 4)
                                else -> 0
                            }
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
