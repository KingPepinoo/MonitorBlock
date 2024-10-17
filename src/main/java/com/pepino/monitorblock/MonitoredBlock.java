package com.pepino.monitorblock;

import org.bukkit.Location;
import org.bukkit.Material;

public class MonitoredBlock {
    private final String name;
    private final Location location;
    private final Material blockType;

    public MonitoredBlock(String name, Location location, Material blockType) {
        this.name = name;
        this.location = location;
        this.blockType = blockType;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Material getBlockType() {
        return blockType;
    }
}
