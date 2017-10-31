package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
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
		public transient final String name;

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

	public String permission;

	private RecipeType type;
	private String id;

	private ShapedRecipe shapedRecipe;
	private ShapelessRecipe shapelessRecipe;
	private FurnaceRecipe furnacerecipe;

	public SerializedRecipe(final ShapedRecipe r, final String id) {
		this.shapedRecipe = r;
		this.type = RecipeType.SHAPED;
		this.id = id;
		this.permission = "recipecreator.recipes."+getResult().getType().name();
	}

	public SerializedRecipe(final ShapelessRecipe r, final String id) {
		this.shapelessRecipe = r;
		this.type = RecipeType.SHAPELESS;
		this.id = id;
		this.permission = "recipecreator.recipes."+getResult().getType().name();
	}

	public SerializedRecipe(final FurnaceRecipe r, final String id) {
		this.furnacerecipe = r;
		this.type = RecipeType.FURNACE;
		this.id = id;
		this.permission = "recipecreator.recipes."+getResult().getType().name();
	}

	public String[] getShape() {
		return this.type==RecipeType.SHAPED ? this.shapedRecipe.getShape() : null;
	}

	public ItemStack getResult() {
		switch (this.type) {
			case FURNACE:
				return this.furnacerecipe.getResult();
			case SHAPED:
				return this.shapedRecipe.getResult();
			case SHAPELESS:
				return this.shapelessRecipe.getResult();
			default:
				return null;
		}
	}

	public Map<Character, ItemStack> getIngredientMap() {
		return this.type==RecipeType.SHAPED ? this.shapedRecipe.getIngredientMap() : null;
	}

	public ArrayList<ItemStack> getIngredients() {
		final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		switch (this.type) {
			case FURNACE:
				ret.add(this.furnacerecipe.getInput());
				return ret;
			case SHAPED:
				for (final Entry<Character, ItemStack> entry : getIngredientMap().entrySet())
					ret.add(entry.getValue());
				return ret;
			case SHAPELESS:
				ret.addAll(this.shapelessRecipe.getIngredientList());
				return ret;
			default:
				return null;
		}
	}

	public Recipe getRecipe() {
		switch (this.type) {
			case FURNACE:
				return this.furnacerecipe;
			case SHAPED:
				return this.shapedRecipe;
			case SHAPELESS:
				return this.shapelessRecipe;
			default:
				return null;
		}
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public RecipeType getType() {
		return this.type;
	}

}
