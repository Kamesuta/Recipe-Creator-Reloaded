package net.teamfruit.rerecipecreators;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import guava10.com.google.common.collect.Sets;

public class RecipeRegistrar {
	private final Server server;
	private Set<Recipe> myrecipes = Sets.newHashSet();

	public RecipeRegistrar(final Server server) {
		this.server = server;
	}

	public void addRecipe(final Recipe recipe) {
		this.myrecipes.add(recipe);
		this.server.addRecipe(recipe);
	}

	public void setRecipes(final Collection<Recipe> recipes) {
		clearRecipe();
		addRecipes(recipes);
	}

	public void addRecipes(final Collection<Recipe> recipes) {
		for (final Recipe recipe : recipes)
			addRecipe(recipe);
	}

	public void removeRecipe(final Recipe recipe) {
		if (recipe==null)
			return;
		for (final Iterator<Recipe> itr = this.server.recipeIterator(); itr.hasNext();) {
			final Recipe itrecipe = itr.next();
			if (recipeEquals(recipe, itrecipe))
				itr.remove();
		}
	}

	public void clearRecipe() {
		for (final Iterator<Recipe> itr = this.server.recipeIterator(); itr.hasNext();) {
			final Recipe itrecipe = itr.next();
			for (final Recipe myrecipe : this.myrecipes)
				if (recipeEquals(myrecipe, itrecipe))
					itr.remove();
		}
	}

	private boolean recipeEquals(final Recipe recipe1, final Recipe recipe2) {
		if (recipe1==null&&recipe2==null)
			return true;
		if (recipe1==null||recipe2==null)
			return false;
		if (!recipe1.getResult().isSimilar(recipe2.getResult()))
			return false;
		if (recipe1 instanceof ShapedRecipe&&recipe2 instanceof ShapedRecipe) {
			final Recipes.RecipeBuilder.ShapedRecipeBuilder.ShapedRecipeIngredients ing1 = new Recipes.RecipeBuilder.ShapedRecipeBuilder.ShapedRecipeIngredients();
			ing1.fromRecipe((ShapedRecipe) recipe1);
			final Recipes.RecipeBuilder.ShapedRecipeBuilder.ShapedRecipeIngredients ing2 = new Recipes.RecipeBuilder.ShapedRecipeBuilder.ShapedRecipeIngredients();
			ing2.fromRecipe((ShapedRecipe) recipe2);
			return ing1.equals(ing2);
		} else if (recipe1 instanceof ShapelessRecipe&&recipe2 instanceof ShapelessRecipe) {
			final Recipes.RecipeBuilder.ShapelessRecipeBuilder.ShapelessRecipeIngredients ing1 = new Recipes.RecipeBuilder.ShapelessRecipeBuilder.ShapelessRecipeIngredients();
			ing1.fromRecipe((ShapelessRecipe) recipe1);
			final Recipes.RecipeBuilder.ShapelessRecipeBuilder.ShapelessRecipeIngredients ing2 = new Recipes.RecipeBuilder.ShapelessRecipeBuilder.ShapelessRecipeIngredients();
			ing2.fromRecipe((ShapelessRecipe) recipe2);
			return ing1.equals(ing2);
		} else if (recipe1 instanceof FurnaceRecipe&&recipe2 instanceof FurnaceRecipe) {
			final Recipes.RecipeBuilder.FurnaceRecipeBuilder.FurnaceRecipeIngredients ing1 = new Recipes.RecipeBuilder.FurnaceRecipeBuilder.FurnaceRecipeIngredients();
			ing1.fromRecipe((FurnaceRecipe) recipe1);
			final Recipes.RecipeBuilder.FurnaceRecipeBuilder.FurnaceRecipeIngredients ing2 = new Recipes.RecipeBuilder.FurnaceRecipeBuilder.FurnaceRecipeIngredients();
			ing2.fromRecipe((FurnaceRecipe) recipe2);
			return ing1.equals(ing2);
		}
		return false;
	}
}
