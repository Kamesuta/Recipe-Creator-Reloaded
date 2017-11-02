package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public class SerializedRecipe implements Serializable {
	private static final long serialVersionUID = -996412512091646013L;

	public static enum RecipeType {
		SHAPELESS(0, "Shapeless"),
		SHAPED(1, "Shaped"),
		FURNACE(2, "Furnace"),
		;

		public final int id;
		public final String name;

		private RecipeType(final int id, final String name) {
			this.id = id;
			this.name = name;
		}

		public static RecipeType fromID(final int id) {
			for (final RecipeType type : values())
				if (type.id==id)
					return type;
			return SHAPED;
		}
	}

	private ShapedRecipe shaped_recipe;
	private ShapelessRecipe shapeless_recipe;
	private FurnaceRecipe furnace_recipe;

	public SerializedRecipe(final ShapedRecipe recipe) {
		this.shaped_recipe = recipe;
	}

	public SerializedRecipe(final ShapelessRecipe recipe) {
		this.shapeless_recipe = recipe;
	}

	public SerializedRecipe(final FurnaceRecipe recipe) {
		this.furnace_recipe = recipe;
	}

	public Recipe getRecipe() {
		if (this.shaped_recipe!=null)
			return this.shaped_recipe;
		else if (this.shapeless_recipe!=null)
			return this.shapeless_recipe;
		else if (this.furnace_recipe!=null)
			return this.furnace_recipe;
		return null;
	}

	public RecipeType getType() {
		if (this.shaped_recipe!=null)
			return RecipeType.SHAPED;
		else if (this.shapeless_recipe!=null)
			return RecipeType.SHAPELESS;
		else if (this.furnace_recipe!=null)
			return RecipeType.FURNACE;
		return null;
	}

	public static SerializedRecipe fromRecipe(final Recipe recipe) {
		if (recipe instanceof ShapedRecipe)
			return new SerializedRecipe((ShapedRecipe) recipe);
		else if (recipe instanceof ShapelessRecipe)
			return new SerializedRecipe((ShapelessRecipe) recipe);
		else if (recipe instanceof FurnaceRecipe)
			return new SerializedRecipe((FurnaceRecipe) recipe);
		return null;
	}

	public static boolean recipeEquals(final SerializedRecipe recipe0, final SerializedRecipe recipe1) {
		if (recipe0==null&&recipe1==null)
			return true;
		if (recipe0==null||recipe1==null)
			return false;
		final RecipeType type0 = recipe0.getType();
		final RecipeType type1 = recipe1.getType();
		if (type0==null||type1==null||type0!=type1)
			return false;
		final Recipe recipe00 = recipe0.getRecipe();
		final Recipe recipe01 = recipe1.getRecipe();
		switch (type0) {
			case SHAPED:
				final ShapedRecipe recipe10 = (ShapedRecipe) recipe00;
				final ShapedRecipe recipe11 = (ShapedRecipe) recipe01;
				return recipe10.getResult().equals(recipe11.getResult())&&recipe10.getIngredientMap().equals(recipe11.getIngredientMap());
			case SHAPELESS:
				final ShapelessRecipe recipe20 = (ShapelessRecipe) recipe00;
				final ShapelessRecipe recipe21 = (ShapelessRecipe) recipe01;
				return recipe20.getResult().equals(recipe21.getResult())&&recipe20.getIngredientList().equals(recipe21.getIngredientList());
			case FURNACE:
				final FurnaceRecipe recipe30 = (FurnaceRecipe) recipe00;
				final FurnaceRecipe recipe31 = (FurnaceRecipe) recipe01;
				return recipe30.getResult().equals(recipe31.getResult())&&recipe30.getInput().isSimilar(recipe31.getInput());
			default:
				return false;
		}
	}
}
