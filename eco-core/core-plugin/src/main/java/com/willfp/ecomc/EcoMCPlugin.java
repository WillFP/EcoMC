package com.willfp.ecomc;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class EcoMCPlugin extends EcoPlugin {
    public EcoMCPlugin() {
        super();
    }

    @Override
    protected void handleEnable() {
        LevelPlaceholder.register();
    }

    @Override
    protected List<Listener> loadListeners() {
        return Arrays.asList(
                new KeyDropListener(this),
                new Hardinator(this),
                new EntityYeeter(this)
        );
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new DisplayItemInHand(this)
        );
    }
}
