package ca.bungo.bungoscrafting.manager;

import ca.bungo.bungoscrafting.CustomCraftingSystem;
import ca.bungo.bungoscrafting.types.CustomCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Manager service for custom crafting animations
 * When creating a crafting animation it should be defined inside of this service.
 * This service will automatically cleanup custom crafts when completed or if the server is shut down
 * */
public class CraftingManager {

    private final List<CustomCraft> activeCrafts;

    public CraftingManager(){
        activeCrafts = new ArrayList<>();

        watcher();
    }

    /**
     * Create a new custom craft at the defined location
     * @param craftLocation Location where the craft was started (Will be where the resulting items spawn)
     * @param reagents Items used for the custom craft, these will be what float around
     * @param results Items that will return
     * @return CustomCraft that has been created.
     * @see CustomCraft
     * */
    public CustomCraft createCustomCraft(Location craftLocation, List<ItemStack> reagents, List<ItemStack> results){
        CustomCraft craft = new CustomCraft(craftLocation, reagents, results);
        activeCrafts.add(craft);
        return craft;
    }

    /**
     * Create a new custom craft at the defined location and state how long it will take for the recipe to be completed
     * @param craftLocation Location where the craft was started (Will be where the resulting items spawn)
     * @param reagents Items used for the custom craft, these will be what float around
     * @param results Items that will return
     * @param timeToCraft Time in ticks to craft the recipe
     * @return CustomCraft that has been created.
     * @see CustomCraft
     * */
    public CustomCraft createCustomCraft(Location craftLocation, List<ItemStack> reagents, List<ItemStack> results, int timeToCraft){
        CustomCraft craft = new CustomCraft(craftLocation, reagents, results, timeToCraft);
        activeCrafts.add(craft);
        return craft;
    }

    /**
     * Clear all the current custom crafts
     * */
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
