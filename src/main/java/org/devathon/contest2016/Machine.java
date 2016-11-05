package org.devathon.contest2016;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

public class Machine {

    public String Name;
    public String Slug;
    public String Desc;
    public Material Takes;
    public Material Makes;
    public int Damage = 1;
   // public String Operation;
    public int Difference;

    public Machine (ConfigurationSection section){
        Name = section.getString("name", "Default Name");
        Slug = section.getString("slug", "slug");
        Desc = section.getString("desc", "No Description.");
        Takes = Material.getMaterial(section.getString("takes").toUpperCase());
        Makes = Material.getMaterial(section.getString("makes").toUpperCase());
      //  Operation = section.getString("operation", "");
        Difference = section.getInt("diff", 8);
        Damage = section.getInt("damage", 1);

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
            out.setDurability(input.getDurability());

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

    private static String getKey (Location pos){
        return pos.getWorld().getUID() + ":" + pos.getBlockX() + ":" + pos.getBlockY() + ":" + pos.getBlockZ();
    }

    public void save (Location pos){
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config == null){
            DevathonPlugin.INSTANCE.getConfig().createSection("world");
            config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        }

        ConfigurationSection machine = config.createSection(getKey(pos));

        machine.set("type", this.Slug);

        DevathonPlugin.INSTANCE.saveConfig();
    }

    public static void remove (Location pos){
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config == null){
            config.set(getKey(pos), null);
            DevathonPlugin.INSTANCE.saveConfig();
        }
    }

    public static Machine get (Location pos){
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config != null){
            ConfigurationSection section = config.getConfigurationSection(getKey(pos));
            if(section != null){
                String slug = section.getString("type");
                return Machine.findSlug(slug);
            }
        }

        return null;
    }

    public static boolean has (Location pos){
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config != null){
            ConfigurationSection section = config.getConfigurationSection(getKey(pos));
            if(section != null){
                return true;
            }
        }

        return false;

    }

    public static ArrayList<Machine> getAll (){
        ArrayList<Machine> machines = new ArrayList<>();

        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("machines");

        if(config != null){
            for(String key : config.getKeys(false)){
                ConfigurationSection machineSection = config.getConfigurationSection(key);
                if(machineSection != null){
                    machines.add(new Machine(machineSection));
                }
            }
        }else {
            System.out.println(ChatColor.RED + "The 'machines' section does not exist in the config!");

        }
        return machines;

    }

}