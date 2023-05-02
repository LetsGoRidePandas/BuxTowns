package com.github.letsgoridepandas.buxtowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YourTownMenu {

    public YourTownMenu(Player p){
        PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),p);
        TownManager tm = new TownManager(BuxTowns.getInstance(),pm.getTown(),p);
        Inventory yourTownMenu = Bukkit.createInventory(p, 54, ChatColor.DARK_GREEN + pm.getTown());
        DecimalFormat moneyformat = new DecimalFormat("#,###,##0.00");

        ItemStack rank = new ItemStack(Material.BEACON, 1);
        ItemMeta rankMeta = rank.getItemMeta();
        rankMeta.setDisplayName("Town Rank");
        ArrayList<String> rankLore = new ArrayList<>();
        rankLore.add(""+tm.getRank());
        rankMeta.setLore(rankLore);
        rank.setItemMeta(rankMeta);
        yourTownMenu.setItem(1,rank);

        ItemStack rankName = new ItemStack(Material.BEEHIVE, 1);
        ItemMeta rankNameMeta = rankName.getItemMeta();
        rankNameMeta.setDisplayName("Town Rank Name");
        ArrayList<String> rankNameLore = new ArrayList<>();
        rankNameLore.add(""+tm.getRankName());
        rankNameMeta.setLore(rankNameLore);
        rankName.setItemMeta(rankNameMeta);
        yourTownMenu.setItem(3,rankName);

        ItemStack residents = new ItemStack(Material.VILLAGER_SPAWN_EGG, 1);
        ItemMeta residentsMeta = residents.getItemMeta();
        residentsMeta.setDisplayName("Residents");
        ArrayList<String> residentsLore = new ArrayList<>();
        residentsLore.add(""+tm.getResidentAmount()+"/"+tm.getMaxResidents());
        residentsMeta.setLore(residentsLore);
        residents.setItemMeta(residentsMeta);
        yourTownMenu.setItem(5,residents);

        ItemStack bank = new ItemStack(Material.EMERALD, 1);
        ItemMeta bankMeta = bank.getItemMeta();
        bankMeta.setDisplayName("Town Balance");
        ArrayList<String> bankLore = new ArrayList<>();
        bankLore.add("$"+moneyformat.format(tm.getBalance()));
        bankMeta.setLore(bankLore);
        bank.setItemMeta(bankMeta);
        yourTownMenu.setItem(7,bank);

        String mayor = tm.getMayor();
        List<String> deputyList = tm.getDeputies();
        List<String> residentList = tm.getResidents();
        List<String> skipList = new ArrayList<>();
        skipList.add(mayor);
        int loc[] = new int[]{11,12,13,4,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,
        37,38,39,40,41,42,43,46,47,48,49,50,51,52};
        int i = 0;
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(mayor)));
        headMeta.setDisplayName(Bukkit.getOfflinePlayer(UUID.fromString(mayor)).getName());
        ArrayList<String> headLore = new ArrayList<>();
        headLore.add("Mayor");
        headMeta.setLore(headLore);
        head.setItemMeta(headMeta);
        yourTownMenu.setItem(10,head);
        headLore.clear();
        for (String uuid: deputyList){
            if(!uuid.equalsIgnoreCase("N/A")) {
                head = new ItemStack(Material.PLAYER_HEAD);
                headMeta = (SkullMeta) head.getItemMeta();
                headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                headMeta.setDisplayName(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                headLore.add("Deputy");
                headMeta.setLore(headLore);
                head.setItemMeta(headMeta);
                yourTownMenu.setItem(loc[i], head);
                i++;
                headLore.clear();
                skipList.add(uuid);
            }
        }

        for (String uuid: residentList){
            if(!skipList.contains(uuid)) {
                head = new ItemStack(Material.PLAYER_HEAD);
                headMeta = (SkullMeta) head.getItemMeta();
                headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
                headMeta.setDisplayName(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
                head.setItemMeta(headMeta);
                yourTownMenu.setItem(loc[i], head);
                i++;
            }
        }




        p.openInventory(yourTownMenu);
    }
}
