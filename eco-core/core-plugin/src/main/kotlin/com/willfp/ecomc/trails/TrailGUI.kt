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
                    lore = if (player.hasPermission(permission)) {
                        listOf(
                            "&eLeft click to activate",
                            "&ethis trail!"
                        ).formatEco()
                    } else {
                        listOf(
                            "&cYou don't own this trail!",
                            "&fBuy it from the &bCristallier ❖"
                        )
                    }
                }.unwrap()
            }
        }
    }

    fun init(plugin: EcoPlugin) {
        menu = menu(4) {
            setTitle("Trails")

            setMask(
                FillerMask(
                    MaskItems(
                        Items.lookup("black_stained_glass_pane"),
                        Items.lookup("red_stained_glass_pane")
                    ),
                    "112222211",
                    "100000001",
                    "100000001",
                    "112202211"
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
                4, 5, slot(
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
