package com.github.letsgoridepandas.buxtowns.commands;

import com.github.letsgoridepandas.buxtowns.*;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TownVoteCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),player);
            boolean hasVote = Vote.hasVote(pm.getTown());
            if (hasVote){
                if(args.length==0) {
                    player.sendMessage(ChatColor.GREEN + "Active Vote: $" +Vote.getAmount(pm.getTown())+" for "+Vote.getMessage(pm.getTown()));
                    return true;
                }
                else if(args.length==1){
                    if(args[0].equalsIgnoreCase("yes")){
                        String message = Vote.setVote(pm.getTown(),1,player.getUniqueId().toString());
                        player.sendMessage(message);
                        return true;
                    }
                    else if(args[0].equalsIgnoreCase("no")){
                        String message = Vote.setVote(pm.getTown(),0,player.getUniqueId().toString());
                        player.sendMessage(message);
                        return true;
                    }
                    else{
                        player.sendMessage(ChatColor.RED+"/townvote [yes|no]");
                        return true;
                    }

                }
                else{
                    player.sendMessage(ChatColor.RED+"/townvote [yes|no]");
                    return true;
                }


            }else{
                player.sendMessage(ChatColor.RED+"There are no active town votes");
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
            options.add("yes");
            options.add("no");
            return options;
        }else return blank;
    }
}



