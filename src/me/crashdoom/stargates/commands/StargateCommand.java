/**
 * This file is part of MCStargates.
 *
 * MCStargates is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MCStargates is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MCStargates.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.crashdoom.stargates.commands;

import me.crashdoom.stargates.IconMenu;
import me.crashdoom.stargates.StargateUtils;
import me.crashdoom.stargates.Stargates;
import me.crashdoom.stargates.entity.Stargate;
import me.crashdoom.stargates.listeners.OnPlayerInteract;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class StargateCommand implements CommandExecutor {

    Stargates parent;

    public StargateCommand(Stargates stargates) {
        this.parent = stargates;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean showHelp = false;

            if (args.length == 0) {
                showHelp = true;
            } else {
                switch (args[0]) {
                    case "dial":
                        dialCommand(player, cmd, label, args);
                        break;

                    case "create":
                        createCommand(player, cmd, label, args);
                        break;

                    case "list":
                        parent.sendChatMessage(player, "Known Gate Addresses:");

                        for (Map.Entry<String, Stargate> stargate : parent.stargates.entrySet()) {
                            parent.sendChatMessage(player, "- " + ChatColor.GREEN + stargate.getKey() + " " + ChatColor.GRAY + " Pos: " + stargate.getValue().getPosition().getX() + ", " + stargate.getValue().getPosition().getY() + ", " + stargate.getValue().getPosition().getZ(), true);
                        }
                        break;

                    default:
                        showHelp = true;
                        break;
                }
            }

            if (showHelp) {
                parent.sendChatMessage(player, "Commands:");
                parent.sendChatMessage(player, "- " + ChatColor.GREEN + "/stargate help " + ChatColor.GRAY + " - Displays this.", true);
                parent.sendChatMessage(player, "- " + ChatColor.GREEN + "/stargate dial " + ChatColor.GRAY + " - Opens the Dial Home Device.", true);
                parent.sendChatMessage(player, "- " + ChatColor.GREEN + "/stargate list " + ChatColor.GRAY + " - Lists known Stargate addresses.", true);
                parent.sendChatMessage(player, "- " + ChatColor.GREEN + "/stargate create " + ChatColor.GRAY + " - Create a new Stargate.", true);
            }
        } else {
            sender.sendMessage("ERROR: This command may only be executed by a player.");
        }

        return true;
    }

    private void dialCommand(Player player, Command cmd, String label, String[] args) {
        if (parent.dialCode.containsKey(player.getName())) {
            parent.dialCode.get(player.getName()).clear();
            if (parent.dhdMenu.containsKey(player.getName())) {
                parent.dhdMenu.get(player.getName()).destroy();
                parent.dhdMenu.remove(player.getName());
            }
        } else {
            parent.dialCode.put(player.getName(), new LinkedList<String>());
        }

        final Stargate nearbyStargate = StargateUtils.getNearbyStargate(player.getLocation());

        if (nearbyStargate == null) {
            parent.sendChatMessage(player, org.bukkit.ChatColor.RED + "No nearby Stargate detected. " + ChatColor.GRAY + "You must be within 5 blocks of a Stargate to dial.");
            return;
        }

        IconMenu iconMenu = new IconMenu("Stargate Dial Home Device", (9 * 4), new IconMenu.OptionClickEventHandler() {
            @Override
            public void onOptionClick(IconMenu.OptionClickEvent event) {
                String player = event.getPlayer().getName();

                List<String> playerDialingCode = parent.dialCode.get(player);
                int index = 1 + playerDialingCode.size();

                playerDialingCode.add(event.getName());
                parent.dhdMenu.get(player).setOption(index, parent.stackMap.get(event.getName()), event.getName());

                if (index == 7) {
                    event.setWillDestroy(true);
                    event.setWillClose(true);

                    Stargate destination = StargateUtils.getStargateByAddress(String.join("", playerDialingCode));

                    if (destination == null) {
                        parent.sendChatMessage(event.getPlayer(), org.bukkit.ChatColor.RED + "Unable to establish connection to " + String.join("", playerDialingCode) + ". " + ChatColor.GRAY + "Check the address and try again.");
                    } else if (destination.getAddress().equals(nearbyStargate.getAddress())) {
                        parent.sendChatMessage(event.getPlayer(), org.bukkit.ChatColor.RED + "Unable to establish connection to " + String.join("", playerDialingCode) + ". " + ChatColor.GRAY + "You cannot dial your own gate.");
                    } else if (destination.getWormhole() != null) {
                        parent.sendChatMessage(event.getPlayer(), org.bukkit.ChatColor.RED + "Unable to establish connection to " + String.join("", playerDialingCode) + ". " + ChatColor.GRAY + "The destination gate is currently busy.");
                    } else {
                        parent.sendChatMessage(event.getPlayer(), "Establishing connection to " + destination.getAddress() + " at " + destination.getPosition().getX() + ", " + destination.getPosition().getY() + ", " + destination.getPosition().getZ() + ".");
                        destination.openConnection(nearbyStargate);
                    }
                    return;
                }

                parent.dhdMenu.get(player).open(event.getPlayer());
                event.setWillClose(false);
            }
        }, parent);

        parent.dhdMenu.put(player.getName(), iconMenu);

        int index = 18;

        for (Map.Entry<String, ItemStack> pair : parent.stackMap.entrySet()) {
            parent.dhdMenu.get(player.getName()).setOption(index++, pair.getValue(), pair.getKey());
        }

        parent.dhdMenu.get(player.getName()).open(player);
    }

    private void createCommand(Player player, Command cmd, String label, String[] args) {
        OnPlayerInteract.addPlayer(player);

        parent.sendChatMessage(player, ChatColor.GREEN + "Please left click the centre Obsidian block of your new Stargate.");
    }
}
