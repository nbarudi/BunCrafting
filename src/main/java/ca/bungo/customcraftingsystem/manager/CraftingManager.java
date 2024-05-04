package ca.bungo.customcraftingsystem.manager;

import ca.bungo.customcraftingsystem.CustomCraftingSystem;
import ca.bungo.customcraftingsystem.types.CustomCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftingManager {

    private List<CustomCraft> activeCrafts;

    public CraftingManager(){
        activeCrafts = new ArrayList<>();

        watcher();
    }

    public CustomCraft createCustomCraft(Location craftLocation, List<ItemStack> reagents, List<ItemStack> results){
        CustomCraft craft = new CustomCraft(craftLocation, reagents, results);
        activeCrafts.add(craft);
        return craft;
    }

    public CustomCraft createCustomCraft(Location craftLocation, List<ItemStack> reagents, List<ItemStack> results, int timeToCraft){
        CustomCraft craft = new CustomCraft(craftLocation, reagents, results, timeToCraft);
        activeCrafts.add(craft);
        return craft;
    }

    public void cleanup(){
        for(CustomCraft craft : activeCrafts){
            craft.cleanup();
        }
    }

    private void watcher(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(CustomCraftingSystem.getInstance(), () -> {
            List<CustomCraft> toRemove = new ArrayList<>();
            for(CustomCraft craft : activeCrafts){
                if(craft.getRemainingTime() <= 0){
                    toRemove.add(craft);
                }
            }
            activeCrafts.removeAll(toRemove);
        }, 50, 5);
    }



}
