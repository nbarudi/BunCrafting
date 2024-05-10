package ca.bungo.bungoscrafting.types;

import ca.bungo.bungoscrafting.CustomCraftingSystem;
import ca.bungo.bungoscrafting.api.RecipeService;
import ca.bungo.bungoscrafting.api.recipes.LargeShapedRecipe;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Create the custom 5x5 crafting grid
 * This class handles all the inventory clicks, recipe verifying, and crafting animation spawning for the service
 * */
public class LargeCrafter implements InventoryHolder {

    private final Inventory inventory;

    private ItemStack confirmCraft;
    private ItemStack cancelCraft;
    private ItemStack resultItem;
    private ItemStack _resultItem;
    private ItemStack fillerItem;

    private ItemStack result;
    private BukkitRunnable task;

    public LargeCrafter() {
        inventory = Bukkit.createInventory(this, 45, "&eUltimate Crafting Table".asComponent());
        loadInventory();
        recipeLoop();
    }

    private void loadInventory() {
        confirmCraft = new ItemStack(Material.GREEN_CONCRETE);
        cancelCraft = new ItemStack(Material.RED_CONCRETE);
        fillerItem = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        resultItem = new ItemStack(Material.BARRIER);

        ItemMeta meta = confirmCraft.getItemMeta();
        meta.displayName("&aConfirm Craft".asComponent());
        meta.lore(List.of(
                "&ePressing this button will use up the items in the crafting grid!".asComponent(),
                "&4Crafts &lCannot &r&4be cancelled once started!".asComponent()
        ));
        confirmCraft.setItemMeta(meta);

        meta = cancelCraft.getItemMeta();
        meta.displayName("&cCancel Craft".asComponent());
        meta.lore(List.of(
                "&ePressing this button will return the items in the crafting grid to your inventory!".asComponent()
        ));
        cancelCraft.setItemMeta(meta);

        meta = fillerItem.getItemMeta();
        meta.displayName("".asComponent());
        fillerItem.setItemMeta(meta);

        meta = resultItem.getItemMeta();
        meta.displayName("&4Invalid Recipe".asComponent());
        resultItem.setItemMeta(meta);

        _resultItem = resultItem;

        inventory.setItem(16, confirmCraft);
        inventory.setItem(24, resultItem);
        inventory.setItem(34, cancelCraft);

        for(int i = 0; i < 45; i++){
            if(i == 16 || i == 34 || i == 24) continue;
            if(i <= 4 || (i >= 9 && i <= 13) || (i >= 18 && i <= 22) || (i >= 27 && i <= 31) || (i >= 36 && i <= 40)) continue;
            inventory.setItem(i, fillerItem);
        }
    }

