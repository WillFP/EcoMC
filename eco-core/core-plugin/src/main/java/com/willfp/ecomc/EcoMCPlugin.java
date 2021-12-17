package com.willfp.ecomc;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;

public class EcoMCPlugin extends EcoPlugin {
    private static EcoMCPlugin instance;

    public EcoMCPlugin() {
        super();
        instance = this;
    }

    @Override
    protected void handleEnable() {
        LevelPlaceholder.register();
        SchmoneyPlaceholder.init();
    }

    @Override
    protected void handleReload() {
        SchmoneyPlaceholder.createTheRunnable();
    }

    @Override
    protected List<Listener> loadListeners() {
        return Arrays.asList(
                new KeyDropListener(this),
                new Hardinator(this),
                new EntityYeeter(this),
                new SpawnProtection(this)
        );
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new DisplayItemInHand(this)
        );
    }

    public static EcoMCPlugin getInstance() {
        return instance;
    }
}
