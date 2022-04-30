@file:Suppress("UNUSED_PARAMETER")

package com.willfp.ecomc

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import org.bukkit.OfflinePlayer

private val key = PersistentDataKey(
    EcoMCPlugin.instance.namespacedKeyFactory.create("crystals"),
    PersistentDataKeyType.INT,
    0
)

var OfflinePlayer.crystals: Int
    get() = this.profile.read(key)
    set(value) {
        this.profile.write(key, value)
    }
