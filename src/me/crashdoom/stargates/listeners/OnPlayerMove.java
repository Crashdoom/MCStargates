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

import me.crashdoom.stargates.StargateUtils;
import me.crashdoom.stargates.Stargates;
import me.crashdoom.stargates.entity.Stargate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.LinkedList;
import java.util.List;


public class OnPlayerMove implements Listener {
    Stargates parent;
    List<String> ignore = new LinkedList<String>();

    public OnPlayerMove(Stargates stargates) {
        this.parent = stargates;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        final String playerName = e.getPlayer().getName();
        if (ignore.indexOf(playerName) != -1) return;

        Location loc = e.getPlayer().getLocation();
        Block block = loc.getBlock();

        if (block.getType() == Material.END_GATEWAY) {
            Stargate nearbyGate = StargateUtils.getNearbyStargate(loc);

            if (nearbyGate.isOriginStargate()) {
                e.getPlayer().teleport(nearbyGate.getWormhole().getPosition().clone().add(nearbyGate.getAxis().equals("x") ? 0 : 2, 1, nearbyGate.getAxis().equals("z") ? 0 : 2));
            } else {
                parent.sendChatMessage(e.getPlayer(), "A burst of energy forces you away from the wormhole exit.");
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(-1));
                e.getPlayer().damage(1.0);
            }

            ignore.add(playerName);

            Bukkit.getScheduler().scheduleSyncDelayedTask(parent, new Runnable() {
                @Override
                public void run() {
                    ignore.remove(playerName);
                }
            }, 40L);
        }
    }
}
