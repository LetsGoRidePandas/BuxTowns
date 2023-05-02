package com.github.letsgoridepandas.buxtowns.commands;

import com.github.letsgoridepandas.buxtowns.BuxTowns;
import com.github.letsgoridepandas.buxtowns.PlayerManager;
import com.github.letsgoridepandas.buxtowns.TownInvites;
import com.github.letsgoridepandas.buxtowns.TownManager;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeputyCommand implements TabExecutor {
    private boolean canKick;
    private PlayerManager pm;
    private TownManager tm;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            pm = new PlayerManager(BuxTowns.getInstance(),player);
            tm = new TownManager(BuxTowns.getInstance(), pm.getTown(), player);
            File baseFile = new File(BuxTowns.getInstance().getDataFolder(),"config.yml");
            FileConfiguration baseConfig = YamlConfiguration.loadConfiguration(baseFile);
            canKick = baseConfig.getBoolean("enable-deputy-kick");
            if(!pm.getTownRank().equalsIgnoreCase("Deputy")){
                player.sendMessage(ChatColor.RED+"You must be a deputy of a town to use that command");
                return true;
            }
            int length = args.length;
            switch(length){
                case 0:
                    if(canKick)
                        player.sendMessage(ChatColor.RED + "/deputy <invite|kick|resign>");
                    else
                        player.sendMessage(ChatColor.RED + "/deputy <invite|resign>");
                    return true;
                case 1:
                    String subcommand=args[0].toLowerCase();
                    switch(subcommand){
                        case "invite":
                            player.sendMessage(ChatColor.RED+"/deputy invite <player>");
                            break;
                        case "kick":
                            if(canKick)
                                player.sendMessage(ChatColor.RED+"/deputy kick <player>");
                            else
                                player.sendMessage(ChatColor.RED+"Your server does not allow deputies to kick residents. Contact a server admin if you feel this is incorrect");
                            break;
                        case "resign":
                            List<String> deputies = tm.getDeputies();
                            deputies.remove(player.getUniqueId().toString());
                            tm.setDeputies(deputies);
                            pm.setTownRank("Resident");
                            player.sendMessage(ChatColor.GREEN+"You have resigned as Deputy of "+pm.getTown());
                            break;
                        default:
                            if(canKick)
                                player.sendMessage(ChatColor.RED+"/deputy <invite|kick|resign>");
                            else
                                player.sendMessage(ChatColor.RED+"/deputy <invite|resign>");
                    }
                    return true;
                case 2:
                    subcommand=args[0].toLowerCase();
                    String mainarg = args[1].toLowerCase();
                    switch(subcommand) {
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
                            if(!canKick){
                                player.sendMessage(ChatColor.RED+"Your server does not allow deputies to kick residents. Contact a server admin if you feel this is incorrect");
                                return true;
                            }

                            if(mainarg.equalsIgnoreCase(player.getName())){
                                player.sendMessage(ChatColor.RED + "You can't kick yourself");
                                return true;
                            }

                            boolean townmember = false;
                            boolean deputy = false;
                            UUID targetuuid = UUID.randomUUID();
                            OfflinePlayer kicktarget;

                            List<String> residents = tm.getResidents();
                            List<String> deputies = tm.getDeputies();

                            for (String depuuid : deputies) {
                                if(!depuuid.equalsIgnoreCase("N/A")) {
                                    kicktarget = Bukkit.getOfflinePlayer(UUID.fromString(depuuid));
                                    if (kicktarget.getName().equalsIgnoreCase(mainarg)) {
                                        deputy = true;
                                    }
                                }
                            }
                            if(deputy) {
                                player.sendMessage(ChatColor.RED+"You can't kick another deputy");
                                return true;
                            }

                            for (String uuid : residents) {
                                kicktarget = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                                if (kicktarget.getName().equalsIgnoreCase(mainarg)) {
                                    townmember = true;
                                    targetuuid = UUID.fromString(uuid);
                                }
                            }
                            if (townmember) {
                                if(targetuuid.toString().equalsIgnoreCase(tm.getMayor())){
                                    player.sendMessage(ChatColor.RED+"You can't kick the mayor");
                                    return true;
                                }
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
                        case "resign":
                            player.sendMessage(ChatColor.RED+"/deputy resign");
                            break;
                        default:
                            if(canKick)
                                player.sendMessage(ChatColor.RED+"/deputy <invite|kick|resign>");
                            else
                                player.sendMessage(ChatColor.RED+"/deputy <invite|resign>");
                            break;

                    }
                    return true;
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
        File baseFile = new File(BuxTowns.getInstance().getDataFolder(),"config.yml");
        FileConfiguration baseConfig = YamlConfiguration.loadConfiguration(baseFile);
        Player tplayer = (Player) sender;
        pm = new PlayerManager(BuxTowns.getInstance(), tplayer);
        tm = new TownManager(BuxTowns.getInstance(), pm.getTown(), tplayer);
        canKick = baseConfig.getBoolean("enable-deputy-kick");
        if (args.length == 1){
            List<String> mainoptions = new ArrayList<>();
            mainoptions.add("invite");
            if(canKick)
            mainoptions.add("kick");
            mainoptions.add("resign");
            return mainoptions;
        }
        else if(args.length==2){
            if(args[0].equalsIgnoreCase("invite")){
                List<String> playerNames = new ArrayList<>();
                Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
                Bukkit.getServer().getOnlinePlayers().toArray(players);
                for (int i = 0; i < players.length; i++){
                    playerNames.add(players[i].getName());
                }
                playerNames.remove(tplayer.getName());

                return playerNames;
            }else if(args[0].equalsIgnoreCase("kick") && canKick){
                List<String> playerNames = new ArrayList<>();
                for (String uuid : tm.getResidents()) {
                    OfflinePlayer oplayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if(!tm.getDeputies().contains(uuid) && !tm.getMayor().equalsIgnoreCase(uuid) && !oplayer.getName().equalsIgnoreCase(tplayer.getName()))
                    playerNames.add(oplayer.getName());

                }

                return playerNames;
            }else return blank;


        }else if(args.length>2){
            return blank;
        }
        return null;
    }
}
