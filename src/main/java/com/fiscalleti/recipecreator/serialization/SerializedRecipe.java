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
}
