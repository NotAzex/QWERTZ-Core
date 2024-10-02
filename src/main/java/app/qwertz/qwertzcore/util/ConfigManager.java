/*
        Copyright (C) 2024 QWERTZ_EXE

        This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License
        as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.

        This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
        without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
        See the GNU Affero General Public License for more details.

        You should have received a copy of the GNU Affero General Public License along with this program.
        If not, see <http://www.gnu.org/licenses/>.
*/

package app.qwertz.qwertzcore.util;

import app.qwertz.qwertzcore.QWERTZcore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigManager {
    private final QWERTZcore plugin;
    private final File configFile;
    private final Gson gson;
    private Map<String, Object> config;

    public static final String DEFAULT_FONT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String QWERTZ_FONT = "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ";
    public static final String MODERN_FONT = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘᴏʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘᴏʀsᴛᴜᴠᴡxʏᴢ";
    public static final String BLOCKY_FONT = "🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉🄰🄱🄲🄳🄴🄵🄶🄷🄸🄹🄺🄻🄼🄽🄾🄿🅀🅁🅂🅃🅄🅅🅆🅇🅈🅉";

    public ConfigManager(QWERTZcore plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "config.json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.config = new HashMap<>();
        ensurePluginFolder();
        loadConfig();
    }


    public void loadConfig() {
        if (!configFile.exists()) {
            createDefaultConfig();
        }

        try (Reader reader = new FileReader(configFile)) {
            config = gson.fromJson(reader, Map.class);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not load config file: " + e.getMessage());
            createDefaultConfig();
        }

        if (config == null) {
            config = new HashMap<>();
        }

        // Ensure all required settings exist
        ensureConfigDefaults();
    }

    private void createDefaultConfig() {
        config = new HashMap<>();
        ensureConfigDefaults();
        saveConfig();
        plugin.getLogger().info("Created default config.json file");
    }


    private void ensureConfigDefaults() {

        if (!config.containsKey("spawn")) {
            setDefaultSpawnLocation();
        }
        if (!config.containsKey("tpOnRevive")) {
            config.put("tpOnRevive", true);
        }
        if (!config.containsKey("tpOnUnrevive")) {
            config.put("tpOnUnrevive", true);
        }
        if (!config.containsKey("tpOnDeath")) {
            config.put("tpOnDeath", true);
        }
        if (!config.containsKey("tpOnJoin")) {
            config.put("tpOnJoin", true);
        }
        if (!config.containsKey("server")) {
            config.put("server", "My Server");
        }
        if (!config.containsKey("event")) {
            config.put("event", "Event");
        }
        if (!config.containsKey("scoreboardUpperCase")) {
            config.put("scoreboardUpperCase", true);
        }
        if (!config.containsKey("font")) {
            config.put("font", "modern");
        }
        if (!config.containsKey("reviveTokensEnabled")) {
            config.put("reviveTokensEnabled", true);
        }
        if (!config.containsKey("discord")) {
            config.put("discord", QWERTZcore.DISCORD_LINK);
        }
        if (!config.containsKey("youtube")) {
            config.put("youtube", "https://youtube.com/");
        }
        if (!config.containsKey("store")) {
            config.put("store", "https://yourstore.com/");
        }
        if (!config.containsKey("tiktok")) {
            config.put("tiktok", "https://tiktok.com/");
        }
        if (!config.containsKey("twitch")) {
            config.put("twitch", "https://twitch.tv/");
        }
        if (!config.containsKey("website")) {
            config.put("website", QWERTZcore.WEBSITE);
        }
        if (!config.containsKey("other")) {
            config.put("other", "https://example.com/");
        }
        if (!config.containsKey("chat")) {
            config.put("chat", true);
        }
    }


    private void setDefaultSpawnLocation() {
        Map<String, Object> spawnMap = new HashMap<>();
        spawnMap.put("world", "world");
        spawnMap.put("x", 0.0);
        spawnMap.put("y", 64.0);
        spawnMap.put("z", 0.0);
        spawnMap.put("yaw", 0.0f);
        spawnMap.put("pitch", 0.0f);
        config.put("spawn", spawnMap);
    }

    public void saveConfig() {
        try (Writer writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save config file: " + e.getMessage());
        }
        loadConfig();
    }

        public Location getSpawnLocation() {
        Map<String, Object> spawnMap = (Map<String, Object>) config.get("spawn");
        if (spawnMap != null) {
            World world = Bukkit.getWorld((String) spawnMap.get("world"));
            double x = ((Number) spawnMap.get("x")).doubleValue();
            double y = ((Number) spawnMap.get("y")).doubleValue();
            double z = ((Number) spawnMap.get("z")).doubleValue();
            float yaw = ((Number) spawnMap.get("yaw")).floatValue();
            float pitch = ((Number) spawnMap.get("pitch")).floatValue();
            return new Location(world, x, y, z, yaw, pitch);
        }
        return plugin.getServer().getWorlds().get(0).getSpawnLocation();
    }

    public boolean getTpOnRevive() {
        return (boolean) config.getOrDefault("tpOnRevive", true);
    }



    private void ensurePluginFolder() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getLogger().info("Creating plugin folder: " + plugin.getDataFolder().getPath());
            if (plugin.getDataFolder().mkdirs()) {
                plugin.getLogger().info("Plugin folder created successfully.");
            } else {
                plugin.getLogger().warning("Failed to create plugin folder.");
            }
        }
    }

    public Set<String> getKeys() {
        return config.keySet();
    }

    public boolean hasKey(String key) {
        return config.containsKey(key);
    }

    public Object get(String key) {
        return config.get(key);
    }

    public void set(String key, Object value) {
        config.put(key, value);
    }

    public String getServerName() {
        return (String) config.getOrDefault("server", "My Server");
    }

    public String getEventName() {
        return (String) config.getOrDefault("event", "Event");
    }
    public boolean getTpOnUnrevive() {
        return (boolean) config.getOrDefault("tpOnUnrevive", true);
    }

    public boolean getTpOnDeath() {
        return (boolean) config.getOrDefault("tpOnDeath", true);
    }

    public boolean getTpOnJoin() {
        return (boolean) config.getOrDefault("tpOnJoin", true);
    }
    public boolean getScoreboardUpperCase() {
        return (boolean) config.getOrDefault("scoreboardUpperCase", true);
    }

    public String getFont() {
        return (String) config.getOrDefault("font", "modern");
    }

    public String getFontString() {
        String fontType = getFont();
        switch (fontType.toLowerCase()) {
            case "qwertz":
                return QWERTZ_FONT;
            case "modern":
                return MODERN_FONT;
            case "blocky":
                return BLOCKY_FONT;
            default:
                return DEFAULT_FONT;
        }
    }


    public String formatScoreboardText(String text) {
        boolean upperCase = getScoreboardUpperCase();
        String fontString = getFontString();

        if (upperCase) {
            text = text.toUpperCase();
        }

        if (fontString.equals(DEFAULT_FONT)) {
            return text; // No conversion needed for default font
        }

        StringBuilder formattedText = new StringBuilder();
        for (char c : text.toCharArray()) {
            int index = DEFAULT_FONT.indexOf(c);
            if (index != -1) {
                if (fontString.equals(BLOCKY_FONT)) {
                    // For blocky font and for Math, we need to handle surrogate pairs
                    formattedText.append(fontString.substring(index * 2, (index * 2) + 2));
                } else {
                    formattedText.append(fontString.charAt(index));
                }
            } else {
                formattedText.append(c);
            }
        }

        return formattedText.toString();
    }
    public boolean isReviveTokensEnabled() {
        return (boolean) config.getOrDefault("reviveTokensEnabled", true);
    }
    public String getDiscordLink() {
        return (String) config.getOrDefault("discord", QWERTZcore.DISCORD_LINK);
    }
    public boolean getChat() {
        return (boolean) config.getOrDefault("chat", true);
    }
}