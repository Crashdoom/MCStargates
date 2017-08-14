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
package me.crashdoom.stargates.entity;

import me.crashdoom.stargates.Stargates;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class Stargate {
    private Stargates parent;

    private String address;
    private String alias;

    private Stargate wormhole = null;
    private boolean isOrigin = false;

    private Location position; // Central block
    private String axis;

    public Stargate(Stargates parent, String address, Location location, String axis) {
        this(parent, address, location, axis, "");
    }

    public Stargate(Stargates parent, String address, Location location, String axis, String alias) {
        this.parent = parent;
        this.address = address;
        this.position = location;
        this.axis = axis;
        this.alias = alias;
    }

    public String getAddress() {
        return this.address;
    }

    public String getAlias() {
        return this.alias;
    }

    public Stargate getWormhole() {
        return this.wormhole;
    }

    public boolean isOriginStargate() {
        return this.isOrigin;
    }

    public Location getPosition() {
        return this.position;
    }

    public String getAxis() {
        return this.axis;
    }

    public void showPortal() {
        for (int y = 1; y < 4; y++) {
            for (int i = -1; i <= 1; i++) {
                Block block = new Location(this.position.getWorld(), this.position.getX() + (this.axis.equals("X") ? i : 0), this.position.getY() + y, this.position.getZ() + (this.axis.equals("Z") ? i : 0)).getBlock();
                block.setType(Material.END_GATEWAY);
            }
        }
    }

    public void closePortal() {
        for (int y = 1; y < 4; y++) {
            for (int i = -1; i <= 1; i++) {
                Block block = new Location(this.position.getWorld(), this.position.getX() + (this.axis.equals("X") ? i : 0), this.position.getY() + y, this.position.getZ() + (this.axis.equals("Z") ? i : 0)).getBlock();
                block.setType(Material.AIR);
            }
        }
    }

    public boolean openConnection(Stargate origin) {
        if (this.wormhole != null) return false;

        this.wormhole = origin;
        this.isOrigin = false;

        if (!this.wormhole.confirmConnection(this)) {
            this.wormhole = null;
            return false;
        }

        return true;
    }

    public boolean closeConnection() {
        if (this.wormhole == null) return false;

        this.wormhole.closePortal();

        if (this.isOrigin)
            this.wormhole.closeConnection();

        this.wormhole = null;
        this.isOrigin = false;

        return true;
    }

    public boolean confirmConnection(Stargate destination) {
        if (this.wormhole != null) return false;

        this.wormhole = destination;
        this.isOrigin = true;

        this.wormhole.showPortal();
        this.showPortal();

        Bukkit.getScheduler().scheduleSyncDelayedTask(parent, new Runnable() {
            @Override
            public void run() {
                closeConnection();
            }
        }, (20L * 60));

        return true;
    }
}
