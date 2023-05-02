package com.github.letsgoridepandas.buxtowns.events;

import com.github.letsgoridepandas.buxtowns.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GUIEvent implements Listener {

    @EventHandler
    public void clickEvent(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),p);
        String town = pm.getTown();
        if(e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Towns")){
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }
            else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Town List")){
                p.closeInventory();
                TownListMenu list = new TownListMenu(p);
            }
            else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(town)){
                p.closeInventory();
                YourTownMenu yourTown = new YourTownMenu(p);
            }
            else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Top Towns")){
                p.closeInventory();
                TownTopMenu list = new TownTopMenu(p);
            }
        }
        if(e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Town List")){
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }
            else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Next Page")){
                //p.closeInventory();
                p.sendMessage("PlaceHolder");
            }
        }
        if(e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + town)){
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }
            else if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("Town Balance")){
                //p.closeInventory();
                p.sendMessage("PlaceHolder");
            }
        }
        if(e.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GREEN + "Top Towns")){
            e.setCancelled(true);
            if(e.getCurrentItem() == null){
                return;
            }

        }
    }
}
