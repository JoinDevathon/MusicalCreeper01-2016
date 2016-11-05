package org.devathon.contest2016;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;



public class DevathonPlugin extends JavaPlugin {

    public static DevathonPlugin INSTANCE;

    public String SignHead = "[machine]";
    public Material MachineBlock = Material.IRON_BLOCK;
    public Material MachineItem = Material.IRON_BLOCK;

    @Override
    public void onEnable() {
        // put your enable code here
        INSTANCE = this;

        /*this.getConfig().addDefault("sign.head", "[machine]");
        this.getConfig().addDefault("machines.block", Material.IRON_BLOCK.toString());
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();*/

        this.saveDefaultConfig();

        SignHead = this.getConfig().getString("sign.head");
        MachineBlock = Material.valueOf(this.getConfig().getString("machine.block").toUpperCase());
        MachineItem = Material.valueOf(this.getConfig().getString("machine.item").toUpperCase());
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        // put your disable code here
        DevathonPlugin.INSTANCE.saveConfig();
    }


}

