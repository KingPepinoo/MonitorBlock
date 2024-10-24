package com.pepino.monitorblock.listeners;

import com.pepino.monitorblock.MonitorBlock;
import com.pepino.monitorblock.MonitoredBlock;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BlockInteractionListener implements Listener {

    private final MonitorBlock plugin = MonitorBlock.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        List<MonitoredBlock> monitoredBlocks = plugin.getMonitoredBlocks();

        if (monitoredBlocks.isEmpty()) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null) {
            return;
        }

        for (MonitoredBlock monitoredBlock : monitoredBlocks) {
            if (clickedBlock.getLocation().equals(monitoredBlock.getLocation())) {
                Player player = event.getPlayer();

                String interactionType = event.getAction() == Action.RIGHT_CLICK_BLOCK ? "Right Click" : "Left Click";

                sendDiscordMessage(player.getName(), monitoredBlock, interactionType);
                Bukkit.getLogger().info("Player " + player.getName() + " interacted with monitored block '" + monitoredBlock.getName() + "'. Interaction Type: " + interactionType);
                break;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        List<MonitoredBlock> monitoredBlocks = plugin.getMonitoredBlocks();

        if (monitoredBlocks.isEmpty()) {
            return;
        }

        Block brokenBlock = event.getBlock();

        for (MonitoredBlock monitoredBlock : monitoredBlocks) {
            if (brokenBlock.getLocation().equals(monitoredBlock.getLocation())) {
                Player player = event.getPlayer();

                String interactionType = "Block Break";

                sendDiscordMessage(player.getName(), monitoredBlock, interactionType);
                Bukkit.getLogger().info("Player " + player.getName() + " broke monitored block '" + monitoredBlock.getName() + "'.");
                break;
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {

        List<MonitoredBlock> monitoredBlocks = plugin.getMonitoredBlocks();

        if (monitoredBlocks.isEmpty()) {
            return;
        }

        List<Block> explodedBlocks = event.blockList();

        for (Block explodedBlock : explodedBlocks) {
            for (MonitoredBlock monitoredBlock : monitoredBlocks) {
                if (explodedBlock.getLocation().equals(monitoredBlock.getLocation())) {
                    Entity entity = event.getEntity();
                    String cause = entity != null ? entity.getType().toString() : "Explosion";
                    String interactionType = "Explosion (" + cause + ")";

                    sendDiscordMessage("Environment", monitoredBlock, interactionType);
                    Bukkit.getLogger().info("Monitored block '" + monitoredBlock.getName() + "' was exploded by " + cause + ".");
                    break;
                }
            }
        }
    }

    private void sendDiscordMessage(String playerName, MonitoredBlock monitoredBlock, String interactionType) {
        String webhookUrl = plugin.getWebhookUrl();

        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equalsIgnoreCase("YOUR_DISCORD_WEBHOOK_URL")) {
            plugin.getLogger().warning("Webhook URL is not set or invalid. Please set it in the config.yml file.");
            return;
        }

        Vector coords = monitoredBlock.getLocation().toVector();
        String blockType = monitoredBlock.getBlockType().toString();

        String jsonPayload = String.format("{\"embeds\": [{"
                + "\"title\": \"%s has been interacted with!\","
                + "\"color\": 5814783,"
                + "\"fields\": ["
                + "{\"name\": \"Player\", \"value\": \"%s\", \"inline\": true},"
                + "{\"name\": \"Coordinates\", \"value\": \"%s\", \"inline\": true},"
                + "{\"name\": \"Block Type\", \"value\": \"%s\", \"inline\": true},"
                + "{\"name\": \"Interaction Type\", \"value\": \"%s\", \"inline\": true}"
                + "]}]}",
                escapeJson(monitoredBlock.getName()),
                escapeJson(playerName),
                escapeJson(coords.toString()),
                escapeJson(blockType),
                escapeJson(interactionType)
        );

        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            os.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode != 204) {
                plugin.getLogger().warning("Failed to send message to Discord webhook. Response code: " + responseCode);
                InputStream is = connection.getErrorStream();
                if (is != null) {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        response.append(line);
                        response.append('\r');
                    }
                    rd.close();
                    plugin.getLogger().warning("Response: " + response.toString());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error sending message to Discord webhook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeJson(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '/': sb.append("\\/"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20 || c > 0x7E) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }
}
