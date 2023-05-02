package com.github.letsgoridepandas.buxtowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class MainMenu {

    public MainMenu(Player p){

        PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),p);
        String town = pm.getTown();
        TownManager tm = new TownManager(BuxTowns.getInstance(),town,p);
        String icon = tm.getIcon();

        Inventory mainMenu = Bukkit.createInventory(p, InventoryType.HOPPER, ChatColor.DARK_GREEN + "Towns");

        ItemStack list = new ItemStack(Material.PAPER, 1);
        ItemMeta listMeta = list.getItemMeta();
        listMeta.setDisplayName("Town List");
        ArrayList<String> listLore = new ArrayList<>();
        listLore.add("Displays all official towns");
        listMeta.setLore(listLore);
        list.setItemMeta(listMeta);
        mainMenu.setItem(0, list);

        if(!town.equals("N/A")) {
            ItemStack yours = new ItemStack(Material.getMaterial(icon));
            ItemMeta yoursMeta = yours.getItemMeta();
            yoursMeta.setDisplayName(town);
            ArrayList<String> yoursLore = new ArrayList<>();
            yoursLore.add("Displays info on "+town);
            yoursMeta.setLore(yoursLore);
            yours.setItemMeta(yoursMeta);
            mainMenu.setItem(2, yours);
        }

        ItemStack top = new ItemStack(Material.GOLDEN_APPLE, 1);
        ItemMeta  topMeta = top.getItemMeta();
        topMeta.setDisplayName("Top Towns");
        ArrayList<String> topLore = new ArrayList<>();
        topLore.add("Displays the top 10 towns");
        topMeta.setLore(topLore);
        top.setItemMeta(topMeta);
        mainMenu.setItem(4, top);

        p.openInventory(mainMenu);
    }
}
