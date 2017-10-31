package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

public class SerializableFurnaceRecipe implements Serializable {
	private static final long serialVersionUID = 5024505828440280152L;

	private ItemStack input;
	private ItemStack result;

	public SerializableFurnaceRecipe(final FurnaceRecipe furnaceRecipe) {
		this.input = furnaceRecipe.getInput();
		this.result = furnaceRecipe.getResult();
	}

	public FurnaceRecipe unbox() {
		final FurnaceRecipe r = new FurnaceRecipe(this.result, this.result.getData());
		r.setInput(this.input.getData());
		return r;
	}
}
