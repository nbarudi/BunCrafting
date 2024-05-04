package ca.bungo.customcraftingsystem.api;

import ca.bungo.customcraftingsystem.CustomCraftingSystem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.*;

public class RecipeService {

    private static final Map<NamespacedKey, Recipe> recipes = new HashMap<NamespacedKey, Recipe>();

    public static Recipe getRecipe(NamespacedKey key) {
        return recipes.get(key);
    }

    @Deprecated
    public static void addRecipe(String name, Recipe recipe) {
        recipes.put(NamespacedKey.fromString(name, CustomCraftingSystem.getInstance()), recipe);
    }

    public static void addRecipe(NamespacedKey key, Recipe recipe) {
        recipes.put(key, recipe);
    }

    public static Collection<Recipe> getRecipes(){
        return recipes.values();
    }

}
