package ca.bungo.bungoscrafting.api.recipes;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Create a 5x5 crafting recipe for use with the LargeCrafter
 * Follows the same logic as a standard minecraft shaped recipe would use
 * @see org.bukkit.inventory.ShapedRecipe
 * @see ca.bungo.bungoscrafting.types.LargeCrafter
 * */
public class LargeShapedRecipe implements Recipe {

    private final ItemStack result;

    private String[] rows;
    private Map<Character, RecipeChoice> ingredients = new HashMap<>();

    /**
     * Create a new Large Shaped Recipe
     * @param result The itemstack that will be given on successful craft
     * */
    public LargeShapedRecipe(@NotNull ItemStack result) {
        this.result = result;
    }

    /**
     * Get the itemstack that will be created on successful craft
     * @return ItemStack that is crafted
     * */
    @Override
    public @NotNull ItemStack getResult() {
        return result;
    }

    /**
     * Define the crafting shape for the recipe.
     * Acts as a builder function
     * This is handled the exact same way as standard minecraft shaped recipes.
     * @param shape Recipe Shape
     * @return self
     * @see org.bukkit.inventory.ShapedRecipe
     * */
    @NotNull
    public LargeShapedRecipe shape(@NotNull final String... shape) {
        Preconditions.checkArgument(shape != null, "Must provide a shape");
        Preconditions.checkArgument(shape.length > 0 && shape.length < 6, "Crafting recipes should be 1, 2 or 3 rows, not ", shape.length);

        int lastLen = -1;
        for (String row : shape) {
            Preconditions.checkArgument(row != null, "Shape cannot have null rows");
            Preconditions.checkArgument(row.length() > 0 && row.length() < 6, "Crafting rows should be 1, 2, or 3 characters, not ", row.length());

            Preconditions.checkArgument(lastLen == -1 || lastLen == row.length(), "Crafting recipes must be rectangular");
            lastLen = row.length();
        }
        this.rows = new String[shape.length];
        for (int i = 0; i < shape.length; i++) {
            this.rows[i] = shape[i];
        }

        // Remove character mappings for characters that no longer exist in the shape
        HashMap<Character, RecipeChoice> newIngredients = new HashMap<>();
        for (String row : shape) {
            for (Character c : row.toCharArray()) {
                newIngredients.put(c, ingredients.get(c));
            }
        }
        this.ingredients = newIngredients;

        return this;
    }

    /**
     * Set the shape key's ingredient
     * @param key The key defined in shape
     * @param ingredient The ingredient which the key represents
     * @return self
     * @see org.bukkit.inventory.ShapedRecipe
     * */
    @NotNull
    @Deprecated // Paper
    public LargeShapedRecipe setIngredient(char key, @NotNull MaterialData ingredient) {
        return setIngredient(key, ingredient.getItemType(), ingredient.getData());
    }

    /**
     * Set the shape key's ingredient
     * @param key The key defined in shape
     * @param ingredient The ingredient which the key represents
     * @return self
     * @see org.bukkit.inventory.ShapedRecipe
     * */
    @NotNull
    public LargeShapedRecipe setIngredient(char key, @NotNull Material ingredient) {
        return setIngredient(key, ingredient, 0);
    }

    /**
     * Set the shape key's ingredient
     * @param key The key defined in shape
     * @param ingredient The ingredient which the key represents
     * @param raw damage data value
     * @return self
     * @see org.bukkit.inventory.ShapedRecipe
     * */
    @Deprecated
    @NotNull
    public LargeShapedRecipe setIngredient(char key, @NotNull Material ingredient, int raw) {
        Preconditions.checkArgument(ingredients.containsKey(key), "Symbol does not appear in the shape:", key);

        // -1 is the old wildcard, map to Short.MAX_VALUE as the new one
        if (raw == -1) {
            raw = Short.MAX_VALUE;
        }

        ingredients.put(key, new RecipeChoice.MaterialChoice(Collections.singletonList(ingredient)));
        return this;
    }

    /**
     * Set the shape key's ingredient
     * @param key The key defined in shape
     * @param ingredient The ingredient which the key represents
     * @return self
     * @see org.bukkit.inventory.ShapedRecipe
     * */
    @NotNull
    public LargeShapedRecipe setIngredient(char key, @NotNull RecipeChoice ingredient) {
        Preconditions.checkArgument(ingredients.containsKey(key), "Symbol does not appear in the shape:", key);

        ingredients.put(key, ingredient);
        return this;
    }

    /**
     * Set the shape key's ingredient
     * @param key The key defined in shape
     * @param item The exact item which the key represents
     * @return self
     * @see org.bukkit.inventory.ShapedRecipe
     * */
    @NotNull
    public LargeShapedRecipe setIngredient(char key, @NotNull ItemStack item) {
        return setIngredient(key, new RecipeChoice.ExactChoice(item));
    }

    /**
     * Get the key - ingredient map for the recipe
     * @return Ingredient Map <Character, ItemStack>
     * @deprecated RecipeChoices allow multiple item types instead of the 1 that this returns
     * */
    @Deprecated // Paper
    @NotNull
    public Map<Character, ItemStack> getIngredientMap() {
        HashMap<Character, ItemStack> result = new HashMap<Character, ItemStack>();
        for (Map.Entry<Character, RecipeChoice> ingredient : ingredients.entrySet()) {
            if (ingredient.getValue() == null) {
                result.put(ingredient.getKey(), null);
            } else {
                result.put(ingredient.getKey(), ingredient.getValue().getItemStack().clone());
            }
        }
        return result;
    }

    /**
     * Get the recipe choice map.
     * @return Map containing key value pairs between Character and RecipeChoice
     * @see RecipeChoice
     * */
    @NotNull
    public Map<Character, RecipeChoice> getChoiceMap() {
        Map<Character, RecipeChoice> result = new HashMap<>();
        for (Map.Entry<Character, RecipeChoice> ingredient : ingredients.entrySet()) {
            if (ingredient.getValue() == null) {
                result.put(ingredient.getKey(), null);
            } else {
                result.put(ingredient.getKey(), ingredient.getValue().clone());
            }
        }
        return result;
    }

    /**
     * Get the shape of the recipe
     * @return String[] containing the shape defined for the recipe
     * */
    @NotNull
    public String[] getShape() {
        return rows.clone();
    }


}
