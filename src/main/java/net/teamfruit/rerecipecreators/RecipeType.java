package net.teamfruit.rerecipecreators;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

public enum RecipeType {
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

	public static RecipeType fromRecipe(final Recipe recipe) {
		if (recipe instanceof ShapedRecipe)
			return RecipeType.SHAPED;
		else if (recipe instanceof ShapelessRecipe)
			return RecipeType.SHAPELESS;
		else if (recipe instanceof FurnaceRecipe)
			return RecipeType.FURNACE;
		return null;
	}
}
