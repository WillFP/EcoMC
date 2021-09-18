package com.willfp.ecomc;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.util.NumberUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.jetbrains.annotations.NotNull;

public class Hardinator extends PluginDependent<EcoPlugin> implements Listener {
    public Hardinator(@NotNull EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void handle(@NotNull final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Mob) {
            event.setDamage(event.getDamage() * this.getPlugin().getConfigYml().getDouble("incoming-damage-multiplier"));
        }
    }
}
