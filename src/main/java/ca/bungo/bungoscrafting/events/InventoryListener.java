package ca.bungo.bungoscrafting.events;

import ca.bungo.bungoscrafting.types.LargeCrafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getInventory().getHolder() instanceof LargeCrafter crafter) {
            crafter.handleInventoryClick(e);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if(e.getInventory().getHolder() instanceof LargeCrafter crafter) {
            crafter.handleInventoryClose(e);
        }
    }

}
