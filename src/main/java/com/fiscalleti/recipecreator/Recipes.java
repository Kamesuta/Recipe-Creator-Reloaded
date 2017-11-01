package com.fiscalleti.recipecreator;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.fiscalleti.recipecreator.serialization.SerializedRecipe;

public class Recipes {
	public static void createShapeless(final Player player) {
		final ArrayList<ItemStack> ingred = new ArrayList<ItemStack>();
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		ingred.add(player.getInventory().getItem(9)!=null ? player.getInventory().getItem(9) : null);
		ingred.add(player.getInventory().getItem(10)!=null ? player.getInventory().getItem(10) : null);

		ingred.add(player.getInventory().getItem(18)!=null ? player.getInventory().getItem(18) : null);
		ingred.add(player.getInventory().getItem(19)!=null ? player.getInventory().getItem(19) : null);

		final ItemStack out = player.getInventory().getItem(17)!=null ? player.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		final ShapelessRecipe r = new ShapelessRecipe(out);

		boolean empty = true;

		for (final ItemStack m : ingred)
			if (m!=null) {
				r.addIngredient(m.getData());
				empty = false;
			}

		if (empty||out.getType()==Material.AIR) {
			player.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		final SerializedRecipe r2 = new SerializedRecipe(r, name);

		RecipeCreator.instance.recipestorage.putRecipe(name, r2);

		RecipeCreator.instance.getServer().addRecipe(r2.getRecipe());
		player.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+name+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+name+"' created");
	}

	public static void createShaped(final Player player) {
		final ItemStack tl = player.getInventory().getItem(9)!=null ? player.getInventory().getItem(9) : new ItemStack(Material.AIR, 1);
		final ItemStack tm = player.getInventory().getItem(10)!=null ? player.getInventory().getItem(10) : new ItemStack(Material.AIR, 1);
		final ItemStack tr = player.getInventory().getItem(11)!=null ? player.getInventory().getItem(11) : new ItemStack(Material.AIR, 1);

		final ItemStack ml = player.getInventory().getItem(18)!=null ? player.getInventory().getItem(18) : new ItemStack(Material.AIR, 1);
		final ItemStack mm = player.getInventory().getItem(19)!=null ? player.getInventory().getItem(19) : new ItemStack(Material.AIR, 1);
		final ItemStack mr = player.getInventory().getItem(20)!=null ? player.getInventory().getItem(20) : new ItemStack(Material.AIR, 1);

		final ItemStack bl = player.getInventory().getItem(27)!=null ? player.getInventory().getItem(27) : new ItemStack(Material.AIR, 1);
		final ItemStack bm = player.getInventory().getItem(28)!=null ? player.getInventory().getItem(28) : new ItemStack(Material.AIR, 1);
		final ItemStack br = player.getInventory().getItem(29)!=null ? player.getInventory().getItem(29) : new ItemStack(Material.AIR, 1);

		final ItemStack out = player.getInventory().getItem(17)!=null ? player.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		if (tl.getType()==Material.AIR&&tm.getType()==Material.AIR&&tr.getType()==Material.AIR&&ml.getType()==Material.AIR&&mm.getType()==Material.AIR&&mr.getType()==Material.AIR&&bl.getType()==Material.AIR&&bm.getType()==Material.AIR&&br.getType()==Material.AIR||out.getType()==Material.AIR) {
			player.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final ShapedRecipe r = new ShapedRecipe(out);
		r.shape(new String[] { "abc", "def", "ghi" });

		if (tl.getType()!=Material.AIR)
			r.setIngredient('a', tl.getData());
		if (tm.getType()!=Material.AIR)
			r.setIngredient('b', tm.getData());
		if (tr.getType()!=Material.AIR)
			r.setIngredient('c', tr.getData());
		if (ml.getType()!=Material.AIR)
			r.setIngredient('d', ml.getData());
		if (mm.getType()!=Material.AIR)
			r.setIngredient('e', mm.getData());
		if (mr.getType()!=Material.AIR)
			r.setIngredient('f', mr.getData());
		if (bl.getType()!=Material.AIR)
			r.setIngredient('g', bl.getData());
		if (bm.getType()!=Material.AIR)
			r.setIngredient('h', bm.getData());
		if (br.getType()!=Material.AIR)
			r.setIngredient('i', br.getData());

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		final SerializedRecipe r2 = new SerializedRecipe(r, name);

		RecipeCreator.instance.recipestorage.putRecipe(name, r2);

		RecipeCreator.instance.getServer().addRecipe(r2.getRecipe());
		player.sendMessage(ChatColor.GREEN+"Shaped recipe '"+name+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Shaped recipe '"+name+"' created");

	}

	public static void createFurnace(final Player player) {
		ItemStack in = null;
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		in = player.getInventory().getItem(29)!=null ? player.getInventory().getItem(29) : null;

		final ItemStack out = player.getInventory().getItem(17)!=null ? player.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		final FurnaceRecipe r = new FurnaceRecipe(out, out.getData());

		if (in==null||out.getType()==Material.AIR) {
			player.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		final SerializedRecipe r2 = new SerializedRecipe(r, name);

		RecipeCreator.instance.recipestorage.putRecipe(name, r2);

		RecipeCreator.instance.getServer().addRecipe(r2.getRecipe());
		player.sendMessage(ChatColor.GREEN+"Furnace recipe '"+name+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Furnace recipe '"+name+"' created");
	}

	public static void loadRecipes(final ChatOutput output) {
		output.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Loading Recipe Files");
		int count = 0;
		for (final SerializedRecipe r : RecipeCreator.instance.recipestorage.getRecipes().values()) {
			RecipeCreator.instance.getServer().addRecipe(r.getRecipe());
			count++;
		}
		output.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Done Loading Recipe Files! ("+count+" Recipes)");
	}

	public static boolean removeRecipe(final String name, final ChatOutput s) {
		return false;
		// TODO
	}
}
