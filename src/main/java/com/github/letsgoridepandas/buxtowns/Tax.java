package com.github.letsgoridepandas.buxtowns;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;


import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
@SuppressWarnings("all")
public class Tax {

    private int timePeriod; //number
    private String timeDuration; //letter
    private int checkPeriod; //number
    private String checkDuration; //letter
    File baseFile = new File(BuxTowns.getInstance().getDataFolder(),"config.yml");
    FileConfiguration baseConfig = YamlConfiguration.loadConfiguration(baseFile);
    File taxFile = new File(BuxTowns.getInstance().getDataFolder(),"taxes.yml");
    FileConfiguration taxConfig = YamlConfiguration.loadConfiguration(taxFile);
    private final boolean townTaxEnabled = baseConfig.getBoolean("enable-town-tax");
    private final boolean residentTaxEnabled = baseConfig.getBoolean("enable-resident-tax");
    private final boolean serverTaxEnabled = baseConfig.getBoolean("enable-server-tax");

    public Tax() {

    }

    public void collectTaxes(){
        if(townTaxEnabled || residentTaxEnabled) {
            Calendar currentDate = Calendar.getInstance();
            SimpleDateFormat formatter =
                    new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String dateNow = formatter.format(currentDate.getTime());

            try {
                timePeriod = Integer.parseInt(baseConfig.getString("tax-period").substring(0,baseConfig.getString("tax-period").length()-1));
            }catch (NumberFormatException e){
                BuxTowns.getInstance().getLogger().log(Level.SEVERE,"tax-period config setting not valid", e);
                Bukkit.getServer().getPluginManager().disablePlugin(BuxTowns.getInstance());
            }

            try {
                timeDuration = baseConfig.getString("tax-period").substring(baseConfig.getString("tax-period").length()-1);
            }catch (NumberFormatException e){
                BuxTowns.getInstance().getLogger().log(Level.SEVERE,"tax-period config setting not valid", e);
                Bukkit.getServer().getPluginManager().disablePlugin(BuxTowns.getInstance());
            }
            String nextTime = taxConfig.getString("next-run");

            Date d1 = null;
            try {
                d1 = formatter.parse(dateNow);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date d2= null;
            try {
                d2 = formatter.parse(nextTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long elapsed = d2.getTime() - d1.getTime();

            //start actual tax stuff
            if(elapsed<=0){
                Economy econ = BuxTowns.getEconomy();

                if(residentTaxEnabled){

                    for (File file: new File(BuxTowns.getInstance().getDataFolder()+ File.separator+"PlayerData").listFiles()){
                        OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().substring(0,file.getName().length()-4)));
                        PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),op);
                        if(!pm.getTown().equalsIgnoreCase("N/A")) {
                            TownManager tm = new TownManager(BuxTowns.getInstance(), pm.getTown(), op);
                            String style = tm.getTaxStyle();
                            double rate = tm.getResidentTax();
                            if(style.equalsIgnoreCase("flat")){
                                if(econ.has(op,rate)){
                                    tm.offlineDeposit(rate);
                                }else{
                                    double balance = econ.getBalance(op);
                                    tm.offlineDeposit(balance);
                                }
                                }
                            else if(style.equalsIgnoreCase("percent")){
                                double balance = econ.getBalance(op);
                                tm.offlineDeposit((balance*rate)/100);
                                }
                        }
                    }
                }

                if(townTaxEnabled){
                    for (File file: new File(BuxTowns.getInstance().getDataFolder()+ File.separator+"Towns").listFiles()){
                        FileConfiguration townConfig = YamlConfiguration.loadConfiguration(file);
                        double amount = townConfig.getDouble("tax");
                        if(townConfig.getDouble("bank-balance")>amount)
                            townConfig.set("bank-balance",townConfig.getDouble("bank-balance")-amount);
                        else
                            townConfig.set("bank-balance",0);

                        try {
                            townConfig.save(file);
                        } catch (IOException e) {
                            BuxTowns.getInstance().getLogger().log(Level.SEVERE,"Could not save file to "+file, e);
                        }
                    }
                }

                if(serverTaxEnabled){
                    for (File file: new File(BuxTowns.getInstance().getDataFolder()+ File.separator+"PlayerData").listFiles()){
                        OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().substring(0,file.getName().length()-4)));
                        double balance = econ.getBalance(op);
                        //if elite
                        if(balance>=baseConfig.getDouble("tax-class.high.max")){
                            econ.withdrawPlayer(op,balance*baseConfig.getDouble("tax-class.elite.percent"));
                        }
                        //if high
                        else if(balance>=baseConfig.getDouble("tax-class.mid.max") && balance<=baseConfig.getDouble("tax-class.high.max")){
                            econ.withdrawPlayer(op,balance*baseConfig.getDouble("tax-class.high.percent"));
                        }
                        //if mid
                        else if(balance>=baseConfig.getDouble("tax-class.low.max") && balance<=baseConfig.getDouble("tax-class.mid.max")){
                            econ.withdrawPlayer(op,balance*baseConfig.getDouble("tax-class.mid.percent"));
                        }
                        //if low
                        else if(balance>=baseConfig.getDouble("tax-class.poor.max") && balance<=baseConfig.getDouble("tax-class.low.max")){
                            econ.withdrawPlayer(op,balance*baseConfig.getDouble("tax-class.low.percent"));
                        }
                        //if poor
                        else if(balance<=baseConfig.getDouble("tax-class.poor.max")){
                            econ.withdrawPlayer(op,balance*baseConfig.getDouble("tax-class.poor.percent"));
                        }

                    }
                }

