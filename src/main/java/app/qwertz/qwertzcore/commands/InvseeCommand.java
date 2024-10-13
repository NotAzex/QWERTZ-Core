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

package app.qwertz.qwertzcore.commands;

import app.qwertz.qwertzcore.QWERTZcore;
import app.qwertz.qwertzcore.gui.InvseeGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {
    private final QWERTZcore plugin;

    public InvseeCommand(QWERTZcore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "This command can only be used by players.");
            plugin.getSoundManager().playSoundToSender(sender);
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Usage: /invsee <player>");
            plugin.getSoundManager().playSoundToSender(sender);
            return true;
        }

        Player viewer = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Player not found.");
            plugin.getSoundManager().playSoundToSender(sender);
            return true;
        }

        new InvseeGUI(plugin, viewer, target).open();
        return true;
    }
}