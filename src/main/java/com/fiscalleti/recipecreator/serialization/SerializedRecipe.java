package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

	public RecipeType getType() {
		if (this.shaped_recipe!=null)
			return RecipeType.SHAPED;
		else if (this.shapeless_recipe!=null)
			return RecipeType.SHAPELESS;
		else if (this.furnace_recipe!=null)
			return RecipeType.FURNACE;
		return null;
	}

	public Recipe getRecipe() {
		final RecipeType type = getType();
		if (type!=null)
			switch (type) {
				case SHAPED:
					return this.shaped_recipe;
				case SHAPELESS:
					return this.shapeless_recipe;
				case FURNACE:
					return this.furnace_recipe;
				default:
					break;
			}
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
		return resultEquals(recipe0, recipe1)&&ingredientEquals(recipe0, recipe1);
	}

	public static boolean resultEquals(final SerializedRecipe recipe0, final SerializedRecipe recipe1) {
		if (recipe0==recipe1||recipe0==null&&recipe1==null)
			return true;
		if (recipe0==null||recipe1==null)
			return false;
		final Recipe recipe00 = recipe0.getRecipe();
		final Recipe recipe01 = recipe1.getRecipe();
		if (recipe00==null||recipe01==null)
			return false;
		final ItemStack item0 = recipe00.getResult();
		final ItemStack item1 = recipe01.getResult();
		return item0.equals(item1);
	}

	public static boolean ingredientEquals(final SerializedRecipe recipe0, final SerializedRecipe recipe1) {
		if (recipe0==recipe1||recipe0==null&&recipe1==null)
			return true;
		if (recipe0==null||recipe1==null)
			return false;
		final RecipeType type0 = recipe0.getType();
		final RecipeType type1 = recipe1.getType();
		if (type0==null||type1==null||type0!=type1)
			return false;
		final Recipe recipe00 = recipe0.getRecipe();
		final Recipe recipe01 = recipe1.getRecipe();
		if (recipe00==null||recipe01==null)
			return false;
		switch (type0) {
			case SHAPED:
				final ShapedRecipe recipe10 = (ShapedRecipe) recipe00;
				final ShapedRecipe recipe11 = (ShapedRecipe) recipe01;
				final String[] shape0 = recipe10.getShape();
				final String[] shape1 = recipe11.getShape();
				if (shape0.length!=shape1.length)
					return false;
				final Map<Character, ItemStack> map0 = recipe10.getIngredientMap();
				final Map<Character, ItemStack> map1 = recipe11.getIngredientMap();
				for (int i = 0; i<shape0.length; i++) {
					final char[] shape10 = shape0[i].toCharArray();
					final char[] shape11 = shape1[i].toCharArray();
					if (shape10.length!=shape11.length)
						return false;
					for (int j = 0; j<shape10.length; j++) {
						final ItemStack item10 = map0.get(shape10[j]);
						final ItemStack item11 = map1.get(shape11[j]);
						if (item10.getData().equals(item11.getData()))
							return false;
					}
				}
				return true;
			case SHAPELESS:
				final ShapelessRecipe recipe20 = (ShapelessRecipe) recipe00;
				final ShapelessRecipe recipe21 = (ShapelessRecipe) recipe01;
				final List<ItemStack> list0 = recipe20.getIngredientList();
				final List<ItemStack> list1 = recipe21.getIngredientList();
				final int list0size = list0.size();
				if (list0size!=list1.size())
					return false;
				for (int i = 0; i<list0size; i++) {
					final ItemStack item10 = list0.get(i);
					final ItemStack item11 = list1.get(i);
					if (item10.getData().equals(item11.getData()))
						return false;
				}
				return true;
			case FURNACE:
				final FurnaceRecipe recipe30 = (FurnaceRecipe) recipe00;
				final FurnaceRecipe recipe31 = (FurnaceRecipe) recipe01;
				if (!recipe30.getInput().getData().equals(recipe31.getInput().getData()))
					return false;
				return true;
			default:
				return false;
		}
	}
}
