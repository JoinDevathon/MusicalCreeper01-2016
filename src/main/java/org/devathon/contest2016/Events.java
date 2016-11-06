package org.devathon.contest2016;

import net.minecraft.server.v1_10_R1.Item;
import net.minecraft.server.v1_10_R1.NBTTagByte;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.material.Sign;

import java.util.ArrayList;
import java.util.Dictionary;

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

        bookPages.add("Welcome to the wonderful world of machines!\nTo create a machine, place a sign and make the first line be \"[machine]\", and the second line will be the slug of the machine you want to create.\n\nSee the next pages for all the different machines.");

        String currentPage = "";
        for(Machine m : Machine.getAll()){
            currentPage += ChatColor.BOLD + "" + m.Name + " ("+m.Slug+")\n";
            currentPage += ChatColor.RESET + "\n" + m.Desc + "\n";
            currentPage += ChatColor.RESET + "\nSpeed: " + m.Time + "s \n";
            currentPage += "\n " + m.Takes.toString() + " -> " + m.Makes.toString() + "*" + m.Difference;
            if(m.SneakMakes != null)
                currentPage += "\n^" + m.Takes.toString() + " -> " + m.SneakMakes.toString() + "*" + m.Difference;
            bookPages.add(currentPage);
            currentPage = "";
        }

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

                    if(DevathonPlugin.INSTANCE.UseModels) {
                        Location armorStandLoc = attachedBlock.getLocation();

                        armorStandLoc.setX(armorStandLoc.getX() + 0.5f);
                        armorStandLoc.setY(armorStandLoc.getY() - 1.2f);
                        armorStandLoc.setZ(armorStandLoc.getZ() + 0.5f);

                        float yRot = 0;

                        if(s.getAttachedFace() == BlockFace.NORTH){
                                yRot = -180.0f;
                        }
                        if(s.getAttachedFace() == BlockFace.EAST){
                            yRot = -90.0f;
                        }
                        if(s.getAttachedFace() == BlockFace.SOUTH){
                            yRot = 0f;
                        }
                        if(s.getAttachedFace() == BlockFace.WEST){
                            yRot = 90f;
                        }

                        armorStandLoc.setYaw(yRot);

                        ArmorStand stand = event.getPlayer().getLocation().getWorld().spawn(armorStandLoc, ArmorStand.class);
                        stand.setBasePlate(false);
                        stand.setArms(false);
                        ItemStack stack = new ItemStack(DevathonPlugin.INSTANCE.MachineItem, 1, (short) machine.Damage);

                        /*net.minecraft.server.v1_10_R1.ItemStack cStack = CraftItemStack.asNMSCopy(stack);
                        NBTTagCompound tag = cStack.hasTag() ? cStack.getTag() : new NBTTagCompound();
                        tag.set("Unbreakable", new NBTTagByte((byte)1));
                        cStack.setTag(tag);
                        stack = CraftItemStack.asBukkitCopy(cStack);*/

                        stand.setHelmet(stack);
                        stand.setGravity(false);
                        stand.setInvulnerable(false);
                        stand.setCustomName("Machine Armor Stand");
                        stand.setVisible(false);

                        event.getPlayer().getWorld().getBlockAt(attachedBlock.getLocation()).setType(Material.AIR);
                    }

                    machine.save(attachedBlock.getLocation());

                } else {
                    event.getPlayer().sendMessage(attachedBlock.getType().name() + " is not a valid machine block! Please use " + DevathonPlugin.INSTANCE.MachineBlock);
                }
            } else{
                event.getPlayer().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Machine sign must be placed on side of block! ");
            }
        }
    }

    @EventHandler
    public void onInteract (PlayerInteractEvent event) {
        if(!DevathonPlugin.INSTANCE.UseModels) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

                Machine machine = Machine.get(event.getClickedBlock().getLocation());
                if (machine != null) {
                    Player player = event.getPlayer();
                    UseMachine(event.getClickedBlock().getLocation(), player);


                    /*boolean processed = machine.Use(player.getInventory().getItemInMainHand(), player);
                    if (processed) {
                        //player.getInventory().addItem(processed);
                        player.getInventory().remove(player.getInventory().getItemInMainHand());
                        player.sendMessage("Used machine!");
                        event.setCancelled(true);
                    }*/
                }
            }
        }

    }

    @EventHandler
    public void onArmorStandChange(PlayerArmorStandManipulateEvent event){
        ArmorStand stand = event.getRightClicked();
        if(stand.getCustomName().equalsIgnoreCase("Machine Armor Stand")){

            Player player = event.getPlayer();

            Location pos = stand.getLocation();
            pos.setX(pos.getX() - 0.5f);
            pos.setY(pos.getY() + 1.2f);
            pos.setZ(pos.getZ() - 0.5f);

            UseMachine(pos, player);
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onInteractWithEntity (PlayerInteractAtEntityEvent event){
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if(entity.getType() == EntityType.ARMOR_STAND){
            ArmorStand stand = (ArmorStand)entity;
            if(stand.getCustomName().equalsIgnoreCase("Machine Armor Stand")){
                Location pos = stand.getLocation();
                pos.setX(pos.getX() - 0.5f);
                pos.setY(pos.getY() + 1.2f);
                pos.setZ(pos.getZ() - 0.5f);

                UseMachine(pos, player);
                event.setCancelled(true);
            }
        }
    }

    public void UseMachine (Location pos, Player player){
        Machine machine = Machine.get(pos);
        if(machine != null){
            boolean processed = machine.Use(pos, player.getInventory().getItemInMainHand(), player);
            if(processed) {
                //player.getInventory().addItem(processed);
                player.getInventory().remove(player.getInventory().getItemInMainHand());
                player.sendMessage("Used machine!");
                //player.playSound(player.getLocation(), "machine", 1F, 5F);
                player.getWorld().playSound(player.getLocation(), "machine", 1F, 5F);
            }
        }
    }

    @EventHandler
    public void onBlockBreak (BlockBreakEvent event) {
        if (DevathonPlugin.INSTANCE.UseModels) {
            Block block = event.getBlock();
            ProcessDestroy(block.getLocation(), event.getPlayer());

        }
    }

    @EventHandler
    public void onEntityDamaged(EntityDamageByEntityEvent event) {
        if((event.getEntity() instanceof LivingEntity)){
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            if(livingEntity.getType().equals(EntityType.ARMOR_STAND)){
                if(event.getDamager() instanceof Player){
                    Location pos = livingEntity.getLocation();

                    pos.setX(pos.getX() - 0.5f);
                    pos.setY(pos.getY() + 1.2f);
                    pos.setZ(pos.getZ() - 0.5f);


                    ProcessDestroy(pos, (Player)event.getDamager());
                    livingEntity.remove();
                }
            }
        }
    }

    public void ProcessDestroy(Location loc, Player player){
        if (Machine.has((loc))) {
            Machine.remove(loc);
            player.sendMessage("Destroyed machine!");
            ItemStack stack1 = new ItemStack(Material.IRON_INGOT, 3);
            ItemStack stack2 = new ItemStack(Material.STICK, 8);
            ItemStack stack3 = new ItemStack(Material.WOOD, 6);

            player.getWorld().dropItem(loc, stack1);
            player.getWorld().dropItem(loc, stack2);
            player.getWorld().dropItem(loc, stack3);
        }
    }



}
