package com.github.letsgoridepandas.buxtowns;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class TownManager {
    private final BuxTowns plugin;
    private final FileConfiguration townConfig;
    private final File configFile;
    private Player player;
    private OfflinePlayer offlinePlayer;
    private final String town;
    private final String townYML;
    private Economy econ = BuxTowns.getEconomy();
    private DecimalFormat moneyformat = new DecimalFormat("#,###,##0.00");


    public TownManager(BuxTowns plugin, String town, Player player){
        this.plugin = plugin;
        this.player = player;
        this.town = town;
        this.townYML = town.toLowerCase()+".yml";
        this.configFile = new File(this.plugin.getDataFolder()+File.separator+"Towns",this.townYML);
        this.townConfig = YamlConfiguration.loadConfiguration(configFile);

    }

    public TownManager(BuxTowns plugin, String town, OfflinePlayer player){
        this.plugin = plugin;
        this.offlinePlayer = player;
        this.town = town;
        this.townYML = town.toLowerCase()+".yml";
        this.configFile = new File(this.plugin.getDataFolder()+File.separator+"Towns",this.townYML);
        this.townConfig = YamlConfiguration.loadConfiguration(configFile);

    }

    public void createTown(){
        econ = BuxTowns.getEconomy();
        moneyformat = new DecimalFormat("#,###,##0.00");
        double balance = econ.getBalance(player);
        File baseFile = new File(this.plugin.getDataFolder(),"config.yml");
        FileConfiguration baseConfig = YamlConfiguration.loadConfiguration(baseFile);
        PlayerManager pm = new PlayerManager(plugin,player);
        String formatted = moneyformat.format(baseConfig.getDouble("Town-ranks.Rank-1.cost"));

        if(!this.configFile.exists()){
            if(balance<baseConfig.getDouble("Town-ranks.Rank-1.cost")){
                player.sendMessage(ChatColor.RED +"You do not have the funds to create a town: "+formatted);
                return;
            }
            try {
                this.configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String rankName= baseConfig.getString("Town-ranks.Rank-1.rank-name");
            int maxRes= baseConfig.getInt("Town-ranks.Rank-1.max-residents");
            int tax = baseConfig.getInt("Town-ranks.Rank-1.tax");
            List<String> list = new ArrayList<>();
            list.add(this.player.getUniqueId().toString());
            List<String> list2 = new ArrayList<>();
            list2.add("N/A");
            townConfig.set("name",this.town);
            townConfig.set("icon","CAMPFIRE");
            townConfig.set("rank",1);
            townConfig.set("rank-name",rankName);
            townConfig.set("max-residents",maxRes);
            townConfig.set("bank-balance",0);
            townConfig.set("tax",tax);
            townConfig.set("resident-tax-style","flat");
            townConfig.set("resident-tax",0);
            townConfig.set("mayor",this.player.getUniqueId().toString());
            townConfig.set("deputies",list2);
            townConfig.set("resident-amount",1);
            townConfig.set("residents",list);
            saveData();
            pm.setTown(this.town);
            pm.setTownRank("Mayor");
            econ.withdrawPlayer(player,baseConfig.getDouble("Town-ranks.Rank-1.cost"));
            player.sendMessage(ChatColor.DARK_GREEN+"Successfully withdrew $"+formatted+" to create "+this.town);
            Bukkit.broadcastMessage(ChatColor.GREEN+this.town+" is now an official "+rankName);

        }
        else {
            player.sendMessage(ChatColor.RED + this.town + " already exists");
        }
    }

    public void promoteTown(){
        if(!this.configFile.exists()){
            player.sendMessage(ChatColor.RED+"That town does not exist");
            return;
        }
        File baseFile = new File(this.plugin.getDataFolder(),"config.yml");
        FileConfiguration baseConfig = YamlConfiguration.loadConfiguration(baseFile);
        List<String> allranks = new ArrayList<>();
        for (String rank: baseConfig.getConfigurationSection("Town-ranks").getKeys(false)){
            allranks.add(rank);
        }
        int maxRank = Integer.parseInt(allranks.get(allranks.size()-1).substring(allranks.get(allranks.size()-1).length()-1));
        if(this.getRank()==maxRank){
            player.sendMessage(ChatColor.RED+"This town is already Max rank");
            return;
        }
        int newRank=this.getRank()+1;
        if(baseConfig.getDouble("Town-ranks.Rank-"+newRank+".cost")>this.getBalance()){
            player.sendMessage(ChatColor.RED+this.getName()+" does not have the required funds in their bank to rank up: " +
                    "$"+moneyformat.format(baseConfig.getDouble("Town-ranks.Rank-"+newRank+".cost")));
            return;
        }
        double newBalance = this.getBalance()-baseConfig.getDouble("Town-ranks.Rank-"+newRank+".cost");
        townConfig.set("bank-balance",newBalance);
        townConfig.set("rank",newRank);
        townConfig.set("rank-name",baseConfig.getString("Town-ranks.Rank-"+newRank+".rank-name"));
        townConfig.set("max-residents",baseConfig.getInt("Town-ranks.Rank-"+newRank+".max-residents"));
        townConfig.set("tax",baseConfig.getInt("Town-ranks.Rank-"+newRank+".tax"));
        saveData();
        Bukkit.broadcastMessage(ChatColor.GREEN+this.town+" is now a "+this.getRankName()+" (Rank "+newRank+")");

    }

    public void deleteTown(){
        if(!this.configFile.exists()){
            player.sendMessage(ChatColor.RED+"That town does not exist");
            return;
        }
        List<String> residentList = this.getResidents();
        for (String uuid: residentList){
            File playerFile = new File(this.plugin.getDataFolder() + File.separator + "PlayerData", uuid+".yml");
            FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
            playerConfig.set("town","N/A");
            playerConfig.set("town-rank","N/A");
            try {
                playerConfig.save(playerFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE,"Could not save file to "+playerFile, e);
            }
        }
        Bukkit.broadcastMessage(ChatColor.DARK_RED+this.getName()+" has fallen into ruins and is no longer an official town");
        configFile.delete();

    }

    public void deposit(double amount){
        econ.withdrawPlayer(player,amount);
        townConfig.set("bank-balance",this.getBalance()+amount);
        player.sendMessage(ChatColor.GREEN+"Successfully deposited $"+moneyformat.format(amount)+
                " into "+this.getName()+"'s bank");
        saveData();
    }
    public void offlineDeposit(double amount){
        econ.withdrawPlayer(offlinePlayer,amount);
        townConfig.set("bank-balance",this.getBalance()+amount);
        saveData();
    }
    public void withdraw(double amount){
        econ.depositPlayer(offlinePlayer,amount);
        townConfig.set("bank-balance",this.getBalance()-amount);
        saveData();
    }
    public void saveData(){
        try {
            townConfig.save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,"Could not save file to "+this.configFile, e);
        }
    }
    public String getName(){
        return townConfig.getString("name");
    }
    public String getIcon(){
        return townConfig.getString("icon");
    }
    public int getRank(){
        return townConfig.getInt("rank");
    }
    public String getRankName(){
        return townConfig.getString("rank-name");
    }
    public int getMaxResidents(){
        return townConfig.getInt("max-residents");
    }
    public double getBalance(){
        return townConfig.getDouble("bank-balance");
    }
    public String getTaxStyle(){
        return townConfig.getString("resident-tax-style");
    }
    public void setTaxStyle(String style){
        townConfig.set("resident-tax-style",style);
        saveData();
    }
    public int getResidentTax(){
        return townConfig.getInt("resident-tax");
    }
    public void setResidentTax(double amount){
        townConfig.set("resident-tax",amount);
        saveData();
    }
    public String getMayor(){
        return townConfig.getString("mayor");
    }
    public void setMayor(String uuid){
        townConfig.set("mayor",uuid);
        saveData();
    }
    public List<String> getDeputies(){
        List<String> deputies = new ArrayList();
        for (String uuid: townConfig.getStringList("deputies")){
            deputies.add(uuid);
        }
        return deputies;
    }
    public void setDeputies(List<String> deputies){
        if(deputies.size()<1)
            deputies.add("N/A");
        if(deputies.size()>1 && deputies.contains("N/A"))
            deputies.remove("N/A");
        townConfig.set("deputies",deputies);
        saveData();
    }
    public int getResidentAmount(){
        return townConfig.getInt("resident-amount");
    }
    public void setResidentAmount(int amount){
        townConfig.set("resident-amount",amount);
        saveData();
    }
    public List<String> getResidents(){
        List<String> residents = new ArrayList();
        for (String uuid: townConfig.getStringList("residents")){
            residents.add(uuid);
        }
        return residents;
    }
    public void setResidents(List<String> residents){
        townConfig.set("residents",residents);
        saveData();
    }



}
