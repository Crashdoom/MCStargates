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

import me.crashdoom.stargates.commands.StargateCommand;
import me.crashdoom.stargates.entity.Stargate;
import me.crashdoom.stargates.listeners.OnPlayerInteract;
import me.crashdoom.stargates.listeners.OnPlayerMove;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.List;

public class Stargates
        extends JavaPlugin
        implements Listener
{

    public Map<String, IconMenu> dhdMenu = new HashMap<>();
    public Map<String, List<String>> dialCode = new HashMap<>();
    public Map<String, ItemStack> stackMap = new LinkedHashMap<>();

    public Map<String, Stargate> stargates = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("onEnable has been invoked!");

        stackMap.put("A", new ItemStack(Material.GRASS, 1));
        stackMap.put("B", new ItemStack(Material.WOOL, 1));
        stackMap.put("C", new ItemStack(Material.WOOL, 1, (byte)8));
        stackMap.put("D", new ItemStack(Material.WOOL, 1, (byte)7));
        stackMap.put("E", new ItemStack(Material.WOOL, 1, (byte)15));
        stackMap.put("F", new ItemStack(Material.WOOL, 1, (byte)12));
        stackMap.put("G", new ItemStack(Material.WOOL, 1, (byte)11));
        stackMap.put("H", new ItemStack(Material.WOOL, 1, (byte)9));
        stackMap.put("I", new ItemStack(Material.WOOL, 1, (byte)3));
        stackMap.put("J", new ItemStack(Material.WOOL, 1, (byte)5));
        stackMap.put("K", new ItemStack(Material.WOOL, 1, (byte)13));
        stackMap.put("L", new ItemStack(Material.WOOL, 1, (byte)10));
        stackMap.put("M", new ItemStack(Material.WOOL, 1, (byte)2));
        stackMap.put("N", new ItemStack(Material.WOOL, 1, (byte)6));
        stackMap.put("O", new ItemStack(Material.WOOL, 1, (byte)4));
        stackMap.put("P", new ItemStack(Material.WOOL, 1, (byte)1));
        stackMap.put("Q", new ItemStack(Material.WOOL, 1, (byte)14));
        stackMap.put("R", new ItemStack(Material.REDSTONE_BLOCK, 1));

        this.getCommand("stargate").setExecutor(new StargateCommand(this));

        getServer().getPluginManager().registerEvents(new OnPlayerInteract(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerMove(this), this);

        StargateUtils.init(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("onDisable has been invoked!");
    }

    public void sendChatMessage(Player player, String msg) {
        sendChatMessage(player, msg, false);
    }

    public void sendChatMessage(Player player, String msg, boolean hideTag) {
        player.sendMessage((hideTag ? "" : ChatColor.DARK_AQUA + "[Stargate] ") + ChatColor.GRAY + msg);
    }
}
