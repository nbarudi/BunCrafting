package ca.bungo.bungoscrafting.api;

import ca.bungo.bungoscrafting.CustomCraftingSystem;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;

import java.util.*;

/**
 * Recipe service for adding, or getting custom recipes
 * */
public class RecipeService {

    private static final Map<NamespacedKey, Recipe> recipes = new HashMap<NamespacedKey, Recipe>();

    /**
     * Get a custom recipe from a NamespacedKey
     * @param key NamespacedKey relating to the recipe
     * @return Recipe found or null if nothing is found
     * */
    public static Recipe getRecipe(NamespacedKey key) {
        return recipes.get(key);
    }

    /**
     * Add a new Recipe to the custom crafting service
     * @param name namespace recipe key.
     * @param recipe Custom recipe to define within the recipe service
     * @deprecated Should make use of a NamespacedKey instead of a string key
     * */
    @Deprecated
    public static void addRecipe(String name, Recipe recipe) {
        recipes.put(NamespacedKey.fromString(name.toLowerCase(), CustomCraftingSystem.getInstance()), recipe);
    }

    /**
     * Add a new Recipe to the custom crafting service
     * @param key NamespacedKey representing the Recipe
     * @param recipe Recipe to add to the custom crafting service
     * */
    public static void addRecipe(NamespacedKey key, Recipe recipe) {
        recipes.put(key, recipe);
    }

    /**
     * Get the currently stored custom crafting recipes
     * @return Collection containing Recipes
     * */
    public static Collection<Recipe> getRecipes(){
        return recipes.values();
    }

}
