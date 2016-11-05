package org.devathon.contest2016;

import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_10_R1.Position;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.Interpreter;
import org.bukkit.inventory.ItemStack;

public class Machine {

    public String Name;
    public String Slug;
    public Material Takes;
    public Material Makes;
   // public String Operation;
    public int Difference;

    public Machine (ConfigurationSection section){
        Name = section.getString("name", "Default Name");
        Slug = section.getString("slug", "slug");
        Takes = Material.getMaterial(section.getString("takes").toUpperCase());
        Makes = Material.getMaterial(section.getString("makes").toUpperCase());
      //  Operation = section.getString("operation", "");
        Difference = section.getInt("diff", 8);

    }

    public boolean CanUse (Material mat){
        return mat == Takes;
    }

    public ItemStack Use (ItemStack input){
        if(input.getType() == Takes){

            /*switch(Operation){
                case "*":
                case "times":
                case "multiplication":
                    amount = input.getAmount() * Difference;
                    break;
                case "+":
                case "add":
                case "addition":
                    amount = input.getAmount() + Difference;
                    break;
                default:
                    amount = Difference;
            }*/

            ItemStack out = new ItemStack(Makes, input.getAmount()*Difference);
            input.setAmount(0);
            return out;

        }

        return null;
    }

    public String toString(){
        return "(" + this.Name + ", " + this.Slug + ", " + this.Takes + ", " + this.Makes + ", " + this.Difference + ")";

    }

    public static Machine findSlug(String slug) {
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("machines");

        if(config != null){
            for(String key : config.getKeys(false)){
                ConfigurationSection machineSection = config.getConfigurationSection(key);
                if(machineSection != null){
                    if(machineSection.getString("slug").equalsIgnoreCase(slug))
                        return new Machine(machineSection);

                }
            }
        }else {
            System.out.println(ChatColor.RED + "The 'machines' section does not exist in the config!");

        }

        return null;
    }

    /*public static Machine find(String id) {
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("machines");

        if(config != null){
            //ConfigurationSection machineConfig
        }else {
            System.out.println(ChatColor.RED + "The 'machines' section does not exist in the config!");

        }

        return null;
    }*/

    public void save (Location pos){
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config == null){
            DevathonPlugin.INSTANCE.getConfig().createSection("world");
            config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        }

        String key = pos.getWorld() + ":" + pos.getBlockX() + ":" + pos.getBlockY() + ":" + pos.getBlockZ();
        ConfigurationSection machine = config.createSection(key);

        machine.set("type", this.Slug);

        DevathonPlugin.INSTANCE.saveConfig();
    }

}