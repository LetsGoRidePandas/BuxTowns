package com.github.letsgoridepandas.buxtowns.commands;

import com.github.letsgoridepandas.buxtowns.BuxTowns;
import com.github.letsgoridepandas.buxtowns.PlayerManager;
import com.github.letsgoridepandas.buxtowns.TownManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class TownCreateCommand implements TabExecutor {
    private final BuxTowns plugin;

    public TownCreateCommand(BuxTowns plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("buxtowns.create")){
                if(args.length != 1){
                    player.sendMessage(ChatColor.RED+"/towncreate <townname>");
                    return true;
                }
                PlayerManager pm = new PlayerManager(plugin,player);
                if (!pm.getTown().equals("N/A")){
                    player.sendMessage(ChatColor.RED+"You cannot create a town if you are already in one");
                    return true;
                }
                String townName = args[0];
                TownManager town = new TownManager(plugin,townName,player);
                town.createTown();


                }
            else player.sendMessage(ChatColor.RED+"You don't have permission to do that");

            }
        else {
            plugin.getLogger().info("You need to be a player to interact with towns");
            return true;
        }
        return true;
    }
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> blank = new ArrayList<>();
        if (args.length == 1){
            List<String> options = new ArrayList<>();
            options.add("<townname>");
            return options;
        }else return blank;

    }
}
