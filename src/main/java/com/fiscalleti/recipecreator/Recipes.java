package com.fiscalleti.recipecreator;

import java.util.ArrayList;
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
	public static void createShapeless(final Player player) {
		final ArrayList<ItemStack> ingred = new ArrayList<ItemStack>();
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		ingred.add(player.getInventory().getItem(9)!=null ? player.getInventory().getItem(9) : null);
		ingred.add(player.getInventory().getItem(10)!=null ? player.getInventory().getItem(10) : null);

		ingred.add(player.getInventory().getItem(18)!=null ? player.getInventory().getItem(18) : null);
		ingred.add(player.getInventory().getItem(19)!=null ? player.getInventory().getItem(19) : null);

		final ItemStack out = player.getInventory().getItem(17)!=null ? player.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		final ShapelessRecipe recipe = new ShapelessRecipe(out);

		boolean empty = true;

		for (final ItemStack m : ingred)
			if (m!=null) {
				recipe.addIngredient(m.getData());
				empty = false;
			}

		if (empty||out.getType()==Material.AIR) {
			player.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		RecipeCreator.instance.recipestorage.putRecipe(name, new SerializedRecipe(recipe, name));
		RecipeCreator.instance.reciperegistrar.addRecipe(recipe);

		player.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+name+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+name+"' created");
	}

	private static ItemStack item(final Player player, final int slot) {
		final ItemStack itemStack = player.getInventory().getItem(slot);
		return itemStack!=null ? itemStack : new ItemStack(Material.AIR, 1);
	}

	public static void createShaped(final Player player, final boolean trim) {
		final Map<Character, MaterialData> map = Maps.newHashMap();

		map.put('a', item(player, 9).getData());
		map.put('b', item(player, 10).getData());
		map.put('c', item(player, 11).getData());

		map.put('d', item(player, 18).getData());
		map.put('e', item(player, 19).getData());
		map.put('f', item(player, 20).getData());

		map.put('g', item(player, 27).getData());
		map.put('h', item(player, 28).getData());
		map.put('i', item(player, 29).getData());

		final ItemStack out = item(player, 17);

		boolean allblank = true;
		for (final MaterialData material : map.values())
			if (material.getItemType()!=Material.AIR) {
				allblank = false;
				break;
			}
		if (allblank||out.getType()==Material.AIR) {
			player.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final Map<MaterialData, Character> inverses = Maps.newHashMap();
		for (final Entry<Character, MaterialData> entry : map.entrySet())
			inverses.put(entry.getValue(), entry.getKey());

		final ShapedRecipe recipe = new ShapedRecipe(out);

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

		Character[][] shape = new Character[][] {
				new Character[] { charmap.get('a'), charmap.get('b'), charmap.get('c') },
				new Character[] { charmap.get('d'), charmap.get('e'), charmap.get('f') },
				new Character[] { charmap.get('g'), charmap.get('h'), charmap.get('i') },
		};

		if (trim)
			shape = trimShape(shape);

		final String[] shapestr = new String[shape.length];
		for (int i = 0; i<shape.length; i++)
			shapestr[i] = StringUtils.join(shape[i]);

		recipe.shape(shapestr);

		for (final Entry<Character, MaterialData> ingred : ingreds.entrySet())
			recipe.setIngredient(ingred.getKey(), ingred.getValue());

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		RecipeCreator.instance.recipestorage.putRecipe(name, new SerializedRecipe(recipe, name));
		RecipeCreator.instance.reciperegistrar.addRecipe(recipe);

		player.sendMessage(ChatColor.GREEN+"Shaped recipe '"+name+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Shaped recipe '"+name+"' created");

	}

	private static Character[][] trimShape(final Character[][] shape) {
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
		if (sx==shape.length&&sy==shape.length)
			return shape;
		final Character[][] trimed = new Character[sx][sy];
		for (int i = 0; i<sx; i++)
			for (int j = 0; j<sy; j++)
				trimed[i][j] = shape[i+vx][j+vy];
		return trimed;
	}

	public static void createFurnace(final Player player) {
		ItemStack in = null;
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		in = player.getInventory().getItem(29)!=null ? player.getInventory().getItem(29) : null;

		final ItemStack out = player.getInventory().getItem(17)!=null ? player.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		final FurnaceRecipe recipe = new FurnaceRecipe(out, out.getData());

		if (in==null||out.getType()==Material.AIR) {
			player.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		RecipeCreator.instance.recipestorage.putRecipe(name, new SerializedRecipe(recipe, name));
		RecipeCreator.instance.reciperegistrar.addRecipe(recipe);

		player.sendMessage(ChatColor.GREEN+"Furnace recipe '"+name+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Furnace recipe '"+name+"' created");
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

	public static boolean removeRecipe(final String name, final ChatOutput s) {
		final Recipe remove = RecipeCreator.instance.recipestorage.getRecipe(name).getRecipe();
		if (remove!=null)
			if (RecipeCreator.instance.recipestorage.removeRecipe(name)) {
				RecipeCreator.instance.reciperegistrar.removeRecipe(remove);
				return true;
			}
		return false;
	}
}
