package com.fiscalleti.recipecreator.serialization;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import com.fiscalleti.recipecreator.RecipeCreator;
import com.google.common.collect.Lists;

public class RecipeStorage {
	private final File recipeDir;

	public RecipeStorage(final File recipeDir) {
		this.recipeDir = recipeDir;
	}

	public void putRecipe(final String name, final SerializedRecipe recipe) {
		putRecipe(new File(this.recipeDir, name+".json"), recipe);
	}

	public SerializedRecipe getRecipe(final String name) {
		return getRecipe(new File(this.recipeDir, name+".json"));
	}

	public List<SerializedRecipe> getRecipes() {
		final List<SerializedRecipe> ret = Lists.newArrayList();
		for (final File recipeFile : this.recipeDir.listFiles()) {
			final SerializedRecipe recipe = getRecipe(recipeFile);
			if (recipe!=null)
				ret.add(recipe);
		}
		return ret;
	}

	public static void putRecipe(final File file, final SerializedRecipe recipe) {
		try {
			ObjectHandler.write(file, SerializedRecipe.class, recipe);
		} catch (final Exception e) {
			RecipeCreator.instance.log.log(Level.WARNING, e.getMessage(), e);
		}
	}

	public static SerializedRecipe getRecipe(final File file) {
		try {
			return ObjectHandler.read(file, SerializedRecipe.class);
		} catch (final Exception e) {
			RecipeCreator.instance.log.log(Level.WARNING, e.getMessage(), e);
		}
		return null;
	}

	public static RecipeStorage createRecipesStorage(final File dataDir) {
		if (!dataDir.exists())
			dataDir.mkdir();
		final File recipeDir = new File(dataDir, "recipes");
		if (!recipeDir.exists())
			recipeDir.mkdir();
		return new RecipeStorage(recipeDir);
	}
}
