package com.github.letsgoridepandas.buxtowns.commands;

import com.github.letsgoridepandas.buxtowns.BuxTowns;
import com.github.letsgoridepandas.buxtowns.TownManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TownPromoteCommand implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (sender.hasPermission("buxtowns.promote")) {
                if (args.length != 1) {
                    sender.sendMessage(ChatColor.RED + "/townpromote <townname>");
                    return true;
                }

                String townName = args[0];
                TownManager town = new TownManager(BuxTowns.getInstance(), townName, player);
                town.promoteTown();

            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to do that");
            }


            return true;
        } else {
            BuxTowns.getInstance().getLogger().info("You need to be a player to interact with towns");
            return true;
        }
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

