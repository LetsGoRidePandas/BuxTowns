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

public class TownListMenu {
    public TownListMenu(Player p){
        Inventory listMenu = Bukkit.createInventory(p, 54, ChatColor.DARK_GREEN + "Town List");
        BuxTowns plugin = BuxTowns.getInstance();
        ArrayList<ItemStack> town = new ArrayList<>();
        ArrayList<String> lore = new ArrayList<>();

        int i = 0;
        for (File file: new File(plugin.getDataFolder()+ File.separator+"Towns").listFiles()){
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            town.add(new ItemStack((Material.getMaterial(config.getString("icon")))));
            ItemMeta meta = town.get(i).getItemMeta();
            meta.setDisplayName(config.getString("name"));
            lore.add(config.getString("rank-name"));
            lore.add(config.getString("resident-amount")+" residents");
            meta.setLore(lore);
            town.get(i).setItemMeta(meta);
            lore.clear();
            listMenu.setItem(i,town.get(i));
            i++;
        }
        p.openInventory(listMenu);
    }

}

