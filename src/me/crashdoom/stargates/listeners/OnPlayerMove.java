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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;


public class OnPlayerMove implements Listener {
    Stargates parent;

    public OnPlayerMove(Stargates stargates) {
        this.parent = stargates;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Location loc = e.getPlayer().getLocation();
        Block block = loc.getBlock();

        if (block.getType() == Material.END_GATEWAY) {
            Stargate nearbyGate = StargateUtils.getNearbyStargate(loc);

            if (nearbyGate.isOriginStargate()) {
                e.getPlayer().teleport(nearbyGate.getWormhole().getPosition().clone().add(0, 1, 0));
                nearbyGate.closeConnection();
            } else {
                parent.sendChatMessage(e.getPlayer(), "This is not the origin gate.");
            }
        }
    }
}
