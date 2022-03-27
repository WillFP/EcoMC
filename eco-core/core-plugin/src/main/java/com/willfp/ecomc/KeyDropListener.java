package com.willfp.ecomc;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.events.EntityDeathByEntityEvent;
import com.willfp.eco.util.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class KeyDropListener extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public KeyDropListener(@NotNull EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void handle(@NotNull final BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (NumberUtils.randFloat(0, 100) < this.getPlugin().getConfigYml().getDouble("chance")) {
            Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "crates give " + player.getName() + " basic"
            );
            player.sendMessage(this.getPlugin().getLangYml().getMessage("got-key"));
            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                    1f,
                    0.6f
            );
            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1f,
                    0.6f
            );
        }
    }

    @EventHandler
    public void handle(@NotNull final EntityDeathByEntityEvent event) {

        Player player = null;

        if (event.getKiller() instanceof Player) {
            player = (Player) event.getKiller();
        } else if (event.getKiller() instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player) {
                player = (Player) projectile.getShooter();
            }
        }

        if (player == null) {
            return;
        }

        AttributeInstance instance = event.getVictim().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (instance == null) {
            return;
        }

        double health = instance.getValue();

        double multiplier = health / 20;

        if (NumberUtils.randFloat(0, 100) < this.getPlugin().getConfigYml().getDouble("mob-chance") * multiplier) {
            Bukkit.getServer().dispatchCommand(
                    Bukkit.getConsoleSender(),
                    "crates give " + player.getName() + " basic"
            );
            player.sendMessage(this.getPlugin().getLangYml().getMessage("got-key"));
            player.playSound(
                    player.getLocation(),
                    Sound.BLOCK_NOTE_BLOCK_CHIME,
                    1f,
                    0.6f
            );
            player.playSound(
                    player.getLocation(),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    1f,
                    0.6f
            );
        }
    }
}
