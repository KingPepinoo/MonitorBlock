package com.pepino.monitorblock.listeners;

import com.pepino.monitorblock.MonitorBlock;
import com.pepino.monitorblock.MonitoredBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WandUsageListener implements Listener {

    private final MonitorBlock plugin = MonitorBlock.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() != Material.STICK || !item.hasItemMeta()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            return;
        }

        String displayName = meta.getDisplayName();
        if (!displayName.startsWith("MonitorBlock Wand ")) {
            return;
        }

        String name = displayName.substring("MonitorBlock Wand ".length());

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {
                MonitoredBlock monitoredBlock = new MonitoredBlock(name, clickedBlock.getLocation(), clickedBlock.getType());
                plugin.addMonitoredBlock(monitoredBlock);
                player.sendMessage("Block at " + clickedBlock.getLocation().toVector() + " has been added to monitoring under name '" + name + "'.");
            }
        }
    }
}
