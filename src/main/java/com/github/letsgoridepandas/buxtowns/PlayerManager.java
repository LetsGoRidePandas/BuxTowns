package com.github.letsgoridepandas.buxtowns;


import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class PlayerManager {
    private BuxTowns plugin;
    private FileConfiguration playerConfig = null;
    private File configFile = null;
    private Player player;
    private OfflinePlayer offlinePlayer;
    private String playerYML;

    public PlayerManager(BuxTowns plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.playerYML = player.getUniqueId() + ".yml";
        this.configFile = new File(this.plugin.getDataFolder() + File.separator + "PlayerData", this.playerYML);
        this.playerConfig = YamlConfiguration.loadConfiguration(configFile);

    }

    public PlayerManager(BuxTowns plugin, OfflinePlayer player) {
        this.plugin = plugin;
        this.offlinePlayer = player;
        this.playerYML = player.getUniqueId() + ".yml";
        this.configFile = new File(this.plugin.getDataFolder() + File.separator + "PlayerData", this.playerYML);
        this.playerConfig = YamlConfiguration.loadConfiguration(configFile);

    }

    public void createYML() {
        if (!this.configFile.exists()) {
            try {
                this.configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            playerConfig.set("name",this.player.getName());
            playerConfig.set("town","N/A");
            playerConfig.set("town-rank","N/A");
            saveData();

        }
    }

    public void saveData(){
        try {
            playerConfig.save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE,"Could not save file to "+this.configFile, e);
        }
    }

    public String getName(){
        return this.playerConfig.getString("name");
    }

    public String getTown(){
        return this.playerConfig.getString("town");
    }
    public String getTownRank(){
        return this.playerConfig.getString("town-rank");
    }
    public void setTown(String town){
        playerConfig.set("town",town);
        saveData();

    }
    public void setTownRank(String rank){
        playerConfig.set("town-rank",rank);
        saveData();

    }
    public void updateName(){
        String oldName = this.getName();
        String newName = this.player.getName();
        if(!oldName.equalsIgnoreCase(newName)){
            playerConfig.set("name",this.player.getName());
            saveData();
        }
    }
}