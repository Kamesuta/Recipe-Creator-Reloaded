package com.fiscalleti.recipecreator;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;

public class RecipeRegistrar implements Iterable<Recipe> {
	private static final Set<Recipe> myrecipes = Collections.newSetFromMap(new WeakHashMap<Recipe, Boolean>());
	private final Server server;

	public RecipeRegistrar(final Server server) {
		this.server = server;
	}

	public void addRecipe(final Recipe recipe) {
		myrecipes.add(recipe);
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
		if (!myrecipes.contains(recipe))
			return;
		final List<Recipe> cache = Lists.newArrayList(this);
		setRecipes(cache);
	}

	public void clearRecipe() {
		for (final Iterator<Recipe> itr = this.server.recipeIterator(); itr.hasNext();) {
			final Recipe recipe = itr.next();
			if (myrecipes.contains(recipe))
				itr.remove();
		}
	}

	public List<Recipe> getRecipesFor(final ItemStack itemStack) {
		final List<Recipe> recipes = this.server.getRecipesFor(itemStack);
		final List<Recipe> result = Lists.newArrayList();
		for (final Recipe recipe : recipes)
			if (myrecipes.contains(recipe))
				result.add(recipe);
		return result;
	}

	private static final Predicate<Recipe> myRecipePredicate = new Predicate<Recipe>() {
		@Override
		public boolean apply(final Recipe recipe) {
			return myrecipes.contains(recipe);
		}
	};

	@Override
	public UnmodifiableIterator<Recipe> iterator() {
		return Iterators.filter(this.server.recipeIterator(), myRecipePredicate);
	}
}
