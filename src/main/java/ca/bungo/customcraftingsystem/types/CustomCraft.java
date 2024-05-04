package ca.bungo.customcraftingsystem.types;

import ca.bungo.customcraftingsystem.CustomCraftingSystem;
import ca.bungo.customcraftingsystem.util.PositionUtility;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class CustomCraft {

    private static final float CRAFT_RADIUS = 8f;

    private final List<ItemStack> reagents;
    private final List<ItemStack> results;
    private final Location craftLocation;
    private float timeToCraft = 200;

    private BukkitRunnable runnable;

    private List<ItemDisplay> displays;

    private boolean isClean = false;

    public CustomCraft(Location craftLocation, List<ItemStack> reagents, List<ItemStack> results){
        this.reagents = reagents;
        this.results = results;
        this.craftLocation = craftLocation;
    }

    public CustomCraft(Location craftLocation, List<ItemStack> reagents, List<ItemStack> results, int timeToCraft){
        this.reagents = reagents;
        this.results = results;
        this.timeToCraft = timeToCraft;
        this.craftLocation = craftLocation;
    }

    public float getRemainingTime() { return timeToCraft; }

    public void startCraft(){
        startCustomCraft();
    }

    private void startCustomCraft(){
        float baseOffset = CRAFT_RADIUS/timeToCraft; //I think 8 is a fair distance
        displays = new ArrayList<>();

        runnable = new BukkitRunnable() {

            float offset = 0;
            float posOffset = 0f;

            @Override
            public void run() {
                List<Location> locations = PositionUtility.teleportInCircle(craftLocation, CRAFT_RADIUS - offset, reagents.size(), posOffset);

                if(displays.isEmpty()){
                    for(int i = 0; i < locations.size(); i++) {
                        int index = i;
                        Location location = locations.get(index);
                        Entity ent = location.getWorld().spawnEntity(location, EntityType.ITEM_DISPLAY, CreatureSpawnEvent.SpawnReason.CUSTOM, (entity) -> {
                            ((ItemDisplay)entity).setItemStack(reagents.get(index));
                            ((ItemDisplay)entity).setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIRSTPERSON_LEFTHAND);
                        });
                        displays.add((ItemDisplay) ent);
                    }
                }else{
                    for(int i = 0; i < locations.size(); i++) {
                        Location location = locations.get(i);
                        displays.get(i).teleport(location);
                        location.getWorld().spawnParticle(Particle.CHERRY_LEAVES, location, 1);
                    }
                    craftLocation.getWorld().playSound(craftLocation, Sound.BLOCK_AMETHYST_BLOCK_STEP, 0.5f, 1.5f);
                }

                offset += baseOffset;
                posOffset += 2f;
                timeToCraft--;
                if(offset >= CRAFT_RADIUS){
                    this.cancel();
                    for(ItemDisplay display : displays){
                        display.remove();
                    }

                    for(ItemStack result : results){
                        craftLocation.getWorld().dropItem(craftLocation, result);
                        craftLocation.getWorld().spawnParticle(Particle.WHITE_SMOKE, craftLocation, 70, 0.5,0.5,0.5);
                        craftLocation.getWorld().playSound(craftLocation, Sound.ENTITY_CHICKEN_EGG, 1f, 1f);
                    }
                    for(ItemStack reagent : reagents) {
                        if(reagent.getType().name().toLowerCase().contains("bucket") && !reagent.getType().equals(Material.BUCKET)){
                            craftLocation.getWorld().dropItem(craftLocation, new ItemStack(Material.BUCKET));
                        }
                    }
                    craftLocation.getWorld().playSound(craftLocation, Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
                    isClean = true;
                }

            }
        };

        runnable.runTaskTimer(CustomCraftingSystem.getInstance(), 0, 1);
    }

    public void cleanup(){
        if(isClean || displays == null) return;
        for(ItemDisplay display : displays){
            runnable.cancel();
            display.remove();
        }

        for(ItemStack item : reagents) {
            craftLocation.getWorld().dropItem(craftLocation, item);
        }
    }

}
