package com.willfp.ecomc;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.events.EntityDeathByEntityEvent;
import com.willfp.eco.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

public class EntityYeeter extends PluginDependent<EcoPlugin> implements Listener {
    public EntityYeeter(@NotNull EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void handle(@NotNull final EntitySpawnEvent event) {
        if (event.getEntityType() == EntityType.BAT) {
            event.setCancelled(true);
        }

        if (event.getEntityType() == EntityType.ENDERMAN) {
            if (NumberUtils.randFloat(0, 100) < 50) {
                event.setCancelled(true);
            }
        }

        if (event.getEntityType() == EntityType.CAVE_SPIDER) {
            if (NumberUtils.randFloat(0, 100) > 60) {
                event.setCancelled(true);
            }
        }
    }
}
