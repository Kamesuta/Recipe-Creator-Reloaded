package com.fiscalleti.recipecreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

	public static void createShaped(final Player player) {
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

		recipe.shape(new String[] {
				""+charmap.get('a')+charmap.get('b')+charmap.get('c'),
				""+charmap.get('d')+charmap.get('e')+charmap.get('f'),
				""+charmap.get('g')+charmap.get('h')+charmap.get('i'),
		});

		for (final Entry<Character, MaterialData> ingred : ingreds.entrySet())
			recipe.setIngredient(ingred.getKey(), ingred.getValue());

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		RecipeCreator.instance.recipestorage.putRecipe(name, new SerializedRecipe(recipe, name));
		RecipeCreator.instance.reciperegistrar.addRecipe(recipe);

		player.sendMessage(ChatColor.GREEN+"Shaped recipe '"+name+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Shaped recipe '"+name+"' created");

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
