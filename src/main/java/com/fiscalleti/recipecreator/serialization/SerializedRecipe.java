package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class SerializedRecipe implements Serializable {
	private static final long serialVersionUID = -996412512091646013L;

	public static enum RecipeType {
		TYPE_SHAPELESS(0, "Shapeless"),
		TYPE_SHAPED(1, "Shaped"),
		TYPE_FURNACE(2, "Furnace"),
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
			return TYPE_SHAPED;
		}
	}

	public String permission;

	private RecipeType type;
	private String id;

	private SerializableShapedRecipe shapedRecipe;
	private SerializableShapelessRecipe shapelessRecipe;
	private SerializableFurnaceRecipe furnacerecipe;

	public SerializedRecipe(final SerializableShapedRecipe r, final String id) {
		this.shapedRecipe = r;
		this.type = RecipeType.TYPE_SHAPED;
		this.id = id;
		this.permission = "recipecreator.recipes."+getResult().getType().name();
	}

	public SerializedRecipe(final SerializableShapelessRecipe r, final String id) {
		this.shapelessRecipe = r;
		this.type = RecipeType.TYPE_SHAPELESS;
		this.id = id;
		this.permission = "recipecreator.recipes."+getResult().getType().name();
	}

	public SerializedRecipe(final SerializableFurnaceRecipe r, final String id) {
		this.furnacerecipe = r;
		this.type = RecipeType.TYPE_FURNACE;
		this.id = id;
		this.permission = "recipecreator.recipes."+getResult().getType().name();
	}

	public String[] getShape() {
		return this.type==RecipeType.TYPE_SHAPED ? this.shapedRecipe.unbox().getShape() : null;
	}

	public ItemStack getResult() {
		switch (this.type) {
			case TYPE_FURNACE:
				return this.furnacerecipe.unbox().getResult();
			case TYPE_SHAPED:
				return this.shapedRecipe.unbox().getResult();
			case TYPE_SHAPELESS:
				return this.shapelessRecipe.unbox().getResult();
			default:
				return null;
		}
	}

	public Map<Character, ItemStack> getIngredientMap() {
		return this.type==RecipeType.TYPE_SHAPED ? this.shapedRecipe.unbox().getIngredientMap() : null;
	}

	public ArrayList<ItemStack> getIngredients() {
		final ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		switch (this.type) {
			case TYPE_FURNACE:
				ret.add(this.furnacerecipe.unbox().getInput());
				return ret;
			case TYPE_SHAPED:
				for (final Entry<Character, ItemStack> entry : getIngredientMap().entrySet())
					ret.add(entry.getValue());
				return ret;
			case TYPE_SHAPELESS:
				ret.addAll(this.shapelessRecipe.unbox().getIngredientList());
				return ret;
			default:
				return null;
		}
	}

	public Recipe getRecipe() {
		switch (this.type) {
			case TYPE_FURNACE:
				return this.furnacerecipe.unbox();
			case TYPE_SHAPED:
				return this.shapedRecipe.unbox();
			case TYPE_SHAPELESS:
				return this.shapelessRecipe.unbox();
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
