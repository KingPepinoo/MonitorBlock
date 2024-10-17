package com.pepino.monitorblock.commands;

import com.pepino.monitorblock.MonitorBlock;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.entity.Player;


public class MonitorBlockCommand implements CommandExecutor {

    private final MonitorBlock plugin = MonitorBlock.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("monitorblock.use")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("Usage: /monitorblock <name>");
            return true;
        }

        String name = args[0];

        ItemStack wand = new ItemStack(Material.STICK);
        ItemMeta meta = wand.getItemMeta();
        meta.setDisplayName("MonitorBlock Wand " + name);
        wand.setItemMeta(meta);

        player.getInventory().addItem(wand);
        player.sendMessage("You have been given a MonitorBlock Wand named '" + name + "'. Use it to select blocks to monitor.");

        return true;
    }
}
