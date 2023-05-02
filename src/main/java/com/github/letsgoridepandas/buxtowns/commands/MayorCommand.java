package com.github.letsgoridepandas.buxtowns.commands;

import com.github.letsgoridepandas.buxtowns.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class MayorCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),player);
            TownManager tm = new TownManager(BuxTowns.getInstance(), pm.getTown(), player);
            File baseFile = new File(BuxTowns.getInstance().getDataFolder(),"config.yml");
            FileConfiguration baseConfig = YamlConfiguration.loadConfiguration(baseFile);
            if(!pm.getTownRank().equalsIgnoreCase("Mayor")){
                player.sendMessage(ChatColor.RED+"You must be a mayor of a town to use that command");
                return true;
            }
            int length = args.length;
            switch(length){
                case 0:
                    player.sendMessage(ChatColor.RED + "/mayor <deputy|invite|kick|taxamount|taxstyle|transfer|withdraw>");
                    return true;
                case 1:
                    String subcommand=args[0].toLowerCase();
                    player.sendMessage(subcommand+"");
                    switch(subcommand){
                        case "deputy":
                            player.sendMessage(ChatColor.RED+"/mayor deputy <add|remove> <player>");
                            break;
                        case "invite":
                            player.sendMessage(ChatColor.RED+"/mayor invite <player>");
                            break;
                        case "kick":
                            player.sendMessage(ChatColor.RED+"/mayor kick <player>");
                            break;
                        case "withdraw":
                            player.sendMessage(ChatColor.RED+"/mayor withdraw <amount> <reason>");
                            break;
                        case "transfer":
                            player.sendMessage(ChatColor.RED+"/mayor transfer <player>");
                            break;
                        case "taxstyle":
                            player.sendMessage(ChatColor.RED+"/mayor taxstyle <flat|percent> Changing this will reset tax amount to 0");
                            break;
                        case "taxamount":
                            player.sendMessage(ChatColor.RED+"/mayor taxamount <amount>");
                            break;
                        default:
                            player.sendMessage(ChatColor.RED+"/mayor <deputy|invite|kick|taxamount|taxstyle|transfer|withdraw>");

                    }
                    return true;
                case 2:
                    subcommand=args[0].toLowerCase();
                    String mainarg = args[1].toLowerCase();
                    switch(subcommand) {
                        case "deputy":
                            switch (mainarg) {
                                case "add":
                                    player.sendMessage(ChatColor.RED + "/mayor deputy add <player>");
                                    break;
                                case "remove":
                                    player.sendMessage(ChatColor.RED + "/mayor deputy remove <player>");
                                    break;
                                default:
                                    player.sendMessage(ChatColor.RED + "/mayor deputy <add|remove> <player>");
                                    break;
                            }
                            break;
                        case "invite":
                            Player p = Bukkit.getPlayerExact(mainarg);
                            if (p == player) {
                                player.sendMessage(ChatColor.RED + "You can't invite yourself");
                                return true;
                            }
                            if (p instanceof Player) {
                                if (!TownInvites.hasInvite(p.getUniqueId().toString())) {
                                    TownInvites.setInvites(p.getUniqueId().toString(), pm.getTown());
                                    player.sendMessage(ChatColor.GREEN + "You have invited " + p.getName() + " to join " + pm.getTown());
                                    p.sendMessage(ChatColor.DARK_GREEN + player.getName() + ChatColor.GREEN + " has invited you to join " + ChatColor.DARK_GREEN + pm.getTown());
                                    p.sendMessage(ChatColor.DARK_GREEN + "use /towninvite <accept|deny> within " + ChatColor.GREEN + "5 minutes " + ChatColor.DARK_GREEN + "to respond.");
                                    new BukkitRunnable() {

                                        @Override
                                        public void run() {
                                            // What you want to schedule goes here
                                            if (TownInvites.hasInvite(p.getUniqueId().toString())) {
                                                TownInvites.removeInvite(p.getUniqueId().toString());
                                            }
                                        }

                                    }.runTaskLater(BuxTowns.getInstance(), 20L * 60 * 5);
                                }else{
                                    player.sendMessage(ChatColor.RED + "That player already has an active town invite");
                                    p.sendMessage(ChatColor.RED+player.getName()+" has tried to invite you to join "+tm.getName()+" but you already " +
                                            "have an active invite from "+TownInvites.getTown(p.getUniqueId().toString()));
                                    p.sendMessage(ChatColor.RED+"Please do /towninvite <accept|deny> first");
                                }
                            } else
                                player.sendMessage(ChatColor.RED + "That player is either offline or does not exist");
                            break;
                        case "kick":

                            if(mainarg.equalsIgnoreCase(player.getName())){
                                player.sendMessage(ChatColor.RED + "You can't kick yourself");
                                return true;
                            }

                            boolean townmember = false;
                            boolean deputy = false;
                            UUID targetuuid = UUID.randomUUID();
                            OfflinePlayer kicktarget;
                            String deputyUUID = "";
                            List<String> residents = tm.getResidents();
                            List<String> deputies = tm.getDeputies();
                            for (String depuuid : deputies) {
                                if(!depuuid.equalsIgnoreCase("N/A")) {
                                    kicktarget = Bukkit.getOfflinePlayer(UUID.fromString(depuuid));
                                    if (kicktarget.getName().equalsIgnoreCase(mainarg)) {
                                        deputy = true;
                                        deputyUUID=depuuid;

                                    }
                                }
                            }
                            if(deputy) {
                                deputies.remove(deputyUUID);
                                tm.setDeputies(deputies);
                            }

                            for (String uuid : residents) {
                                kicktarget = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if (kicktarget.getName().equalsIgnoreCase(mainarg)) {
                                    townmember = true;
                                    targetuuid = UUID.fromString(uuid);
                                }
                            }
                            if (townmember) {
                                residents.remove(targetuuid.toString());
                                tm.setResidents(residents);
                                tm.setResidentAmount(tm.getResidentAmount() - 1);
                                PlayerManager kickpm = new PlayerManager(BuxTowns.getInstance(), Bukkit.getOfflinePlayer(targetuuid));
                                kickpm.setTown("N/A");
                                kickpm.setTownRank("N/A");
                                player.sendMessage(ChatColor.GREEN + kickpm.getName() + " is no longer a " + tm.getName() + " citizen.");
                            } else {
                                player.sendMessage(ChatColor.RED + mainarg + " is not a " + tm.getName() + " citizen.");
                            }
                            break;
                        case "transfer":
                            if(mainarg.equalsIgnoreCase(player.getName())){
                                player.sendMessage(ChatColor.RED + "You can't transfer to yourself");
                                return true;
                            }
                            OfflinePlayer transfertarget;
                            townmember = false;
                            deputy = false;
                            targetuuid = UUID.randomUUID();
                            deputyUUID = "";
                            residents = tm.getResidents();
                            deputies = tm.getDeputies();
                            for (String depuuid : deputies) {
                                if(!depuuid.equalsIgnoreCase("N/A")) {
                                    transfertarget = Bukkit.getOfflinePlayer(UUID.fromString(depuuid));
                                    if (transfertarget.getName().equalsIgnoreCase(mainarg)) {
                                        deputy = true;
                                        deputyUUID=depuuid;

                                    }
                                }
                            }
                            if(deputy) {
                                deputies.remove(deputyUUID);
                                tm.setDeputies(deputies);
                            }

                            for (String uuid : residents) {
                                transfertarget = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if (transfertarget.getName().equalsIgnoreCase(mainarg)) {
                                    townmember = true;
                                    targetuuid = UUID.fromString(uuid);
                                }
                            }
                            if (townmember) {
                                PlayerManager transferpm = new PlayerManager(BuxTowns.getInstance(), Bukkit.getOfflinePlayer(targetuuid));
                                transferpm.setTownRank("Mayor");
                                pm.setTownRank("Resident");
                                tm.setMayor(targetuuid.toString());
                                player.sendMessage(ChatColor.GREEN + transferpm.getName() + " is now the Mayor of " + tm.getName());
                            } else {
                                player.sendMessage(ChatColor.RED + mainarg + " is not a " + tm.getName() + " citizen.");
                            }
                            break;
                        case "withdraw":
                            player.sendMessage(ChatColor.RED+"/mayor withdraw <amount> <reason>");
                            break;
                        case "taxstyle":
                            switch(mainarg){
                                case "flat":
                                    tm.setTaxStyle("flat");
                                    tm.setResidentTax(0);
                                    player.sendMessage(ChatColor.GREEN+pm.getTown()+"'s Tax Style has been set to flat.");
                                    player.sendMessage(ChatColor.GREEN+pm.getTown()+"'s Tax Amount has been reset to 0.");
                                    player.sendMessage(ChatColor.GREEN+"Please do /mayor taxamount <amount> to set a new value if you want to collect taxes");
                                    break;
                                case "percent":
                                    tm.setTaxStyle("percent");
                                    tm.setResidentTax(0);
                                    player.sendMessage(ChatColor.GREEN+pm.getTown()+"'s Tax Style has been set to percent.");
                                    player.sendMessage(ChatColor.GREEN+pm.getTown()+"'s Tax Amount has been reset to 0.");
                                    player.sendMessage(ChatColor.GREEN+"Please do /mayor taxamount <amount> to set a new value if you want to collect taxes");
                                    break;
                                default:
                                    player.sendMessage(ChatColor.RED+"/mayor taxstyle <flat|percent> Changing this will reset tax amount to 0");
                                    break;
                            }
                            break;
                        case "taxamount":
                            double amount=0;
                            try{
                                amount = Double.parseDouble(mainarg);
                            }catch (NumberFormatException e){
                                player.sendMessage(ChatColor.RED+"Invalid Number");
                                return true;
                            }
                            if(amount<0){
                                player.sendMessage(ChatColor.RED+"Amount must positive or 0");
                                return true;
                            }
                            if(tm.getTaxStyle().equalsIgnoreCase("flat")){
                                if(amount>baseConfig.getDouble("resident-tax-max-flat")){
                                    player.sendMessage(ChatColor.RED+"Flat Taxes must be between 0 and "+baseConfig.getDouble("resident-tax-max-flat"));
                                    return true;
                                }
                                tm.setResidentTax(amount);
                                player.sendMessage(ChatColor.GREEN+pm.getTown()+"'s Tax Amount has been set to $"+amount);

                            }
                            else if(tm.getTaxStyle().equalsIgnoreCase("percent")){
                                if(amount>baseConfig.getDouble("resident-tax-max-percent")){
                                    player.sendMessage(ChatColor.RED+"Percent Taxes must be between 0 and "+baseConfig.getDouble("resident-tax-max-percent"));
                                    return true;
                                }
                                tm.setResidentTax(amount);
                                player.sendMessage(ChatColor.GREEN+pm.getTown()+"'s Tax Amount has been set to "+amount+"%");
                            }

                            break;
                        default:
                            player.sendMessage(ChatColor.RED+"/mayor <deputy|invite|kick|taxamount|taxstyle|transfer|withdraw>");
                            break;

                    }
                    return true;
                case 3:
                    subcommand=args[0].toLowerCase();
                    mainarg = args[1].toLowerCase();
                    String subarg = args[2].toLowerCase();
                    switch(subcommand){
                        case "deputy":
                            OfflinePlayer deputytarget;
                            boolean townmember = false;
                            boolean deputy = false;
                            UUID targetuuid = UUID.randomUUID();
                            List<String> residents = tm.getResidents();
                            List<String> deputies = tm.getDeputies();
                            switch(mainarg){
                                case "add":
                                    if(subarg.equalsIgnoreCase(player.getName())){
                                        player.sendMessage(ChatColor.RED + "You can't add yourself as a deputy");
                                        return true;
                                    }

                                    for (String depuuid : deputies) {
                                        if(!depuuid.equalsIgnoreCase("N/A")) {
                                           deputytarget = Bukkit.getOfflinePlayer(UUID.fromString(depuuid));
                                            if (deputytarget.getName().equalsIgnoreCase(subarg)) {
                                                deputy = true;
                                            }
                                        }
                                    }
                                    if(deputy) {
                                        player.sendMessage(ChatColor.RED + "That player is already a deputy");
                                        return true;
                                    }
                                    for (String uuid : residents) {
                                        deputytarget = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                        if (deputytarget.getName().equalsIgnoreCase(subarg)) {
                                            townmember = true;
                                            targetuuid = UUID.fromString(uuid);
                                        }
                                    }
                                    if (townmember) {
                                        PlayerManager deputypm = new PlayerManager(BuxTowns.getInstance(), Bukkit.getOfflinePlayer(targetuuid));
                                        deputypm.setTownRank("Deputy");
                                        deputies.add(targetuuid.toString());
                                        tm.setDeputies(deputies);
                                        player.sendMessage(ChatColor.GREEN + deputypm.getName() + " is now a deputy of " + tm.getName());
                                    } else {
                                        player.sendMessage(ChatColor.RED + subarg + " is not a " + tm.getName() + " citizen.");
                                    }
                                    break;
                                case "remove":
                                    for (String depuuid : deputies) {
                                        if(!depuuid.equalsIgnoreCase("N/A")) {
                                            deputytarget = Bukkit.getOfflinePlayer(UUID.fromString(depuuid));
                                            if (deputytarget.getName().equalsIgnoreCase(subarg)) {
                                                targetuuid=UUID.fromString(depuuid);
                                                deputy = true;
                                            }
                                        }
                                    }
                                    if(deputy) {
                                        PlayerManager deputypm = new PlayerManager(BuxTowns.getInstance(), Bukkit.getOfflinePlayer(targetuuid));
                                        deputypm.setTownRank("Resident");
                                        deputies.remove(targetuuid.toString());
                                        tm.setDeputies(deputies);
                                        player.sendMessage(ChatColor.GREEN + deputypm.getName() + " is no longer a deputy of " + tm.getName());
                                        return true;
                                    }
                                    else {
                                        player.sendMessage(ChatColor.RED + subarg + " is not a deputy of " + tm.getName());
                                    }
                                    break;
                                default:
                                    player.sendMessage(ChatColor.RED+"/mayor deputy <add|remove> <player>");
                            }
                            break;
                        case "invite":
                            player.sendMessage(ChatColor.RED+"/mayor invite <player>");
                            break;
                        case "kick":
                            player.sendMessage(ChatColor.RED+"/mayor kick <player>");
                            break;
                        case "transfer":
                            player.sendMessage(ChatColor.RED+"/mayor transfer <player>");
                            break;
                        case "withdraw":
                            double amount = 0;
                            try{
                                amount = Double.parseDouble(mainarg);
                            }catch(NumberFormatException e) {
                                player.sendMessage(ChatColor.RED + "Invalid Number");
                                return true;
                            }
                            if(amount<=0){
                                player.sendMessage(ChatColor.RED+"Amount must be greater than 0");
                                return true;
                            }
                            ArrayList<String> uuids = new ArrayList<>();
                            double online = 0;
                            for(String uuid : tm.getResidents()){
                                Player res = Bukkit.getPlayer(UUID.fromString(uuid));
                                if(res instanceof Player){
                                    uuids.add(uuid);
                                    online++;
                                }
                            }
                            if(online<3){
                                player.sendMessage(ChatColor.RED + "At least 3 town members must be online to start a withdraw vote");
                                return true;
                            }
                            double required = Math.round((online*baseConfig.getDouble("bank-withdrawal-threshold"))/100);
                            TownVote tv = new TownVote(tm.getName(), tm.getMayor(), subarg, amount, uuids, required, 0);
                            if (!Vote.hasVote(tm.getName())) {
                                tv.createVote();

                                for(String uuid: tv.getOnlineResidents()){
                                    Player res = Bukkit.getPlayer(UUID.fromString(uuid));
                                    if(res instanceof Player){
                                        res.sendMessage(ChatColor.GREEN+player.getName()+" has started vote for the withdrawal of $"
                                                +ChatColor.DARK_GREEN+tv.getAmount()+ChatColor.GREEN+" for "
                                                +ChatColor.DARK_GREEN+tv.getMessage()+ChatColor.GREEN+"The vote will last for "
                                                +ChatColor.DARK_GREEN+"5 minutes"+ChatColor.GREEN+" Do /townvote [yes|no] to vote ");
                                    }
                                }

                                new BukkitRunnable() {

                                    @Override
                                    public void run() {
                                        // What you want to schedule goes here
                                        if (Vote.hasVote(tm.getName())) {
                                            tv.deleteVote();

                                            for(String uuid: tv.getOnlineResidents()){
                                                Player res = Bukkit.getPlayer(UUID.fromString(uuid));
                                                if(res instanceof Player){
                                                    res.sendMessage(ChatColor.GREEN+"The vote for the withdrawal of "
                                                            +ChatColor.DARK_GREEN+tv.getAmount()+ChatColor.GREEN+" for "
                                                            +ChatColor.DARK_GREEN+tv.getMessage()+ChatColor.GREEN+" has been "
                                                            +ChatColor.DARK_GREEN+"DENIED");
                                                }
                                            }
                                        }
                                    }
                                }.runTaskLater(BuxTowns.getInstance(), 20L * 60 * 5);
                            }else{
                                player.sendMessage(ChatColor.RED + "You must wait for the current vote to end first");
                            }


                            break;
                        case "taxstyle":
                            player.sendMessage(ChatColor.RED+"/mayor taxstyle <flat|percent> Changing this will reset tax amount to 0");
                            break;
                        case "taxamount":
                            player.sendMessage(ChatColor.RED+"/mayor taxamount <amount>");
                            break;
                        default:
                            player.sendMessage(ChatColor.RED+"/mayor <deputy|invite|kick|taxamount|taxstyle|transfer|withdraw>");
                            break;

                    }
                    return true;

            }
            if(length>=4){
                String subcommand=args[0].toLowerCase();
                switch(subcommand){
                    case "deputy":
                        player.sendMessage(ChatColor.RED+"/mayor deputy <add|remove> <player>");
                        break;
                    case "invite":
                        player.sendMessage(ChatColor.RED+"/mayor invite <player>");
                        break;
                    case "kick":
                        player.sendMessage(ChatColor.RED+"/mayor kick <player>");
                        break;
                    case "transfer":
                        player.sendMessage(ChatColor.RED+"/mayor transfer <player>");
                        break;
                    case "withdraw":
                        double amount = 0;
                        try{
                            amount = Double.parseDouble(args[1]);
                        }catch(NumberFormatException e) {
                            player.sendMessage(ChatColor.RED + "Invalid Number");
                            return true;
                        }
                        if(amount<=0){
                            player.sendMessage(ChatColor.RED+"Amount must be greater than 0");
                            return true;
                        }
                        StringBuilder message = new StringBuilder(100);
                        for (int i = 2; i < args.length; i++) {
                            message.append(args[i]).append(" ");
                        }
                        String msg = message.toString();
                        /*int i=0;
                        for (String arg: args){
                            if(i>1)
                            message.append(arg);
                        }*/

                        ArrayList<String> uuids = new ArrayList<>();
                        double online = 0;
                        for(String uuid : tm.getResidents()){
                            Player res = Bukkit.getPlayer(UUID.fromString(uuid));
                            if(res instanceof Player){
                                uuids.add(uuid);
                                online++;
                            }
                        }
                        if(online<3){
                            player.sendMessage(ChatColor.RED + "At least 3 town members must be online to start a withdraw vote");
                            return true;
                        }
                        double required = Math.round((online*baseConfig.getDouble("bank-withdrawal-threshold"))/100);
                        TownVote tv = new TownVote(tm.getName(), tm.getMayor(), msg, amount, uuids, required, 0);
                        if (!Vote.hasVote(tm.getName())) {
                            tv.createVote();

                            for(String uuid: tv.getOnlineResidents()){
                                Player res = Bukkit.getPlayer(UUID.fromString(uuid));
                                if(res instanceof Player){
                                    res.sendMessage(ChatColor.GREEN+player.getName()+" has started vote for the withdrawal of $"
                                            +ChatColor.DARK_GREEN+tv.getAmount()+ChatColor.GREEN+" for "
                                            +ChatColor.DARK_GREEN+tv.getMessage()+ChatColor.GREEN+"The vote will last for "
                                            +ChatColor.DARK_GREEN+"5 minutes"+ChatColor.GREEN+" Do /townvote [yes|no] to vote ");
                                }
                            }

                            new BukkitRunnable() {

                                @Override
                                public void run() {
                                    // What you want to schedule goes here
                                    if (Vote.hasVote(tm.getName())) {
                                        tv.deleteVote();

                                        for(String uuid: tv.getOnlineResidents()){
                                            Player res = Bukkit.getPlayer(UUID.fromString(uuid));
                                            if(res instanceof Player){
                                                res.sendMessage(ChatColor.GREEN+"The vote for the withdrawal of "
                                                        +ChatColor.DARK_GREEN+tv.getAmount()+ChatColor.GREEN+" for "
                                                        +ChatColor.DARK_GREEN+tv.getMessage()+ChatColor.GREEN+" has been "
                                                        +ChatColor.DARK_GREEN+"DENIED");
                                            }
                                        }
                                    }
                                }
                            }.runTaskLater(BuxTowns.getInstance(), 20L * 60 * 5);
                        }else{
                            player.sendMessage(ChatColor.RED + "You must wait for the current vote to end first");
                        }
                        break;
                    case "taxstyle":
                        player.sendMessage(ChatColor.RED+"/mayor taxstyle <flat|percent> Changing this will reset tax amount to 0");
                        break;
                    case "taxamount":
                        player.sendMessage(ChatColor.RED+"/mayor taxamount <amount>");
                        break;
                    default:
                        player.sendMessage(ChatColor.RED+"/mayor <deputy|invite|kick|taxamount|taxstyle|transfer|withdraw>");
                        break;

                }
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
            options.add("deputy");
            options.add("invite");
            options.add("kick");
            options.add("taxamount");
            options.add("taxstyle");
            options.add("transfer");
            options.add("withdraw");
            return options;
        }else if (args.length == 2){
            Player tplayer = (Player) sender;
            PlayerManager pm = new PlayerManager(BuxTowns.getInstance(), tplayer);
            TownManager tm = new TownManager(BuxTowns.getInstance(), pm.getTown(), tplayer);
            List<String> options = new ArrayList<>();
            String subcommand = args[0].toLowerCase();
            switch(subcommand){
                case "deputy":
                    options.add("add");
                    options.add("remove");
                    return options;
                case "invite":
                    List<String> playerNames = new ArrayList<>();
                    Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                    Bukkit.getServer().getOnlinePlayers().toArray(players);
                    for (int i = 0; i < players.length; i++){
                        playerNames.add(players[i].getName());
                    }
                        playerNames.remove(tplayer.getName());
                    return playerNames;
                case "kick":
                case "transfer":
                    playerNames = new ArrayList<>();
                    for (String uuid : tm.getResidents()) {
                        OfflinePlayer oplayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                            playerNames.add(oplayer.getName());
                    }
                            playerNames.remove(tplayer.getName());
                    return playerNames;

                case "taxamount":
                case "withdraw":
                    options.add("<amount>");
                    return options;
                case "taxstyle":
                    options.add("flat");
                    options.add("percent");
                    return options;
                default: return blank;

            }

        }
        else if(args.length == 3){
            String subcommand = args[0].toLowerCase();
            String mainarg = args[1].toLowerCase();
            List<String> options = new ArrayList<>();
            Player tplayer = (Player) sender;
            PlayerManager pm = new PlayerManager(BuxTowns.getInstance(), tplayer);
            TownManager tm = new TownManager(BuxTowns.getInstance(), pm.getTown(), tplayer);
            switch(subcommand){
                case "deputy":
                    switch(mainarg){

                        case "add":
                            List<String> playerNames = new ArrayList<>();
                            for (String uuid : tm.getResidents()) {
                                OfflinePlayer oplayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(!tm.getDeputies().contains(uuid) && !tm.getMayor().equalsIgnoreCase(uuid) && !oplayer.getName().equalsIgnoreCase(tplayer.getName()))
                                    playerNames.add(oplayer.getName());

                            }
                            return playerNames;
                        case "remove":
                            playerNames = new ArrayList<>();
                            for (String uuid : tm.getResidents()) {
                                OfflinePlayer oplayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if(tm.getDeputies().contains(uuid))
                                    playerNames.add(oplayer.getName());

                            }

                            return playerNames;
                        default: return blank;

                    }

                case "withdraw":
                    options.add("<message>");
                    return options;
                default: return blank;

            }
        }else if(args.length>3){

            return blank;
        }

        return null;
    }
}
