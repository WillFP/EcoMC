package com.willfp.ecomc.crystals

import com.willfp.eco.core.drops.DropQueue
import com.willfp.eco.core.fast.fast
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.gui.slot.Slot
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.TestableItem
import com.willfp.eco.util.formatEco
import com.willfp.ecomc.crystals
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

private fun pageSlot(change: Int): Slot =
    slot(ItemStack(Material.ARROW)) {
        onLeftClick { event, _, menu ->
            val player = event.whoClicked as Player
            val page = menu.getState<Int>(player, "page") ?: 1
            menu.addState(player, "page", page + change)
        }
    }

private fun buySlot(item: TestableItem, price: Int): Slot =
    slot(item.item) {
        onLeftClick { event, _, _ ->
            val player = event.whoClicked as Player
            if (player.crystals >= price) {
                player.crystals -= price

                DropQueue(player)
                    .addItem(item.item)
                    .forceTelekinesis()
                    .push()

                player.closeInventory()
            }
        }

        setUpdater { player, _, previous ->
            val lore = mutableListOf(
                "Price: &a$price"
            )

            if (player.crystals >= price) {
                lore.add("&eLeft click to buy!")
            } else {
                lore.add("&cYou cannot afford this!")
                lore.add("&cYou need &e${price - player.crystals}&c more crystals.")
                lore.add("&cGet some at &astore.ecomc.net&c!")
            }

            previous.fast().lore = item.item.fast().lore + lore.formatEco(player)
            previous
        }
    }

val crystalShop = menu(6) {
    setMask(
        FillerMask(
            MaskItems(
                Items.lookup("black_stained_glass_pane")
            ),
            "111111111",
            "100000001",
            "100000001",
            "100000001",
            "110111011",
            "111111111"
        )
    )

    setSlot(5, 3, pageSlot(-1))
    setSlot(5, 7, pageSlot(+1))

    setSlot(2, 2, buySlot(Items.lookup("diamond"), 10))
}