                //set next run-time (time it actually collects)

                Calendar nextDate = Calendar.getInstance();
                if(timeDuration.equalsIgnoreCase("d"))
                    nextDate.add(Calendar.DAY_OF_MONTH, timePeriod);
                else if(timeDuration.equalsIgnoreCase("w"))
                    nextDate.add(Calendar.DAY_OF_MONTH, timePeriod*7);
                String time = baseConfig.getString("tax-time");
                nextDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0,2)));
                nextDate.set(Calendar.MINUTE, Integer.parseInt(time.substring(3,5)));
                nextDate.set(Calendar.SECOND, Integer.parseInt(time.substring(6,7)));
                String nextRun = formatter.format(nextDate.getTime());
                taxConfig.set("next-run",nextRun);

                try {
                    taxConfig.save(taxFile);
                } catch (IOException e) {
                    BuxTowns.getInstance().getLogger().log(Level.SEVERE,"Could not save file to "+taxFile, e);
                }
                Bukkit.broadcastMessage(ChatColor.DARK_RED+"TAXES HAVE BEEN COLLECTED! They will be collected next at approximately "+nextRun);


            }

            try {
                checkPeriod = Integer.parseInt(baseConfig.getString("tax-check-delay").substring(0,baseConfig.getString("tax-period").length()-1));
            }catch (NumberFormatException e){
                BuxTowns.getInstance().getLogger().log(Level.SEVERE,"tax-check-delay config setting not valid", e);
                Bukkit.getServer().getPluginManager().disablePlugin(BuxTowns.getInstance());
            }

            try {
                checkDuration = baseConfig.getString("tax-check-delay").substring(baseConfig.getString("tax-period").length()-1);
            }catch (NumberFormatException e){
                BuxTowns.getInstance().getLogger().log(Level.SEVERE,"tax-check-delay config setting not valid", e);
                Bukkit.getServer().getPluginManager().disablePlugin(BuxTowns.getInstance());
            }

            int checkTime = checkPeriod;
            if(checkDuration.equalsIgnoreCase("h"))
                checkTime*=60;
            else if(checkDuration.equalsIgnoreCase("m"))
                checkTime*=1;

            new BukkitRunnable() {

                @Override
                public void run() {
                    // What you want to schedule goes here
                   collectTaxes();
                }

            }.runTaskLater(BuxTowns.getInstance(), 20L * 60 * checkTime);
        }
    }
}