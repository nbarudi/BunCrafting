package ca.bungo.customcraftingsystem.api.recipes;

import com.google.common.base.Preconditions;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LargeShapedRecipe implements Recipe {

    private ItemStack result;

    private String[] rows;
    private Map<Character, RecipeChoice> ingredients = new HashMap<>();

    public LargeShapedRecipe(@NotNull ItemStack result) {
        this.result = result;
    }

    @Override
    public @NotNull ItemStack getResult() {
        return result;
    }

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

    @NotNull
    @Deprecated // Paper
    public LargeShapedRecipe setIngredient(char key, @NotNull MaterialData ingredient) {
        return setIngredient(key, ingredient.getItemType(), ingredient.getData());
    }

    @NotNull
    public LargeShapedRecipe setIngredient(char key, @NotNull Material ingredient) {
        return setIngredient(key, ingredient, 0);
    }

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

    @NotNull
    public LargeShapedRecipe setIngredient(char key, @NotNull RecipeChoice ingredient) {
        Preconditions.checkArgument(ingredients.containsKey(key), "Symbol does not appear in the shape:", key);

        ingredients.put(key, ingredient);
        return this;
    }

    @NotNull
    public LargeShapedRecipe setIngredient(char key, @NotNull ItemStack item) {
        return setIngredient(key, new RecipeChoice.ExactChoice(item));
    }

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

    @NotNull
    public String[] getShape() {
        return rows.clone();
    }


}
