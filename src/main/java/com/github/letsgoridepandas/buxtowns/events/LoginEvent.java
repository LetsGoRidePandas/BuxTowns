package com.github.letsgoridepandas.buxtowns.events;

import com.github.letsgoridepandas.buxtowns.BuxTowns;
import com.github.letsgoridepandas.buxtowns.PlayerManager;
import com.github.letsgoridepandas.buxtowns.TownInvites;
import com.github.letsgoridepandas.buxtowns.Vote;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginEvent implements Listener {

    @EventHandler
    public void LoginEvent(PlayerJoinEvent e){
        Player player = e.getPlayer();
        PlayerManager pm = new PlayerManager(BuxTowns.getInstance(),player);
        pm.createYML();
        pm.updateName();
        if (Vote.hasVote(pm.getTown())){
            player.sendMessage(ChatColor.GREEN+"There is an active withdraw vote for "+pm.getTown()+". Do /townvote to see details.");
        }
        if(TownInvites.hasInvite(player.getUniqueId().toString())){
            player.sendMessage(ChatColor.GREEN+"You have an active town invite. Do /towninvite to see details.");
        }

    }
}
