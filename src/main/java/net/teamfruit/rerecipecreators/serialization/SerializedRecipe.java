package net.teamfruit.rerecipecreators.serialization;

import java.io.Serializable;
import java.util.Set;

import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.google.common.collect.Sets;

public class SerializedRecipe implements Serializable {
	private static final long serialVersionUID = -996412512091646013L;

	private ShapedRecipe shaped_recipe;
	private ShapelessRecipe shapeless_recipe;
	private FurnaceRecipe furnace_recipe;
	private ItemStack craft_result;
	private Set<String> alias;

	public SerializedRecipe(final ShapedRecipe recipe, final ItemStack craft_result) {
		this.shaped_recipe = recipe;
		this.craft_result = craft_result;
	}

	public SerializedRecipe(final ShapelessRecipe recipe, final ItemStack craft_result) {
		this.shapeless_recipe = recipe;
		this.craft_result = craft_result;
	}

	public SerializedRecipe(final FurnaceRecipe recipe, final ItemStack craft_result) {
		this.furnace_recipe = recipe;
		this.craft_result = craft_result;
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

	public ItemStack getCraftResult() {
		return this.craft_result;
	}

	public Set<String> getAlias() {
		if (this.alias==null)
			this.alias = Sets.newHashSet();
		return this.alias;
	}

	public static SerializedRecipe fromRecipe(final Recipe recipe, final ItemStack craft_result) {
		if (recipe instanceof ShapedRecipe)
			return new SerializedRecipe((ShapedRecipe) recipe, craft_result);
		else if (recipe instanceof ShapelessRecipe)
			return new SerializedRecipe((ShapelessRecipe) recipe, craft_result);
		else if (recipe instanceof FurnaceRecipe)
			return new SerializedRecipe((FurnaceRecipe) recipe, craft_result);
		return null;
	}
}
