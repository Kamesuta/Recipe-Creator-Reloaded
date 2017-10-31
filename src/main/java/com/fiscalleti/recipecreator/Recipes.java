package com.fiscalleti.recipecreator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.fiscalleti.recipecreator.serialization.ObjectHandler;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;
import com.google.gson.JsonIOException;

public class Recipes {
	public static void createShapeless(final Player p) {
		final ArrayList<ItemStack> ingred = new ArrayList<ItemStack>();
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		ingred.add(p.getInventory().getItem(9)!=null ? p.getInventory().getItem(9) : null);
		ingred.add(p.getInventory().getItem(10)!=null ? p.getInventory().getItem(10) : null);

		ingred.add(p.getInventory().getItem(18)!=null ? p.getInventory().getItem(18) : null);
		ingred.add(p.getInventory().getItem(19)!=null ? p.getInventory().getItem(19) : null);

		final ItemStack out = p.getInventory().getItem(17)!=null ? p.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		final ShapelessRecipe r = new ShapelessRecipe(out);

		boolean empty = true;

		for (final ItemStack m : ingred)
			if (m!=null) {
				r.addIngredient(m.getData());
				empty = false;
			}

		if (empty||out.getType()==Material.AIR) {
			p.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		final SerializedRecipe r2 = new SerializedRecipe(r, name);

		regenerateRecipes(new CommandSender[] { p, RecipeCreator.instance.console });

		if (recipeExists(r2)) {
			p.sendMessage(ChatColor.RED+"A recipe with that name or result already exists");
			return;
		}

		RecipeCreator.instance.recipestorage.putRecipe(name, r2);

		RecipeCreator.instance.getServer().addRecipe(r2.getRecipe());
		p.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+r2.getId()+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Shapeless recipe '"+r2.getId()+"' created");
	}

	public static void createShaped(final Player p) {
		final ItemStack tl = p.getInventory().getItem(9)!=null ? p.getInventory().getItem(9) : new ItemStack(Material.AIR, 1);
		final ItemStack tm = p.getInventory().getItem(10)!=null ? p.getInventory().getItem(10) : new ItemStack(Material.AIR, 1);
		final ItemStack tr = p.getInventory().getItem(11)!=null ? p.getInventory().getItem(11) : new ItemStack(Material.AIR, 1);

		final ItemStack ml = p.getInventory().getItem(18)!=null ? p.getInventory().getItem(18) : new ItemStack(Material.AIR, 1);
		final ItemStack mm = p.getInventory().getItem(19)!=null ? p.getInventory().getItem(19) : new ItemStack(Material.AIR, 1);
		final ItemStack mr = p.getInventory().getItem(20)!=null ? p.getInventory().getItem(20) : new ItemStack(Material.AIR, 1);

		final ItemStack bl = p.getInventory().getItem(27)!=null ? p.getInventory().getItem(27) : new ItemStack(Material.AIR, 1);
		final ItemStack bm = p.getInventory().getItem(28)!=null ? p.getInventory().getItem(28) : new ItemStack(Material.AIR, 1);
		final ItemStack br = p.getInventory().getItem(29)!=null ? p.getInventory().getItem(29) : new ItemStack(Material.AIR, 1);

		final ItemStack out = p.getInventory().getItem(17)!=null ? p.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		if (tl.getType()==Material.AIR&&tm.getType()==Material.AIR&&tr.getType()==Material.AIR&&ml.getType()==Material.AIR&&mm.getType()==Material.AIR&&mr.getType()==Material.AIR&&bl.getType()==Material.AIR&&bm.getType()==Material.AIR&&br.getType()==Material.AIR||out.getType()==Material.AIR) {
			p.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
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

		regenerateRecipes(new CommandSender[] { p, RecipeCreator.instance.console });

		if (recipeExists(r2)) {
			p.sendMessage(ChatColor.RED+"A recipe with that name or result already exists");
			return;
		}

		RecipeCreator.instance.recipestorage.putRecipe(name, r2);

		RecipeCreator.instance.getServer().addRecipe(r2.getRecipe());
		p.sendMessage(ChatColor.GREEN+"Shaped recipe '"+r2.getId()+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Shaped recipe '"+r2.getId()+"' created");

	}

	public static void createFurnace(final Player p) {
		ItemStack in = null;
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		in = p.getInventory().getItem(29)!=null ? p.getInventory().getItem(29) : null;

		final ItemStack out = p.getInventory().getItem(17)!=null ? p.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);

		final FurnaceRecipe r = new FurnaceRecipe(out, out.getData());

		if (in==null||out.getType()==Material.AIR) {
			p.sendMessage(ChatColor.RED+"Bad recipe contruction in inventory");
			return;
		}

		final String name = String.valueOf(RecipeCreator.instance.recipestorage.getRecipes().size());

		final SerializedRecipe r2 = new SerializedRecipe(r, name);

		regenerateRecipes(new CommandSender[] { p, RecipeCreator.instance.console });

		if (recipeExists(r2)) {
			p.sendMessage(ChatColor.RED+"A recipe with that name or result already exists");
			return;
		}

		RecipeCreator.instance.recipestorage.putRecipe(name, r2);

		RecipeCreator.instance.getServer().addRecipe(r2.getRecipe());
		p.sendMessage(ChatColor.GREEN+"Furnace recipe '"+r2.getId()+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN+"Furnace recipe '"+r2.getId()+"' created");
	}

	public static void loadRecipes(final CommandSender[] c) {
		for (final CommandSender cs : c)
			cs.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Loading Recipe Files");
		int count = 0;
		for (final SerializedRecipe r : RecipeCreator.instance.recipestorage.getRecipes()) {
			RecipeCreator.instance.getServer().addRecipe(r.getRecipe());
			count++;
		}
		for (final CommandSender cs : c)
			cs.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Done Loading Recipe Files! ("+count+" Recipes)");
	}

	public static boolean recipeExists(final SerializedRecipe r) {
		final List<SerializedRecipe> recs = RecipeCreator.instance.recipestorage.getRecipes();
		boolean ret = false;
		for (final SerializedRecipe rec : recs)
			if (rec.getId().equalsIgnoreCase(r.getId())) {
				ret = true;
				break;
			}
		return ret;
	}

	public static boolean recipeExists(final String name) {
		final List<SerializedRecipe> recs = RecipeCreator.instance.recipestorage.getRecipes();
		boolean ret = false;
		for (final SerializedRecipe rec : recs)
			if (rec.getId().equalsIgnoreCase(name)) {
				ret = true;
				break;
			}
		return ret;
	}

	public static boolean removeRecipe(final String name, final CommandSender[] s) {
		final boolean removed = new File(RecipeCreator.instance.getDataFolder()+File.separator+"recipes"+File.separator+name+".rec").delete();
		RecipeCreator.instance.getServer().clearRecipes();
		regenerateRecipes(s);
		loadRecipes(s);
		return removed;
	}

	public static void generateRecipes(final CommandSender[] c) {
		for (final CommandSender cs : c)
			cs.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Generating Recipe Files");
		for (final CommandSender cs : c)
			cs.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Done Generating Recipe Files!");
	}

	public static void regenerateRecipes(final CommandSender[] s) {
		for (final CommandSender sen : s)
			sen.sendMessage(ChatColor.YELLOW+"[Recipe Creator] Regenerating Recipes..");

		final List<SerializedRecipe> recs = RecipeCreator.instance.recipestorage.getRecipes();

		for (final ListIterator<SerializedRecipe> itr = recs.listIterator(); itr.hasNext();) {
			final int index = itr.nextIndex();
			final SerializedRecipe rec = itr.next();
			rec.setId(String.valueOf(index));
			if (!recipeExists(rec))
				try {
					ObjectHandler.write(new File(new File(RecipeCreator.instance.getDataFolder(), "recipes"), index+".rec"), SerializedRecipe.class, rec);
				} catch (final IOException e) {
					e.printStackTrace();
				} catch (final JsonIOException e) {
					e.printStackTrace();
				}
		}

		for (final CommandSender sen : s)
			sen.sendMessage(ChatColor.YELLOW+"[Recipe Creator] Done Regenerating Recipes!");
	}

	public static void resetAllRecipes(final CommandSender[] cs) {
		generateRecipes(cs);
		loadRecipes(cs);
	}
}
