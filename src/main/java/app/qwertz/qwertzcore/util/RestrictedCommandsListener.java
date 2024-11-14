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

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestrictedCommandsListener implements Listener {

    private final Map<String, String> restrictedCommands = new HashMap<>();

    private final ConfigManager configManager;

    public RestrictedCommandsListener(ConfigManager configManager) {
        restrictedCommands.put("/minecraft:me", "qwertzcore.chat.bypassme");
        restrictedCommands.put("/me", "qwertzcore.chat.bypassme");
        restrictedCommands.put("/minecraft:tm", "qwertzcore.chat.bypasstm");
        restrictedCommands.put("/tm", "qwertzcore.chat.bypasstm");
        restrictedCommands.put("/teammsg", "qwertzcore.chat.bypasstm");
        restrictedCommands.put("/minecraft:msg", "null");
        restrictedCommands.put("/minecraft:w", "null");
        restrictedCommands.put("/minecraft:tell", "null");
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0].toLowerCase();

        if (restrictedCommands.containsKey(command)) {
            String bypassPermission = restrictedCommands.get(command);
            if (!bypassPermission.equals("null")) {
                if (!player.isOp() && !player.hasPermission(bypassPermission)) {
                    event.setCancelled(true);
                    player.sendMessage(configManager.getColor("colorError") + "You don't have permission to use this command.");
                }
            }
            else {
                event.setCancelled(true);
                player.sendMessage(configManager.getColor("colorError") + "This command is disabled!");
            }
        }
    }
}