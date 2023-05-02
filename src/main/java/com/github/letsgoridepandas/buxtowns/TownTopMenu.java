package com.github.letsgoridepandas.buxtowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class TownTopMenu{
    public TownTopMenu(Player p) {
        Inventory townTopMenu = Bukkit.createInventory(p, 18, ChatColor.DARK_GREEN + "Top Towns");
        BuxTowns plugin = BuxTowns.getInstance();
        ArrayList<ItemStack> town = new ArrayList<>();
        ArrayList<Integer> rankPoints = new ArrayList<>();
        ArrayList<String> lore = new ArrayList<>();

        int i = 0;
        for (File file: new File(plugin.getDataFolder()+ File.separator+"Towns").listFiles()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            rankPoints.add((10*config.getInt("rank"))+(1*config.getInt("resident-amount")));
            town.add(new ItemStack((Material.getMaterial(config.getString("icon")))));
            ItemMeta meta = town.get(i).getItemMeta();
            meta.setDisplayName(config.getString("name"));
            lore.add(config.getString("rank-name"));
            lore.add(config.getString("resident-amount")+" residents");
            meta.setLore(lore);
            town.get(i).setItemMeta(meta);
            lore.clear();
            i++;
        }
        int loc[] = new int[]{2,3,4,5,6,11,12,13,14,15};
        if(i>=10) {
            for (int k = 0; k < 10; k++) {
                townTopMenu.setItem(loc[k], town.get(rankPoints.indexOf(Collections.max(rankPoints))));
                rankPoints.set(rankPoints.indexOf(Collections.max(rankPoints)), 0);
            }
        }else{
            for (int k = 0; k < i; k++) {
                townTopMenu.setItem(loc[k], town.get(rankPoints.indexOf(Collections.max(rankPoints))));
                rankPoints.set(rankPoints.indexOf(Collections.max(rankPoints)), 0);
            }
        }


        p.openInventory(townTopMenu);
    }
}

