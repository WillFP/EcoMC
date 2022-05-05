package com.willfp.ecomc.trails

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.util.formatEco
import com.willfp.ecomc.crystals.playClickSound
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private lateinit var menu: Menu

object TrailGUI {
    private fun trailSlot(plugin: EcoPlugin, itemStack: ItemStack, particle: Particle, name: String): Slot {
        val permission = "ecomc.trail.${particle.name.lowercase()}"

        return slot(itemStack) {
            onLeftClick { event, _, _ ->
                val player = event.whoClicked as Player

                if (player.trail == particle) {
                    return@onLeftClick
                }

                if (player.hasPermission(permission)) {
                    player.playClickSound()
                    player.trail = particle
                    player.sendMessage(
                        plugin.langYml.getMessage("equipped-trail")
                            .replace("%trail%", name)
                    )
                } else {
                    player.playSound(
                        player.location,
                        Sound.ENTITY_VILLAGER_NO,
                        1f,
                        0.9f
                    )
                    player.sendMessage(plugin.langYml.getMessage("dont-own-trail"))
                }
            }

            setUpdater { player, _, _ ->
                itemStack.clone().fast().apply {
                    lore = if (player.trail == particle) {
                        listOf(
                            "",
                            "&c&oYou already have this",
                            "&c&otrail equipped!"
                        ).formatEco()
                    } else if (player.hasPermission(permission)) {
                        listOf(
                            "",
                            "&e&oLeft click to activate",
                            "&e&othis trail!"
                        ).formatEco()
                    } else {
                        listOf(
                            "",
                            "&c&oYou don't own this trail!",
                            "&f&oBuy it from the &b&oCristallier ❖"
                        )
                    }
                }.unwrap()
            }
        }
    }

    fun init(plugin: EcoPlugin) {
        menu = menu(5) {
            setTitle("Trails")

            setMask(
                FillerMask(
                    MaskItems(
                        Items.lookup("black_stained_glass_pane")
                    ),
                    "111111111",
                    "111111111",
                    "111111111",
                    "111111111",
                    "111111111"
                )
            )

            for (config in plugin.configYml.getSubsections("crystalshop.trails")) {
                setSlot(
                    config.getInt("gui.row") + 1,
                    config.getInt("gui.column"),
                    trailSlot(
                        plugin,
                        Items.lookup(config.getString("display")).item,
                        try {
                            Particle.valueOf(config.getString("particle").uppercase())
                        } catch (e: IllegalArgumentException) {
                            Particle.ASH
                        },
                        config.getFormattedString("name")
                    )
                )
            }

            setSlot(
                5, 5, slot(
                    ItemStackBuilder(Material.BARRIER)
                        .setDisplayName("&cRemove Trail")
                        .build()
                ) {
                    onLeftClick { event, _, _ ->
                        val player = event.whoClicked as Player
                        player.playClickSound()
                        player.trail = null
                        player.sendMessage(plugin.langYml.getMessage("removed-trail"))
                    }
                }
            )
        }
    }

    fun open(player: Player) {
        menu.open(player)
    }
}
