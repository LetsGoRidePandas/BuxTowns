package com.github.letsgoridepandas.buxtowns;

import java.util.HashMap;

public class Vote {
    private static final HashMap<String,TownVote> votes = new HashMap<>();

    public static void makeVote(String uuid, TownVote tv){
        votes.put(uuid,tv);
    }

    public static boolean hasVote(String town){
        return votes.get(town) != null;
    }

    public static String setVote(String town, int vote, String uuid){
        String response = votes.get(town).vote(vote,uuid);
        votes.get(town).checkResults();
        return response;
    }

    public static String getMessage(String town){
        return votes.get(town).getMessage();
    }

    public static double getAmount(String town){
        return votes.get(town).getAmount();
    }

    public static void removeVote(String town){
        votes.remove(town);
    }

}
