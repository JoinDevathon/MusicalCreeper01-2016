package org.devathon.contest2016;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;

public class Machine {

    static HashMap<Location, Machine> machines = new HashMap<Location, Machine>();

    public String Name;
    public String Slug;
    public String Desc;
    public Material Takes;
    public Material Makes;
    public Material SneakMakes;
    public int Damage = 1;
   // public String Operation;
    public int Difference;
    public int Time = 2;

    public boolean running = false;
    public int left  = 0;
    public int total = 0;

    public Machine (ConfigurationSection section){
        Name = section.getString("name", "Default Name");
        Slug = section.getString("slug", "slug");
        Desc = section.getString("desc", "No Description.");
        Takes = Material.getMaterial(section.getString("takes").toUpperCase());
        Makes = Material.getMaterial(section.getString("makes").toUpperCase());
        SneakMakes = section.getString("sneakmakes") != null ? Material.getMaterial(section.getString("sneakmakes").toUpperCase()) : null;
      //  Operation = section.getString("operation", "");
        Difference = section.getInt("diff", 8);
        Damage = section.getInt("damage", 1);
        Time = section.getInt("time", 2);

    }

    public boolean CanUse (Material mat){
        return mat == Takes;
    }

    public boolean Use (Location pos, ItemStack input, Player player){
        if(running){
            player.sendMessage(org.bukkit.ChatColor.YELLOW + "Running... " + left + "/" + total + " " + (Time*left) +"s.." );
            return false;
        }else {
            if (input.getType() == Takes) {

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
                running = true;
                total = input.getAmount();
                left = total;

                player.sendMessage(ChatColor.GREEN + "Processing... This will take " + (Time*total) + "s ");

                Location dropPos = pos;
                dropPos.setX(dropPos.getX() + 0.5);
                dropPos.setZ(dropPos.getZ() + 0.5);

                Material mat = this.Makes;

                if (this.SneakMakes != null && player.isSneaking()) {
                    mat = this.SneakMakes;
                }

                final Material m = mat;
                final int t = total;
                for (int i = 0; i < total; ++i) {
                    final int r = i;
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            ItemStack out = new ItemStack(m, Difference);

                            player.getWorld().dropItemNaturally(dropPos, out);
                            player.getWorld().playSound(dropPos, Sound.BLOCK_PISTON_EXTEND, 1, 1);
                            player.getWorld().playEffect(dropPos, Effect.SMOKE, 1);
                            --left;
                            if (r == t - 1) {
                                running = false;
                            }
                        }

                    }.runTaskLater(DevathonPlugin.INSTANCE, 20 * (Time * i));
                }
                //ItemStack out = new ItemStack(SneakMakes, input.getAmount()*Difference);
                //out.setDurability(input.getDurability());

                //return out;
                return true;


            } else {
                player.sendMessage(ChatColor.GREEN + this.Name + " takes " + Takes + " and turns it into " + this.Difference + " " + this.Makes + "s");
                if (this.SneakMakes != null)
                    player.sendMessage(ChatColor.GREEN + " + sneaking makes " + this.Difference + " " + this.SneakMakes + "s");
            }
        }

        return false;
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
        DevathonPlugin.INSTANCE.reloadConfig();
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config == null){
            DevathonPlugin.INSTANCE.getConfig().createSection("world");
            config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        }

        ConfigurationSection machine = config.createSection(getKey(pos));

        machine.set("type", this.Slug);

        DevathonPlugin.INSTANCE.saveConfig();
    }

    public static boolean remove (Location pos){
        DevathonPlugin.INSTANCE.reloadConfig();
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config != null){
            config.set(getKey(pos), null);
            if(machines.containsKey(pos))
                machines.remove(pos);
            DevathonPlugin.INSTANCE.saveConfig();
            return true;
        }
        return false;
    }

    public static Machine get (Location pos){
        DevathonPlugin.INSTANCE.reloadConfig();
        ConfigurationSection config = DevathonPlugin.INSTANCE.getConfig().getConfigurationSection("world");
        if(config != null){
            ConfigurationSection section = config.getConfigurationSection(getKey(pos));
            if(section != null){
                if(machines.containsKey(pos)){
                    //System.out.println("got from hashmap");
                    return machines.get(pos);
                }else {
                   // System.out.println("creating");
                    String slug = section.getString("type");
                    Machine machine = Machine.findSlug(slug);
                    machines.put(pos, machine);
                    return machine;
                }
            }
        }

        return null;
    }

    public static boolean has (Location pos){
        DevathonPlugin.INSTANCE.reloadConfig();
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
        DevathonPlugin.INSTANCE.reloadConfig();

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