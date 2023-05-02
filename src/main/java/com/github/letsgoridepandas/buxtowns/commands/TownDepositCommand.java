package com.github.letsgoridepandas.buxtowns.commands;

import com.github.letsgoridepandas.buxtowns.BuxTowns;
import com.github.letsgoridepandas.buxtowns.PlayerManager;
import com.github.letsgoridepandas.buxtowns.TownManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TownDepositCommand implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),player);
                if(pm.getTown().equalsIgnoreCase("N/A")){
                   player.sendMessage(ChatColor.RED+"You must be in a town to do that");
                   return true;
                }

                if (args.length != 1) {
                    player.sendMessage(ChatColor.RED + "/towndeposit <amount>");
                    return true;
                }
                TownManager town = new TownManager(BuxTowns.getInstance(), pm.getTown(), player);
                double amount = 0;
                try{
                    amount = Double.parseDouble(args[0]);
                }catch (NumberFormatException e){
                    player.sendMessage(ChatColor.RED+"Invalid Number");
                    return true;
                }
                Economy econ = BuxTowns.getEconomy();
                if(amount>econ.getBalance(player)){
                    player.sendMessage(ChatColor.RED+"Amount greater than balance");
                    return true;
                }
                if(amount<=0){
                    player.sendMessage(ChatColor.RED+"Amount must be greater than 0");
                    return true;
                }
                town.deposit(amount);

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
            options.add("<amount>");
            return options;
        }else return blank;

    }
}
