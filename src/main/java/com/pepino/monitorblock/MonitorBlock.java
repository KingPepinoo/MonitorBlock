package com.pepino.monitorblock;

import com.pepino.monitorblock.commands.MonitorBlockCommand;
import com.pepino.monitorblock.listeners.WandUsageListener;
import com.pepino.monitorblock.listeners.BlockInteractionListener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.ArrayList;
import java.util.List;

public final class MonitorBlock extends JavaPlugin {

    private static MonitorBlock instance;
    private final List<MonitoredBlock> monitoredBlocks = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        getCommand("monitorblock").setExecutor(new MonitorBlockCommand());
        getServer().getPluginManager().registerEvents(new WandUsageListener(), this);
        getServer().getPluginManager().registerEvents(new BlockInteractionListener(), this);
        getLogger().info("MonitorBlock plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("MonitorBlock plugin has been disabled.");
    }

    public static MonitorBlock getInstance() {
        return instance;
    }

    public List<MonitoredBlock> getMonitoredBlocks() {
        return monitoredBlocks;
    }

    public void addMonitoredBlock(MonitoredBlock monitoredBlock) {
        monitoredBlocks.add(monitoredBlock);
    }
}
