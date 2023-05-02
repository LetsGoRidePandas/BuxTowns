package com.github.letsgoridepandas.buxtowns.commands;

import com.github.letsgoridepandas.buxtowns.BuxTowns;
import com.github.letsgoridepandas.buxtowns.PlayerManager;
import com.github.letsgoridepandas.buxtowns.TownInvites;
import com.github.letsgoridepandas.buxtowns.TownManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TownInviteCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            boolean hasInvite = TownInvites.hasInvite(player.getUniqueId().toString());
            if (hasInvite){
                if(args.length==0) {
                    player.sendMessage(ChatColor.GREEN + "You have an active invite from " + TownInvites.getTown(player.getUniqueId().toString()));
                    return true;
                }
                else if(args.length==1){
                    if(args[0].equalsIgnoreCase("accept")){
                        PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),player);
                        TownManager tm = new TownManager(BuxTowns.getInstance(),
                                TownInvites.getTown(player.getUniqueId().toString()),player);
                        if(!pm.getTown().equalsIgnoreCase("N/A")){
                        player.sendMessage(ChatColor.RED+"You are already in a town. Please do /townleave first");
                        return true;
                        }
                        List<String> residents = tm.getResidents();
                        residents.add(player.getUniqueId().toString());
                        tm.setResidents(residents);
                        tm.setResidentAmount(tm.getResidentAmount()+1);
                        pm.setTownRank("Resident");
                        pm.setTown(tm.getName());
                        player.sendMessage(ChatColor.GREEN+"You have "+ChatColor.DARK_GREEN+"ACCEPTED "+ChatColor.GREEN+"the invitation of "+TownInvites.getTown(player.getUniqueId().toString()));
                        TownInvites.removeInvite(player.getUniqueId().toString());
                    }
                    else if(args[0].equalsIgnoreCase("deny")){
                        player.sendMessage(ChatColor.GREEN+"You have "+ChatColor.DARK_GREEN+"DENIED "+ChatColor.GREEN+"the invitation of "+TownInvites.getTown(player.getUniqueId().toString()));
                        TownInvites.removeInvite(player.getUniqueId().toString());
                        return true;
                    }else{
                        player.sendMessage(ChatColor.RED+"/towninvite [accept|deny]");
                        return true;
                    }

                }
                else{
                    player.sendMessage(ChatColor.RED+"/towninvite [accept|deny]");
                    return true;
                }


            }else{
                player.sendMessage(ChatColor.RED+"You do not have any town invites");
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
            options.add("accept");
            options.add("deny");
            return options;
        }else return blank;

    }
}
