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
package me.crashdoom.stargates;

import me.crashdoom.stargates.entity.Stargate;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Map;

public class StargateUtils {
    private static Stargates parent;

    public static void init(Stargates stargates) {
        parent = stargates;
    }

    public static Stargate getStargateByLocation(Location location) {
        for (Map.Entry<String, Stargate> stargate : parent.stargates.entrySet()) {
            if (stargate.getValue().getPosition().getX() == Math.floor(location.getX()) &&
                    stargate.getValue().getPosition().getY() == Math.floor(location.getY()) &&
                    stargate.getValue().getPosition().getZ() == Math.floor(location.getZ()))
                return stargate.getValue();
        }

        return null;
    }

    public static Stargate getStargateByAddress(String address) {
        return parent.stargates.get(address);
    }

    public static Stargate getNearbyStargate(Location playerLocation) {
        int x = (int)Math.floor(playerLocation.getX());
        int y = (int)Math.floor(playerLocation.getY());
        int z = (int)Math.floor(playerLocation.getZ());

        x -= 5;
        y -= 5;
        z -= 5;

        for (x = x; x <= (int)Math.floor(playerLocation.getX()) + 5; x++) {
            for (z = z; z <= (int)Math.floor(playerLocation.getZ()) + 5; z++) {
                for (y = y; y <= (int)Math.floor(playerLocation.getY()) + 5; y++) {
                    Stargate stargate = getStargateByLocation(new Location(playerLocation.getWorld(), x, y, z));

                    if (stargate != null)
                        return stargate;
                }
                y = (int)Math.floor(playerLocation.getY()) - 5;
            }
            z = (int)Math.floor(playerLocation.getZ()) - 5;
        }

        return null;
    }

    public static String getStargateAxis(Location location) {
        boolean checkX = isPortalBlock(new Location(location.getWorld(), Math.floor(location.getX()) + 1.0, location.getY(), location.getZ()), location);
        boolean checkZ = isPortalBlock(new Location(location.getWorld(), location.getX(), location.getY(), Math.floor(location.getZ()) + 1.0), location);

        return (checkX ? "X" : (checkZ ? "Z" : null));
    }

    public static boolean isStargateBuildValid(Location location) {
        boolean isValid = true;

        String axis = getStargateAxis(location);

        if (axis == null) {
            isValid = false;
        } else {
            int cl;

            if (axis.equals("X")) { // X-axis
                cl = (int)Math.floor(location.getX());
            } else { // Z-axis
                cl = (int)Math.floor(location.getZ());
            }

            int cy = (int)Math.floor(location.getY());
            int totalCount = 0;
            for (int a = cl - 2; a <= cl + 2; a++) {
                int passCount = 0;
                for (int y = cy; y <= cy + 4; y++) {
                    if (isPortalBlock(new Location(location.getWorld(), (axis.equals("X") ? a : location.getX()), y, (axis.equals("Z") ? a : location.getZ())), location)) {
                        passCount += 1;
                        totalCount += 1;
                    }
                }

                if (passCount != 5 && passCount != 2) {
                    isValid = false;
                    break;
                }
            }

            if (totalCount != 16) {
                isValid = false;
            }
        }

        return isValid;
    }

    public static boolean isStargateValid(Location location) {
        return (isStargateBuildValid(location) && getStargateByLocation(location) != null);
    }

    private static boolean isPortalBlock(Location location, Location centre) {
        return (location.getX() == centre.getX() &&
                location.getZ() == centre.getZ() &&
                location.getY() == centre.getY() + 4 ? location.getBlock().getType() == Material.REDSTONE_BLOCK : location.getBlock().getType() == Material.OBSIDIAN);
    }
}
