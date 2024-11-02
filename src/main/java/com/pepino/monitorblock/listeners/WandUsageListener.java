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

import java.util.HashMap;
import java.util.UUID;

public class WandUsageListener implements Listener {

    private final MonitorBlock plugin = MonitorBlock.getInstance();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
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

        long currentTime = System.currentTimeMillis();
        long cooldownTime = 250; // 0.25 seconds in milliseconds
        if (cooldowns.containsKey(playerId)) {
            long lastUseTime = cooldowns.get(playerId);
            if (currentTime - lastUseTime < cooldownTime) {
                return;
            }
        }
        cooldowns.put(playerId, currentTime);

        String name = displayName.substring("MonitorBlock Wand ".length());

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock != null) {

                String uniqueName = name;
                int counter = 1;

                while (true) {
                    final String testName = uniqueName;
                    boolean exists = plugin.getMonitoredBlocks().stream().anyMatch(b ->
                            b.getName().equals(testName) && b.getLocation().equals(clickedBlock.getLocation()));
                    if (!exists) {
                        break;
                    }
                    uniqueName = name + "_" + counter;
                    counter++;
                }

                MonitoredBlock monitoredBlock = new MonitoredBlock(uniqueName, clickedBlock.getLocation(), clickedBlock.getType());
                plugin.addMonitoredBlock(monitoredBlock);
                player.sendMessage("Block at " + clickedBlock.getLocation().toVector() +
                        " has been added to monitoring under name '" + uniqueName + "'.");
            }
        }
    }
}
