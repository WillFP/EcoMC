package com.willfp.ecomc;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.display.Display;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DisplayItemInHand extends PluginCommand {
    public DisplayItemInHand(EcoPlugin plugin) {
        super(plugin, "displayiteminhand", "ecomc.displayiteminhand", true);
    }

    @Override
    public CommandHandler getHandler() {
        return ((sender, args) -> {
            Player player = (Player) sender;
            ItemStack itemStack = player.getInventory().getItemInMainHand();
            Display.display(itemStack);

            ItemMeta meta = itemStack.getItemMeta();
            if (meta == null) {
                return;
            }
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.replaceAll(s -> s.replace("§z", ""));
            meta.setLore(lore);
            for (NamespacedKey key : meta.getPersistentDataContainer().getKeys()) {
                meta.getPersistentDataContainer().remove(key);
            }
            itemStack.setItemMeta(meta);
            player.getInventory().setItemInMainHand(itemStack);
            player.sendMessage("okie dokie");
        });
    }
}
