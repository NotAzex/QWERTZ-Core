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

import app.qwertz.qwertzcore.util.EventManager;
import app.qwertz.qwertzcore.QWERTZcore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EventCommands implements CommandExecutor {
    private final QWERTZcore plugin;
    private final EventManager eventManager;

    public EventCommands(QWERTZcore plugin, EventManager eventManager) {
        this.plugin = plugin;
        this.eventManager = eventManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "revive":
                return handleRevive(sender, args);
            case "unrevive":
                return handleUnrevive(sender, args);
            case "reviveall":
                return handleReviveAll(sender);
            case "unreviveall":
                return handleUnReviveAll(sender);
            case "listalive":
                return handleListAlive(sender);
            case "listdead":
                return handleListDead(sender);
            case "givedead":
                return handleGive(sender, args, true);
            case "givealive":
                return handleGive(sender, args, false);
            case "tpalive":
                return handleTeleport(sender, false, true);
            case "tpdead":
                return handleTeleport(sender, true, true);
            case "tpall":
                return handleTeleport(sender, true, false);
            case "tphere":
                return handleTpHere(sender, args);
            case "revivelast":
                return handleReviveLast(sender, args);
            case "healalive":
                return handleHeal(sender, true, args);
            case "healdead":
                return handleHeal(sender, false, args);
            default:
                return false;
        }
    }

    private boolean handleRevive(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Usage: /revive <player>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Player not found!");
            return false;
        }

        if  (!eventManager.revivePlayer(target, (Player) sender)) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorPrimary") + target.getName() + " is already alive!");
        }
        return true;
    }

    private boolean handleUnrevive(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Usage: /unrevive <player>");
            return false;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Player not found!");
            return false;
        }

        if (!eventManager.unrevivePlayer(target)) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorPrimary") + target.getName() + " is already dead!");
        }
        return true;
    }

    private boolean handleReviveAll(CommandSender sender) {
        eventManager.reviveAll((Player) sender);
        sender.sendMessage(plugin.getConfigManager().getColor("colorSuccess") + "All players have been revived!");
        return true;
    }

    private boolean handleUnReviveAll(CommandSender sender) {
        eventManager.unReviveAll();
        sender.sendMessage(plugin.getConfigManager().getColor("colorDead") + "All players have been unrevived!");
        return true;
    }

    private boolean handleListAlive(CommandSender sender) {
        String aliveList = eventManager.getAlivePlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null)
                .map(Player::getName)
                .collect(Collectors.joining(", "));

        if (aliveList.isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorPrimary") + "There are no alive players!");
        } else {
            sender.sendMessage(plugin.getConfigManager().getColor("colorAlive") + "Alive players: " + aliveList);
        }
        return true;
    }

    private boolean handleListDead(CommandSender sender) {
        String deadList = eventManager.getDeadPlayers().stream()
                .map(Bukkit::getPlayer)
                .filter(player -> player != null)
                .map(Player::getName)
                .collect(Collectors.joining(", "));

        if (deadList.isEmpty()) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorPrimary") + "There are no dead players!");
        } else {
            sender.sendMessage(plugin.getConfigManager().getColor("colorDead") + "Dead players: " + deadList);
        }
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args, boolean isDead) {
        if (args.length < 1 || args.length > 3) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Usage: /" + (isDead ? "givedead" : "givealive") + " <item> [amount] [data]");
            return false;
        }

        Material material = Material.matchMaterial(args[0]);
        if (material == null) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Invalid item: " + args[0]);
            return false;
        }

        int amount = 1;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Invalid amount: " + args[1]);
                return false;
            }
        }

        short data = 0;
        if (args.length == 3) {
            try {
                data = Short.parseShort(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Invalid data value: " + args[2]);
                return false;
            }
        }

        ItemStack itemStack = new ItemStack(material, amount, data);
        int playersAffected = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((isDead && eventManager.isPlayerDead(player)) || (!isDead && eventManager.isPlayerAlive(player))) {
                player.getInventory().addItem(itemStack.clone());
                playersAffected++;
            }
        }

        String playerType = isDead ? "dead" : "alive";
        String playerTypeColor = isDead ? plugin.getConfigManager().getColor("colorDead") : plugin.getConfigManager().getColor("colorAlive");
        String itemName = itemStack.getType().toString().toLowerCase().replace("_", " ");

        String message = String.format("%s %s%d %s%s %splayers have received %s%d %s%s",
                QWERTZcore.CORE_ICON,
                plugin.getConfigManager().getColor("colorSuccess"),
                playersAffected,
                playerTypeColor,
                playerType,
                plugin.getConfigManager().getColor("colorSuccess"),
                plugin.getConfigManager().getColor("colorPrimary"),
                itemStack.getAmount(),
                plugin.getConfigManager().getColor("colorPrimary"),
                itemName);

        // Broadcast the message to all players
        Bukkit.broadcastMessage(message);
        return true;
    }

    private boolean handleTeleport(CommandSender sender, boolean isDead, boolean filter) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "This command can only be executed by a player!");
            return true;
        }

        Player executor = (Player) sender;
        int teleportedCount = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player != executor) {
                if (filter) {
                    if ((isDead && eventManager.isPlayerDead(player)) || (!isDead && eventManager.isPlayerAlive(player))) {
                        player.teleport(executor.getLocation());
                        teleportedCount++;
                    }
                } else {
                    player.teleport(executor.getLocation());
                    teleportedCount++;
                }
            }
        }

        String playerType = isDead ? "dead" : "alive";
        String playerTypeColor = isDead ? plugin.getConfigManager().getColor("colorDead") : plugin.getConfigManager().getColor("colorAlive");
        String broadcastMessage = "";
        if (filter) {
            // Broadcast a message to all players
            broadcastMessage = String.format("%s %s%s %steleported all %s%s %splayers to their location!",
                    QWERTZcore.CORE_ICON,
                    plugin.getConfigManager().getColor("colorPrimary"),
                    executor.getName(),
                    plugin.getConfigManager().getColor("colorSuccess"),
                    playerTypeColor,
                    playerType,
                    plugin.getConfigManager().getColor("colorSuccess"));

        } else {
            broadcastMessage = String.format("%s %s%s %steleported all players to their location!",
                    QWERTZcore.CORE_ICON,
                    plugin.getConfigManager().getColor("colorPrimary"),
                    executor.getName(),
                    plugin.getConfigManager().getColor("colorSuccess"));
        }
        Bukkit.broadcastMessage(broadcastMessage);

        return true;
    }
    private boolean handleTpHere(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "This command can only be used by players.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Usage: /tphere <player>");
            return true;
        }

        Player commandSender = (Player) sender;
        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Player not found.");
            return true;
        }

        if (targetPlayer == commandSender) {
            sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "You can't teleport yourself to yourself!");
            return true;
        }

        targetPlayer.teleport(commandSender.getLocation());

        String message = String.format("%s %s%s %shas been teleported to you!",
                QWERTZcore.CORE_ICON,
                plugin.getConfigManager().getColor("colorPrimary"), targetPlayer.getName(),
                plugin.getConfigManager().getColor("colorSuccess"));

        commandSender.sendMessage(message);
        String targetmessage = String.format("%s %sYou have been teleported to %s%s%s!",
                QWERTZcore.CORE_ICON,
                plugin.getConfigManager().getColor("colorSuccess"),
                plugin.getConfigManager().getColor("colorPrimary"), commandSender.getName(), plugin.getConfigManager().getColor("colorSuccess"));
        targetPlayer.sendMessage(targetmessage);

        return true;
    }
    private boolean handleReviveLast(CommandSender sender, String[] args) {
        int seconds = 30; // Default to 30 seconds if no argument is provided

        if (args.length > 0) {
            try {
                seconds = Integer.parseInt(args[0]);
                if (seconds <= 0 || seconds > 60) {
                    sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Please specify a number of seconds between 1 and 60.");
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(plugin.getConfigManager().getColor("colorError") + "Invalid number of seconds. Using default of 30 seconds.");
                seconds = 30;
            }
        }

        List<Player> recentlyDeadPlayers = plugin.getEventManager().getRecentlyDeadPlayers(seconds);
        int revivedCount = 0;

        for (Player player : recentlyDeadPlayers) {
            plugin.getEventManager().revivePlayer(player, (Player) sender);
            revivedCount++;
        }

        String message = String.format("%s %s%d %splayers who died in the last %s%d %sseconds have been revived",
                QWERTZcore.CORE_ICON,
                plugin.getConfigManager().getColor("colorSuccess"),
                revivedCount,
                plugin.getConfigManager().getColor("colorPrimary"),
                plugin.getConfigManager().getColor("colorSuccess"),
                seconds,
                plugin.getConfigManager().getColor("colorPrimary"));

        Bukkit.broadcastMessage(message);
        return true;
    }
    public boolean handleHeal(CommandSender sender, Boolean alive, String[] args) {
        boolean healAlive = alive;
        int healedCount = 0;

        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isDead = plugin.getEventManager().isPlayerDead(player);
            if ((healAlive && !isDead) || (!healAlive && isDead)) {
                healPlayer(player);
                healedCount++;
            }
        }

        String playerType = healAlive ? "alive" : "dead";
        String playerTypeColor = healAlive ? plugin.getConfigManager().getColor("colorAlive") : plugin.getConfigManager().getColor("colorDead");

        String message = String.format("%s %s%d %s%s %splayers have been healed",
                QWERTZcore.CORE_ICON,
                plugin.getConfigManager().getColor("colorSuccess"),
                healedCount,
                playerTypeColor,
                playerType,
                plugin.getConfigManager().getColor("colorSuccess"));

        Bukkit.broadcastMessage(message);

        return true;
    }
    private void healPlayer(Player player) {
        double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        player.setHealth(maxHealth);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.setFireTicks(0);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }
}