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


public class TownLeaveCommand implements TabExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),player);
            TownManager tm = new TownManager(BuxTowns.getInstance(),pm.getTown(),player);
            if (!pm.getTown().equalsIgnoreCase("N/A")) {
                if (args.length != 0) {
                    sender.sendMessage(ChatColor.RED + "/townleave");
                    return true;
                }
                if(pm.getTownRank().equalsIgnoreCase("Mayor")){
                    player.sendMessage(ChatColor.RED+"You cannot leave a town as Mayor. Do /mayor transfer <player> first.");
                    return true;
                }
                boolean deputy = false;
                List<String> residents = tm.getResidents();
                List<String> deputies = tm.getDeputies();
                for (String depuuid : deputies) {
                    if(!depuuid.equalsIgnoreCase("N/A")) {
                        if (player.getUniqueId().toString().equalsIgnoreCase(depuuid)) {
                            deputy = true;

                        }
                    }
                }
                if(deputy) {
                    deputies.remove(player.getUniqueId().toString());
                    tm.setDeputies(deputies);
                }
                residents.remove(player.getUniqueId().toString());
                tm.setResidents(residents);
                tm.setResidentAmount(tm.getResidentAmount() - 1);
                pm.setTown("N/A");
                pm.setTownRank("N/A");
                player.sendMessage(ChatColor.GREEN +" You are no longer a " + tm.getName() + " citizen.");

            } else {
                sender.sendMessage(ChatColor.RED + "You need to be in a town to leave a town");
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
        return blank;
    }
}
