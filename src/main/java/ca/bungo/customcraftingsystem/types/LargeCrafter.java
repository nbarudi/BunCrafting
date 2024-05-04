package ca.bungo.customcraftingsystem.types;

import ca.bungo.customcraftingsystem.CustomCraftingSystem;
import ca.bungo.customcraftingsystem.api.RecipeService;
import ca.bungo.customcraftingsystem.api.recipes.LargeShapedRecipe;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    private Recipe checkForRecipe(){
        Recipe foundRecipe = null;
        Map<Integer, ItemStack> itemToSlots = getIntegerItemStackMap();

        for(Recipe recipe : RecipeService.getRecipes()){
            if(recipe instanceof LargeShapedRecipe largeShapedRecipe){
                Map<Character, RecipeChoice> opts = largeShapedRecipe.getChoiceMap();
                String[] shape = largeShapedRecipe.getShape();

                int loops = 0;
                boolean isCorrect = true;
                for(String shapeLine : shape){
                    for(int i = 0; i < shapeLine.length(); i++){
                        RecipeChoice recipeChoice = opts.get(shapeLine.charAt(i));
                        ItemStack toTest = itemToSlots.get(i + (loops*5));
                        if(toTest == null)
                            toTest = new ItemStack(Material.AIR);

                        if(recipeChoice == null){
                            if(!toTest.getType().equals(Material.AIR)){
                                isCorrect = false;
                                break;
                            }
                        } else if(!recipeChoice.test(toTest)) {
                            isCorrect = false;
                            break;
                        }


                    }
                    if(!isCorrect) break;
                    loops++;
                }
                if(isCorrect){
                    foundRecipe = recipe;
                    break;
                }
            }
        }

        return foundRecipe;
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

    public void handleInventoryClick(InventoryClickEvent event){
        ItemStack itemStack = event.getCurrentItem();

        if(itemStack == null || !(event.getWhoClicked() instanceof Player player)) return;



        if(itemStack.equals(fillerItem) || itemStack.equals(resultItem)) {
            event.setCancelled(true);
            return;
        }

        if(itemStack.equals(confirmCraft)) {
            event.setCancelled(true);

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
                inventory.removeItemAnySlot(reagent);
            }

            event.getWhoClicked().closeInventory();
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