    /**
     * Get the LargeCrafter inventory
     * @return Custom 5x5 crafting grid inventory
     * */
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private boolean matchesRecipe(Map<Integer, ItemStack> grid, LargeShapedRecipe recipe, int startRow, int startCol) {
        String[] shape = recipe.getShape();
        Map<Character, RecipeChoice> ingredients = recipe.getChoiceMap();
        int recipeHeight = shape.length;
        int recipeWidth = shape[0].length();

        // Check that the ingredients match within the recipe's area
        for (int i = 0; i < recipeHeight; i++) {
            for (int j = 0; j < recipeWidth; j++) {
                int gridIndex = (startRow + i) * 5 + (startCol + j); // Calculate the correct index in the 5x5 grid
                char ingredientKey = shape[i].charAt(j);
                RecipeChoice requiredChoice = ingredients.get(ingredientKey);
                ItemStack itemInGrid = grid.get(gridIndex);

                if (requiredChoice != null) {  // Check if a specific ingredient is required at this position
                    if (itemInGrid == null || !requiredChoice.test(itemInGrid)) {
                        return false;  // Ingredient does not match
                    }
                } else {  // If no ingredient is mapped to this character
                    if (itemInGrid != null && itemInGrid.getType() != Material.AIR) {
                        return false;  // Slot should be empty
                    }
                }
            }
        }

        // Check that all other slots in the 5x5 grid are empty
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                // Check if the slot is outside the bounds of the recipe
                if (row < startRow || row >= startRow + recipeHeight || col < startCol || col >= startCol + recipeWidth) {
                    int gridIndex = row * 5 + col;
                    ItemStack itemInGrid = grid.get(gridIndex);
                    if (itemInGrid != null && itemInGrid.getType() != Material.AIR) {
                        return false;  // Found an item outside the recipe area
                    }
                }
            }
        }

        return true;  // All checks passed, recipe matches
    }

    private LargeShapedRecipe findRecipeInGrid(Map<Integer, ItemStack> grid, List<LargeShapedRecipe> recipes) {
        for (LargeShapedRecipe recipe : recipes) {
            String[] shape = recipe.getShape();
            int rows = shape.length;
            int cols = shape[0].length();
            for (int startRow = 0; startRow <= 5 - rows; startRow++) {
                for (int startCol = 0; startCol <= 5 - cols; startCol++) {
                    if (matchesRecipe(grid, recipe, startRow, startCol)) {
                        return recipe;  // Return the matching recipe directly
                    }
                }
            }
        }
        return null;  // Return null if no recipe matches
    }

    private Recipe checkForRecipe(){
        Map<Integer, ItemStack> itemToSlots = getIntegerItemStackMap();
        return findRecipeInGrid(itemToSlots, RecipeService.getRecipes().stream().map((r) -> (LargeShapedRecipe) r).toList());
    }

    private @NotNull Map<Integer, ItemStack> getIntegerItemStackMap() {
        ItemStack[] contents = inventory.getContents();
        Map<Integer, ItemStack> itemToSlots = new HashMap<>();
        for(int i = 0; i < inventory.getContents().length; i++){
            ItemStack item = contents[i];
            if(i <= 4){
                itemToSlots.put(i, item);
            }
            else if((i >= 9 && i <= 13)){
                itemToSlots.put(i - 4, item);
            }
            else if((i >= 18 && i <= 22)){
                itemToSlots.put(i - 8, item);
            }
            else if((i >= 27 && i <= 31)){
                itemToSlots.put(i - 12, item);
            }
            else if((i >= 36 && i <= 40)){
                itemToSlots.put(i - 16, item);
            }
        }
        return itemToSlots;
    }

    private void recipeLoop(){
        task = new BukkitRunnable() {
            @Override
            public void run() {
                Recipe recipe = checkForRecipe();
                if(recipe != null){
                    result = recipe.getResult();
                }
                else {
                    result = null;
                }

                if(result == null){
                    resultItem = _resultItem;
                } else {
                    ItemStack stack = result.clone();
                    ItemMeta meta = stack.getItemMeta();
                    meta.lore(List.of(
                            "&5Clicking 'Confirm' will use up your reagents!".asComponent(),
                            "&5This item is what will be crafted on confirmation".asComponent()));
                    stack.setItemMeta(meta);
                    resultItem = stack;
                }

                inventory.setItem(24, resultItem);
            }
        };

        task.runTaskTimer(CustomCraftingSystem.getInstance(), 0, 10);
    }

    /**
     * Handles all the inventory click logic.
     * This listener is already defined within the crafting service
     * @param event InventoryClickEvent to manage
     * */
    public void handleInventoryClick(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();

        if(itemStack == null || !(event.getWhoClicked() instanceof Player player)) return;

        if(itemStack.equals(fillerItem) || itemStack.equals(resultItem)) {
            event.setCancelled(true);
            return;
        }

        if(itemStack.equals(confirmCraft)) {
            event.setCancelled(true);
            Recipe recipe = checkForRecipe();
            if(recipe == null)
                result = null;
            else
                result = recipe.getResult();

            if(result == null) {
                event.getWhoClicked().playSound(Sound.sound(Key.key("minecraft:block.note_block.bass"), Sound.Source.MASTER, 1f, 1f));
                return;
            }

            List<ItemStack> reagents = inventory.getContents().toList();
            reagents = reagents.stream().filter((stack) -> {
                if(stack == null) return false;
                if(stack.equals(fillerItem)) return false;
                if(stack.equals(confirmCraft)) return false;
                if(stack.equals(cancelCraft)) return false;
                return !stack.equals(resultItem);
            }).toList();


            CustomCraft craft = CustomCraftingSystem.getInstance().craftingManager.createCustomCraft(event.getWhoClicked().getLocation(),
                    reagents, List.of(result));

            for(ItemStack reagent : reagents) {
                ItemStack toRemove = reagent.clone();
                toRemove.setAmount(1);
                inventory.removeItem(toRemove);
            }

            craft.startCraft();
        }
        else if(itemStack.equals(cancelCraft)) {
            event.getWhoClicked().playSound(Sound.sound(Key.key("minecraft:block.note_block.bass"), Sound.Source.MASTER, 1f, 1f));
            event.setCancelled(true);

            for(int i = 0; i < inventory.getSize(); i++){
                ItemStack stack = inventory.getItem(i);
                if(stack == null) continue;
                if(stack.equals(fillerItem)) continue;
                if(stack.equals(confirmCraft)) continue;
                if(stack.equals(cancelCraft)) continue;
                if(stack.equals(resultItem)) continue;

                player.getInventory().addItem(stack.clone());
                inventory.setItem(i, null);
            }
        }
    }

    /**
     * Handles all the inventory close logic.
     * This listener is already defined within the crafting service
     * @param event InventoryCloseEvent to manage
     * */
    public void handleInventoryClose(InventoryCloseEvent event){
        for(ItemStack stack : inventory.getContents()){
            if(stack == null) continue;
            if(stack.equals(fillerItem)) continue;
            if(stack.equals(confirmCraft)) continue;
            if(stack.equals(cancelCraft)) continue;
            if(stack.equals(resultItem)) continue;

            event.getPlayer().getInventory().addItem(stack);
        }

        task.cancel();
    }

}
