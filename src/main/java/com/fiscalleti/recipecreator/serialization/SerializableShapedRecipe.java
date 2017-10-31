package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import guava10.com.google.common.collect.Maps;

public class SerializableShapedRecipe implements Serializable {
	private static final long serialVersionUID = 3801078178397385670L;
	private String[] shape;
	private Map<Character, ItemStack> ingredients = Maps.newHashMap();
	private ItemStack result;

	public SerializableShapedRecipe(final ShapedRecipe recipe) {
		this.shape = recipe.getShape();
		for (final Entry<Character, ItemStack> entry : recipe.getIngredientMap().entrySet()) {
			final ItemStack ingredient = entry.getValue();
			if (ingredient!=null) {
				final Character key = entry.getKey();
				this.ingredients.put(key, ingredient);
			}
		}

		this.result = recipe.getResult();
	}

	public ShapedRecipe unbox() {
		final ShapedRecipe recipe = new ShapedRecipe(this.result);
		recipe.shape(this.shape);
		for (final Character key : this.ingredients.keySet())
			recipe.setIngredient(key, this.ingredients.get(key).getData());
		return recipe;
	}
}
