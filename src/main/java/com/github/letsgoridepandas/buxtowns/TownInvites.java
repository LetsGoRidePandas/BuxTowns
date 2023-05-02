package com.github.letsgoridepandas.buxtowns;

import java.util.HashMap;

public class TownInvites {

        private static HashMap<String, String> invites = new HashMap<String, String>();

        public static boolean hasInvite(String uuid){
            if(invites.get(uuid)!=null)
                return true;
            else
                return false;
        }
        public static String getTown(String uuid){
            return invites.get(uuid);
        }

        public static void setInvites(String uuid, String town){
            invites.put(uuid,town);
        }

        public static void removeInvite(String uuid){
            invites.remove(uuid);
        }
}
