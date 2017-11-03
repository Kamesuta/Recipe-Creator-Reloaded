package com.fiscalleti.recipecreator;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;

import com.fiscalleti.recipecreator.serialization.SerializedRecipe;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Recipes {
	public static abstract class RecipeBuilder {
		protected final Player player;

		public RecipeBuilder(final Player player) {
			this.player = player;
		}

		protected ItemStack item(final int slot) {
			final ItemStack itemStack = this.player.getInventory().getItem(slot);
			return itemStack!=null ? itemStack : new ItemStack(Material.AIR, 1);
		}

		public abstract Recipe toRecipe();

		public abstract SerializedRecipe toSerializedRecipe();

		public static class ShapelessRecipeBuilder extends RecipeBuilder {
			public ShapelessRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			public ShapelessRecipe toRecipe() {
				final List<MaterialData> ingredients = Lists.newArrayList();

				//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
				ingredients.add(item(9).getData());
				ingredients.add(item(10).getData());
				ingredients.add(item(11).getData());

				ingredients.add(item(18).getData());
				ingredients.add(item(19).getData());
				ingredients.add(item(20).getData());

				ingredients.add(item(27).getData());
				ingredients.add(item(28).getData());
				ingredients.add(item(29).getData());

				final ItemStack out = item(17);

				final ShapelessRecipe recipe = new ShapelessRecipe(out);

				boolean empty = true;
				for (final MaterialData ingredient : ingredients)
					if (ingredient.getItemType()!=Material.AIR) {
						recipe.addIngredient(ingredient);
						empty = false;
					}

				if (empty||out.getType()==Material.AIR)
					return null;

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final ShapelessRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe);
			}
		}

		public static class ShapedRecipeBuilder extends RecipeBuilder {
			public ShapedRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			public ShapedRecipe toRecipe() {
				final Map<Character, MaterialData> map = Maps.newHashMap();

				//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
				map.put('a', item(9).getData());
				map.put('b', item(10).getData());
				map.put('c', item(11).getData());

				map.put('d', item(18).getData());
				map.put('e', item(19).getData());
				map.put('f', item(20).getData());

				map.put('g', item(27).getData());
				map.put('h', item(28).getData());
				map.put('i', item(29).getData());

				final ItemStack out = item(17);

				boolean empty = true;
				for (final MaterialData material : map.values())
					if (material.getItemType()!=Material.AIR) {
						empty = false;
						break;
					}

				if (empty||out.getType()==Material.AIR)
					return null;

				final Map<MaterialData, Character> inverses = Maps.newHashMap();
				for (final Entry<Character, MaterialData> entry : map.entrySet())
					inverses.put(entry.getValue(), entry.getKey());

				final Map<Character, MaterialData> ingreds = Maps.newHashMap();
				final Map<Character, Character> charmap = Maps.newHashMap();
				for (final Entry<Character, MaterialData> entry : map.entrySet()) {
					final Character key = entry.getKey();
					final MaterialData value = entry.getValue();
					if (value.getItemType()!=Material.AIR) {
						final Character ukey = inverses.get(value);
						charmap.put(key, ukey);
						ingreds.put(ukey, value);
					} else
						charmap.put(key, ' ');
				}

				final Character[][] shape = processShape(new Character[][] {
						new Character[] { charmap.get('a'), charmap.get('b'), charmap.get('c') },
						new Character[] { charmap.get('d'), charmap.get('e'), charmap.get('f') },
						new Character[] { charmap.get('g'), charmap.get('h'), charmap.get('i') },
				});

				final ShapedRecipe recipe = new ShapedRecipe(out);

				final String[] shapestr = new String[shape.length];
				for (int i = 0; i<shape.length; i++)
					shapestr[i] = StringUtils.join(shape[i]);

				recipe.shape(shapestr);

				for (final Entry<Character, MaterialData> ingred : ingreds.entrySet())
					recipe.setIngredient(ingred.getKey(), ingred.getValue());

				return recipe;
			}

			@Override
			public final SerializedRecipe toSerializedRecipe() {
				final ShapedRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe);
			}

			protected Character[][] processShape(final Character[][] shape) {
				return shape;
			}
		}

		public static class TrimmedShapedRecipeBuilder extends ShapedRecipeBuilder {

			public TrimmedShapedRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			protected Character[][] processShape(final Character[][] shape) {
				int sx = 0;
				int sy = 0;
				int vx = -1;
				int vy = -1;
				for (int i = 0; i<shape.length; i++)
					for (int j = 0; j<shape[0].length; j++) {
						if (!Character.isWhitespace(shape[i][j])) {
							if (vx<0)
								vx = i;
							sx = Math.max(sx, i+1-vx);
						}
						if (!Character.isWhitespace(shape[j][i])) {
							if (vy<0)
								vy = i;
							sy = Math.max(sy, i+1-vy);
						}
					}
				if (shape.length>0&&sx==shape[0].length&&sy==shape.length)
					return shape;
				final Character[][] trimed = new Character[sx][sy];
				for (int i = 0; i<sx; i++)
					for (int j = 0; j<sy; j++)
						trimed[i][j] = shape[i+vx][j+vy];
				return trimed;
			}
		}

		public static class FurnaceRecipeBuilder extends RecipeBuilder {
			public FurnaceRecipeBuilder(final Player player) {
				super(player);
			}

			@Override
			public FurnaceRecipe toRecipe() {
				final MaterialData in = item(29).getData();
				final ItemStack out = item(17);

				if (in.getItemType()==Material.AIR||out.getType()==Material.AIR) {
					this.player.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
					return null;
				}

				final FurnaceRecipe recipe = new FurnaceRecipe(out, in);

				return recipe;
			}

			@Override
			public SerializedRecipe toSerializedRecipe() {
				final FurnaceRecipe recipe = toRecipe();
				if (recipe==null)
					return null;
				return new SerializedRecipe(recipe);
			}
		}
	}

	public static void loadRecipes(final ChatOutput output) {
		output.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Loading Recipe Files");
		RecipeCreator.instance.recipestorage.load();
		final List<Recipe> recipes = Lists.newArrayList();
		for (final SerializedRecipe r : RecipeCreator.instance.recipestorage.getRecipes().values())
			recipes.add(r.getRecipe());
		RecipeCreator.instance.reciperegistrar.setRecipes(recipes);
		output.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Done Loading Recipe Files! ("+recipes.size()+" Recipes)");
	}

	public static void add(final ChatOutput output, final String name, final SerializedRecipe recipe) {
		if (RecipeCreator.instance.recipestorage.getRecipe(name)!=null) {
			output.sendMessage(ChatColor.RED+"A recipe with that name already exists");
			return;
		}

		RecipeCreator.instance.recipestorage.putRecipe(name, recipe);
		RecipeCreator.instance.reciperegistrar.addRecipe(recipe.getRecipe());

		output.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+name+"' created");
	}

	public static boolean removeRecipe(final ChatOutput output, final String name) {
		final Recipe remove = RecipeCreator.instance.recipestorage.getRecipe(name).getRecipe();
		if (remove!=null)
			if (RecipeCreator.instance.recipestorage.removeRecipe(name)) {
				RecipeCreator.instance.reciperegistrar.removeRecipe(remove);
				return true;
			}
		return false;
	}
}
