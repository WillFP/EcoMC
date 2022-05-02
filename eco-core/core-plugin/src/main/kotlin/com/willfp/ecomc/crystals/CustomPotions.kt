package com.willfp.ecomc.crystals

import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.items.CustomItem
import com.willfp.eco.util.formatEco
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.checkerframework.checker.units.qual.m

fun initCustomPotions(plugin: EcoPlugin) {
    fun potionEffect(id: String, name: String, type: PotionEffectType, strength: Int, duration: Int): CustomItem {
        val item = ItemStack(Material.POTION)
        val meta = item.itemMeta
        meta as PotionMeta
        meta.addCustomEffect(
            PotionEffect(
                type,
                duration * 20,
                strength - 1,
                true,
                true,
                true
            ),
            true
        )
        meta.setDisplayName(name.formatEco())
        item.itemMeta = meta
        return CustomItem(
            plugin.namespacedKeyFactory.create(id),
            { it == item },
            item
        ).apply { register() }
    }

    potionEffect(
        "strength_5",
        "&cStrength V Potion",
        PotionEffectType.INCREASE_DAMAGE,
        5,
        900
    )
}
