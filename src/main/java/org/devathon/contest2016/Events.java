package org.devathon.contest2016;

import java.util.*;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Events implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        GivePlayerHelpBook(event.getPlayer());
    }

    public void GivePlayerHelpBook(Player player){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);

        BookMeta meta = (BookMeta)book.getItemMeta();

        meta.setAuthor("Server");
        meta.setTitle("Machine FAQ");

        ArrayList<String> bookPages = new ArrayList<>();

        bookPages.add("This is page 1!");
        bookPages.add("This is page 2!");
        bookPages.add("This is page 3!");

        meta.setPages(bookPages);
        book.setItemMeta(meta);
        player.getInventory().addItem(book);
    }

}
