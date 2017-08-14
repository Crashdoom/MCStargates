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
package me.crashdoom.stargates.listeners;

import me.crashdoom.stargates.IconMenu;
import me.crashdoom.stargates.StargateUtils;
import me.crashdoom.stargates.Stargates;
import me.crashdoom.stargates.entity.Stargate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class OnPlayerInteract implements Listener {
    Stargates parent;
    static LinkedList<String> activeClients = new LinkedList<>();
    public Map<String, IconMenu> newCodeMenu = new HashMap<>();

    public OnPlayerInteract(Stargates stargates) {
        this.parent = stargates;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (activeClients.indexOf(player.getName()) == -1 || event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        Block centre_block = event.getClickedBlock();
        boolean isInvalid = event.getClickedBlock().getType() != Material.OBSIDIAN || !StargateUtils.isStargateBuildValid(centre_block.getLocation());

        if (isInvalid) {
            if (StargateUtils.getStargateByLocation(centre_block.getLocation()) != null) {
                parent.sendChatMessage(player, ChatColor.RED + "Stargate has been damaged. " + ChatColor.GRAY + "Removing Stargate from active network.");
            } else {
                parent.sendChatMessage(player, ChatColor.RED + "Unable to detect Stargate Portal build. " + ChatColor.GRAY + "You must select the bottom-middle block.");
                parent.sendChatMessage(player, "Stargate portals must be a 5x5 border of Obsidian with a redstone block in the centre-top position, and no obstructions.");
            }
        } else if (StargateUtils.getStargateByLocation(centre_block.getLocation()) != null) {
            parent.sendChatMessage(player, ChatColor.RED + "This Stargate is already registered.");
        } else {
            parent.sendChatMessage(player, ChatColor.GREEN + "Detected valid Stargate build.");
            parent.sendChatMessage(player, "Your new Stargate address has been shown on screen and registered in /sg list.");

            IconMenu sgCode = new IconMenu("Your new Stargate Address is", 9, new IconMenu.OptionClickEventHandler() {
                @Override
                public void onOptionClick(IconMenu.OptionClickEvent event) {
                    event.setWillClose(true);
                    event.setWillDestroy(true);
                }
            }, parent);

            String address = "";

            for (int index = 1; index < 8; index++) {
                Random random = new Random();
                List<String> keySet = new ArrayList<String>(parent.stackMap.keySet());
                //String key = keySet.get(random.nextInt(keySet.size() - 2) + 1);
                String key = keySet.get(random.nextInt(keySet.size()));

                sgCode.setOption(index, parent.stackMap.get(key), key);
                address = address + key;
            }

            //sgCode.setOption(7, parent.stackMap.get("[A]"), "[A]");

            sgCode.open(player);

            Stargate stargate = new Stargate(parent, address, centre_block.getLocation(), StargateUtils.getStargateAxis(centre_block.getLocation()));
            parent.stargates.put(address, stargate);
        }

        activeClients.remove(player.getName());
        event.setCancelled(true);
    }

    public static void addPlayer(Player player) {
        if (activeClients.indexOf(player.getName()) == -1) {
            activeClients.add(player.getName());
        }
    }
}
