package org.devathon.contest2016;

import org.bukkit.plugin.java.JavaPlugin;

public class DevathonPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // put your enable code here
        getServer().getPluginManager().registerEvents(new Events(), this);​
    }

    @Override
    public void onDisable() {
        // put your disable code here
    }


}

