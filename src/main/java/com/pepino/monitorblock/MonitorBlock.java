package com.pepino.monitorblock;

import com.pepino.monitorblock.commands.MonitorBlockCommand;
import com.pepino.monitorblock.listeners.WandUsageListener;
import com.pepino.monitorblock.listeners.BlockInteractionListener;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class MonitorBlock extends JavaPlugin {

    private static MonitorBlock instance;
    private final List<MonitoredBlock> monitoredBlocks = new ArrayList<>();
    private String webhookUrl;

    private File monitoredBlocksFile;
    private FileConfiguration monitoredBlocksConfig;

    @Override
    public void onEnable() {
        instance = this;

        // Load configuration
        saveDefaultConfig();
        loadWebhookUrl();

        // Initialize monitored blocks storage
        createMonitoredBlocksFile();
        loadMonitoredBlocks();

        // Register command executor
        getCommand("monitorblock").setExecutor(new MonitorBlockCommand());

        // Register event listeners
        getServer().getPluginManager().registerEvents(new WandUsageListener(), this);
        getServer().getPluginManager().registerEvents(new BlockInteractionListener(), this);

        getLogger().info("MonitorBlock plugin has been enabled.");
    }

    @Override
    public void onDisable() {
        saveMonitoredBlocks();
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
        saveMonitoredBlocks();
    }

    public void removeMonitoredBlock(MonitoredBlock monitoredBlock) {
        monitoredBlocks.remove(monitoredBlock);
        saveMonitoredBlocks();
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void loadWebhookUrl() {
        FileConfiguration config = getConfig();
        webhookUrl = config.getString("webhook-url");
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            getLogger().warning("Webhook URL is not set in the configuration file. Please set it in config.yml.");
        }
    }

    private void createMonitoredBlocksFile() {
        monitoredBlocksFile = new File(getDataFolder(), "monitored_blocks.yml");
        if (!monitoredBlocksFile.exists()) {
            monitoredBlocksFile.getParentFile().mkdirs();
            try {
                monitoredBlocksFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        monitoredBlocksConfig = YamlConfiguration.loadConfiguration(monitoredBlocksFile);
    }

    public void saveMonitoredBlocks() {
        try {
            monitoredBlocksConfig.set("blocks", null); // Clear existing data

            for (int i = 0; i < monitoredBlocks.size(); i++) {
                MonitoredBlock block = monitoredBlocks.get(i);
                String path = "blocks." + i;
                monitoredBlocksConfig.set(path + ".name", block.getName());
                monitoredBlocksConfig.set(path + ".world", block.getLocation().getWorld().getName());
                monitoredBlocksConfig.set(path + ".x", block.getLocation().getX());
                monitoredBlocksConfig.set(path + ".y", block.getLocation().getY());
                monitoredBlocksConfig.set(path + ".z", block.getLocation().getZ());
                monitoredBlocksConfig.set(path + ".blockType", block.getBlockType().toString());
            }

            monitoredBlocksConfig.save(monitoredBlocksFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMonitoredBlocks() {
        try {
            monitoredBlocks.clear();
            if (monitoredBlocksConfig.contains("blocks")) {
                for (String key : monitoredBlocksConfig.getConfigurationSection("blocks").getKeys(false)) {
                    String path = "blocks." + key;
                    String name = monitoredBlocksConfig.getString(path + ".name");
                    String worldName = monitoredBlocksConfig.getString(path + ".world");
                    double x = monitoredBlocksConfig.getDouble(path + ".x");
                    double y = monitoredBlocksConfig.getDouble(path + ".y");
                    double z = monitoredBlocksConfig.getDouble(path + ".z");
                    String blockTypeName = monitoredBlocksConfig.getString(path + ".blockType");

                    if (name == null || worldName == null || blockTypeName == null) {
                        continue;
                    }

                    org.bukkit.World world = getServer().getWorld(worldName);
                    if (world == null) {
                        getLogger().warning("World '" + worldName + "' is not loaded. Skipping block '" + name + "'.");
                        continue;
                    }

                    org.bukkit.Location location = new org.bukkit.Location(world, x, y, z);
                    org.bukkit.Material blockType = org.bukkit.Material.getMaterial(blockTypeName);

                    if (blockType == null) {
                        getLogger().warning("Invalid block type '" + blockTypeName + "' for block '" + name + "'.");
                        continue;
                    }

                    MonitoredBlock block = new MonitoredBlock(name, location, blockType);
                    monitoredBlocks.add(block);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
