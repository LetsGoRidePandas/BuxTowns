package com.github.letsgoridepandas.buxtowns;

import com.github.letsgoridepandas.buxtowns.commands.*;
import com.github.letsgoridepandas.buxtowns.events.GUIEvent;
import com.github.letsgoridepandas.buxtowns.events.LoginEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

import java.util.logging.Logger;

public final class BuxTowns extends JavaPlugin implements Listener {
    private static BuxTowns plugin;
    private static Economy econ = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        Logger log = getLogger();
        log.info("BuxTowns is enabled");
        CustomConfig config = new CustomConfig();
        config.loadData();
        if (!setupEconomy() ) {
            log.info("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerCommands();
        registerEvents();
        Tax tax = new Tax();
        tax.collectTaxes();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("BuxTowns is disabled");
    }

    public static BuxTowns getInstance(){
        return plugin;
    }
    public static Economy getEconomy(){
        return econ;
    }

    @SuppressWarnings("all")
    private void registerCommands(){
        getServer().getPluginCommand("town").setExecutor(new TownCommand(this));
        getServer().getPluginCommand("towncreate").setExecutor(new TownCreateCommand(this));
        getServer().getPluginCommand("townpromote").setExecutor(new TownPromoteCommand());
        getServer().getPluginCommand("towndelete").setExecutor(new TownDeleteCommand());
        getServer().getPluginCommand("towndeposit").setExecutor(new TownDepositCommand());
        getServer().getPluginCommand("towninvite").setExecutor(new TownInviteCommand());
        getServer().getPluginCommand("townleave").setExecutor(new TownLeaveCommand());
        getServer().getPluginCommand("mayor").setExecutor(new MayorCommand());
        getServer().getPluginCommand("deputy").setExecutor(new DeputyCommand());
        getServer().getPluginCommand("townvote").setExecutor(new TownVoteCommand());
    }

    private void registerEvents(){
        getServer().getPluginManager().registerEvents(new GUIEvent(), this);
        getServer().getPluginManager().registerEvents(new LoginEvent(), this);
    }
    @SuppressWarnings("all")
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

}
