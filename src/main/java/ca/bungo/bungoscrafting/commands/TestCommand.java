package ca.bungo.bungoscrafting.commands;

import ca.bungo.bungoscrafting.CustomCraftingSystem;
import ca.bungo.bungoscrafting.api.RecipeService;
import ca.bungo.bungoscrafting.api.recipes.LargeShapedRecipe;
import ca.bungo.bungoscrafting.types.LargeCrafter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TestCommand extends Command {



    public TestCommand() {
        super("test");

        this.description = "Developmental testing command!";
        this.setPermission("customcraftingsystem.commands.test");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player player)) return false;

        /*
        Location baseLocation = player.getLocation();

        List<ItemStack> reagents = List.of(
                new ItemStack(Material.DIAMOND_SWORD),
                new ItemStack(Material.DIAMOND_SWORD),
                new ItemStack(Material.NETHERITE_SWORD),
                new ItemStack(Material.NETHERITE_SWORD),
                new ItemStack(Material.NETHER_STAR)
        );
        List<ItemStack> results = List.of(new ItemStack(Material.DIRT));

        CustomCraft craft = CustomCraftingSystem.getInstance().craftingManager.createCustomCraft(baseLocation, reagents, results);
        Bukkit.getScheduler().runTaskLater(CustomCraftingSystem.getInstance(), craft::startCraft, 40);*/


        LargeShapedRecipe recipe = new LargeShapedRecipe(new ItemStack(Material.NETHER_STAR))
                .shape( "RRRRR",
                        "R   R",
                        "R   R",
                        "R   R",
                        "RRRRR")
                .setIngredient('R', Material.REDSTONE_BLOCK);
        RecipeService.addRecipe(NamespacedKey.fromString("test_recipe", CustomCraftingSystem.getInstance()), recipe);

        recipe = new LargeShapedRecipe(new ItemStack(Material.WITHER_SKELETON_SKULL))
                .shape("SSS", "S S", "SSS")
                .setIngredient('S', Material.SOUL_SAND);
        RecipeService.addRecipe(NamespacedKey.fromString("test_recipe_2", CustomCraftingSystem.getInstance()), recipe);

        recipe = new LargeShapedRecipe(new ItemStack(Material.SPONGE))
                .shape("YYYYY", " WDW ")
                .setIngredient('Y', Material.YELLOW_CONCRETE_POWDER)
                .setIngredient('W', Material.WATER_BUCKET)
                .setIngredient('D', Material.DIAMOND);
        RecipeService.addRecipe(NamespacedKey.fromString("test_recipe_3", CustomCraftingSystem.getInstance()), recipe);

        LargeCrafter crafter = new LargeCrafter();
        player.openInventory(crafter.getInventory());

        return false;
    }
}
