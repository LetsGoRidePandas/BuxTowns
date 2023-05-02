package com.github.letsgoridepandas.buxtowns;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class CustomConfig {

    private BuxTowns plugin = BuxTowns.getInstance();

    //Buxtowns\Config.yml
    private File configFile =  new File(plugin.getDataFolder(), "config.yml");
    private FileConfiguration dataconfig;

    //Buxtowns\Taxes.yml
    private File taxFile =  new File(plugin.getDataFolder(), "taxes.yml");
    private FileConfiguration taxconfig;

    //Buxtowns\Towns\
    private File townFolder =  new File(plugin.getDataFolder(), "Towns");

    //Buxtowns\PlayerData\
    private File playerFolder =  new File(plugin.getDataFolder(), "PlayerData");


    public CustomConfig(){

    }

    public void loadData(){
        if(!configFile.exists()){
            plugin.saveResource("config.yml",true);
        }
        if(!taxFile.exists()){
            try {
                this.taxFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            taxconfig = YamlConfiguration.loadConfiguration(taxFile);
            taxconfig.set("next-run","2000/01/01 00:00:00");
            try {
                taxconfig.save(taxFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE,"Could not save file to "+taxFile, e);
            }
        }
        if(!townFolder.exists()){
            townFolder.mkdirs();
        }
        if(!playerFolder.exists()){
            playerFolder.mkdirs();
        }
        dataconfig = YamlConfiguration.loadConfiguration(configFile);
    }
}
