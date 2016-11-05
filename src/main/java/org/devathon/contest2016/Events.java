package org.devathon.contest2016;

import java.util.*;

import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.Wood;

public class Events implements Listener {

    static final int MAX_PAGE_LENGTH = 256;

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        GivePlayerHelpBook(event.getPlayer());
    }


    public void GivePlayerHelpBook(Player player){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);

        BookMeta meta = (BookMeta)book.getItemMeta();

        meta.setAuthor("Server");
        meta.setTitle("Machine FAQ"); // < 16 chars

        ArrayList<String> bookPages = new ArrayList<>();

        bookPages.add("Welcome to the wonderful world of machines!\nTo create a machine create a sign and make the first line be \"[machine]\", and the second line will be the slug of the machine you want to create.\n\nSee the next pages for all the different machines.");

        //int currentPageLength = 0;
        String currentPage = "";
        for(Machine m : Machine.getAll()){

            currentPage += ChatColor.BOLD + "" + m.Name + " ("+m.Slug+")\n";
            currentPage += ChatColor.RESET + "\n" + m.Desc + "\n";
            currentPage += "\n" + m.Takes.toString() + " -> " + m.Makes.toString() + "*" + m.Difference;
            bookPages.add(currentPage);
        }

        //bookPages.add("This is page 2!");
        //bookPages.add("This is page 3!");

        meta.setPages(bookPages);
        book.setItemMeta(meta);
        player.getInventory().addItem(book);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();
        if(event.getLines()[0].equalsIgnoreCase(DevathonPlugin.INSTANCE.SignHead)){
            if(block.getType() == Material.WALL_SIGN){
                Sign s = (Sign)block.getState().getData();
                Block attachedBlock = block.getRelative(s.getAttachedFace());

                if(attachedBlock.getType() == DevathonPlugin.INSTANCE.MachineBlock){
                    //event.getPlayer().sendMessage("Successfully created a machine! " + block.getType().toString());

                    String machineSlug = event.getLines()[1].toLowerCase();

                    Machine machine = Machine.findSlug(machineSlug);
                    event.getPlayer().sendMessage("Created machine " + machine);

                    ArmorStand stand = event.getPlayer().getLocation().getWorld().spawn(attachedBlock.getLocation(), ArmorStand.class);
                    stand.setBasePlate(false);
                    stand.setArms(false);
                    stand.setHelmet(new ItemStack(DevathonPlugin.INSTANCE.MachineItem, 1, (short)machine.Damage));
                    stand.setGravity(false);
                    stand.setInvulnerable(false);

                    machine.save(attachedBlock.getLocation());

                } else {
                    event.getPlayer().sendMessage(attachedBlock.getType().name() + " is not a valid machine block! Please use " + DevathonPlugin.INSTANCE.MachineBlock);
                }
            } else{
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Machine sign must be placed on side of block! ");
            }
        }
        System.out.println(event.getLines()[0]);
    }

    @EventHandler
    public void onBlockInteract (PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
            Machine machine = Machine.get(event.getClickedBlock().getLocation());
            if(machine != null){
                Player player = event.getPlayer();

                ItemStack processed = machine.Use(player.getInventory().getItemInMainHand());
                if(processed != null) {
                    player.getInventory().addItem(processed);
                    player.getInventory().remove(player.getInventory().getItemInMainHand());
                    player.sendMessage("Used machine!");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event){
        Block block = event.getBlock();
        if(Machine.has((block.getLocation()))){
            Machine.remove(block.getLocation());
            event.getPlayer().sendMessage("Destroyed machine!");
        }

    }


}
