package net.teamfruit.rerecipecreators;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.inventory.Recipe;

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
		if (this.myrecipes.remove(recipe)) {
			this.myrecipes.remove(recipe);
			setRecipes(this.myrecipes);
		}
	}

	public void clearRecipe() {
		this.server.resetRecipes();
	}
}
