package com.github.letsgoridepandas.buxtowns;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class TownVote {
    private String town;
    private String message;
    private double amount;
    private ArrayList<String> onlineResidents;
    private double requiredVotes;
    private double currentVotes;
    private String mayor;
    private ArrayList<String> alreadyVoted = new ArrayList<>();


    public TownVote(String town, String mayor, String message, double amount, ArrayList<String> onlineResidents,
                    double requiredVotes, double currentVotes){
        this.town=town;
        this.mayor = mayor;
        this.message = message;
        this.amount = amount;
        this.onlineResidents = onlineResidents;
        this.requiredVotes = requiredVotes;
        this.currentVotes = currentVotes;


    }

    public String getTown() {
        return town;
    }

    public String getMessage() {
        return message;
    }

    public double getAmount() {
        return amount;
    }

    public ArrayList<String> getOnlineResidents() {
        return onlineResidents;
    }

    public double getRequiredVotes() {
        return requiredVotes;
    }

    public double getCurrentVotes() {
        return currentVotes;
    }

    public ArrayList<String> getAlreadyVoted() {
        return alreadyVoted;
    }

    public String vote(int vote, String uuid){
        if(!getAlreadyVoted().contains(uuid)) {
            currentVotes += vote;
            alreadyVoted.add(uuid);
            if(vote == 0)
            return ChatColor.GREEN+"You have voted "+ChatColor.DARK_GREEN+"NO"+ChatColor.GREEN+" to allow the town bank withdrawal " +
                    "of "+ChatColor.DARK_GREEN+getAmount()+ChatColor.GREEN+" for "+ChatColor.DARK_GREEN+getMessage();
            else
                return ChatColor.GREEN+"You have voted "+ChatColor.DARK_GREEN+"YES"+ChatColor.GREEN+" to allow the town bank withdrawal " +
                        "of "+ChatColor.DARK_GREEN+getAmount()+ChatColor.GREEN+" for "+ChatColor.DARK_GREEN+getMessage();
        }
        else
            return ChatColor.RED+"You have already voted for this";
    }
    public boolean checkResults(){
        if(getCurrentVotes()>=getRequiredVotes()) {
            TownManager tm = new TownManager(BuxTowns.getInstance(),getTown(),Bukkit.getOfflinePlayer(UUID.fromString(getMayor())));
            tm.withdraw(getAmount());
            for(String uuid: getOnlineResidents()){
                Player res = Bukkit.getPlayer(UUID.fromString(uuid));
                if(res instanceof Player){
                    res.sendMessage(ChatColor.GREEN+"The vote for the withdrawal of "
                            +ChatColor.DARK_GREEN+getAmount()+ChatColor.GREEN+" for "
                            +ChatColor.DARK_GREEN+getMessage()+ChatColor.GREEN+" has been "
                            +ChatColor.DARK_GREEN+"APPROVED");
                }
            }
            deleteVote();
            return true;
        }
        else return false;
    }

    public String getMayor() {
        return mayor;
    }

    public void createVote(){
        Vote.makeVote(getTown(),this);
    }

    public void deleteVote(){
        Vote.removeVote(getTown());
    }

}

