package com.willfp.ecomc;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpawnProtection extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Pass an {@link EcoPlugin} in order to interface with it.
     *
     * @param plugin The plugin to manage.
     */
    public SpawnProtection(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            return;
        }

        player.setMetadata("new_player_invul", this.getPlugin().getMetadataValueFactory().create(true));
        player.sendMessage(this.getPlugin().getLangYml().getMessage("invul-join"));

        this.getPlugin().getScheduler().runLater(() -> {
            if (!player.hasMetadata("new_player_invul")) {
                return;
            }
            player.sendMessage(this.getPlugin().getLangYml().getMessage("invul-5m-left"));
        }, 12000);

        this.getPlugin().getScheduler().runLater(() -> {
            if (!player.hasMetadata("new_player_invul")) {
                return;
            }
            player.sendMessage(this.getPlugin().getLangYml().getMessage("invul-expired"));
        }, 18000);
    }

    @EventHandler
    public void onDamage(@NotNull final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!player.hasMetadata("new_player_invul")) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPvP(@NotNull final EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player attacker = tryAsPlayer(event.getDamager());

        if (!(attacker != null && event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();

        if (attacker.hasMetadata("new_player_invul")) {
            attacker.removeMetadata("new_player_invul", this.getPlugin());
            attacker.sendMessage(this.getPlugin().getLangYml().getMessage("invul-pvp-remove"));
        }

        if (victim.hasMetadata("new_player_invul")) {
            event.setCancelled(true);
            attacker.sendMessage(this.getPlugin().getLangYml().getMessage("invul-pvp-protected-attacker"));
            victim.sendMessage(this.getPlugin().getLangYml().getMessage("invul-pvp-protected-victim"));
        }
    }

    @Nullable
    private Player tryAsPlayer(@NotNull final Entity entity) {
        if (entity instanceof Player) {
            return (Player) entity;
        }

        if (entity instanceof Projectile projectile) {
            if (projectile.getShooter() instanceof Player shooter) {
                return shooter;
            }
        }

        return null;
    }
}
