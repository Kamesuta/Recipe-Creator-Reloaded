package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import guava10.com.google.common.collect.Lists;

public class SerializableShapelessRecipe implements Serializable {
	private static final long serialVersionUID = -3375750271937486423L;

	private ItemStack result;
	private List<ItemStack> ingredients = Lists.newArrayList();

	public SerializableShapelessRecipe(final ShapelessRecipe r) {
		this.result = new ItemStack(r.getResult());
		for (final ItemStack itemStack : r.getIngredientList())
			this.ingredients.add(itemStack);
	}

	public ShapelessRecipe unbox() {
		final ShapelessRecipe shapelessRecipe = new ShapelessRecipe(this.result);
		for (final ItemStack ingredient : this.ingredients)
			shapelessRecipe.addIngredient(ingredient.getData());
		return shapelessRecipe;
	}
}
