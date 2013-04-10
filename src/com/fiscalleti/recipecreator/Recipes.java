package com.fiscalleti.recipecreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.fiscalleti.recipecreator.serialization.ObjectHandler;
import com.fiscalleti.recipecreator.serialization.SerializableShapedRecipe;
import com.fiscalleti.recipecreator.serialization.SerializableShapelessRecipe;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;

public class Recipes {
	public static void createShapeless(Player p, String name){
		createRecipes();
		ArrayList<Material> ingred = new ArrayList<Material>();
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		ingred.add((p.getInventory().getItem(9) != null) ? p.getInventory().getItem(9).getType() : null);
		ingred.add((p.getInventory().getItem(10) != null) ? p.getInventory().getItem(10).getType() : null);
		ingred.add((p.getInventory().getItem(11) != null) ? p.getInventory().getItem(11).getType() : null);
		
		ingred.add((p.getInventory().getItem(18) != null) ? p.getInventory().getItem(18).getType() : null);
		ingred.add((p.getInventory().getItem(19) != null) ? p.getInventory().getItem(19).getType() : null);
		ingred.add((p.getInventory().getItem(20) != null) ? p.getInventory().getItem(20).getType() : null);
		
		ingred.add((p.getInventory().getItem(27) != null) ? p.getInventory().getItem(27).getType() : null);
		ingred.add((p.getInventory().getItem(28) != null) ? p.getInventory().getItem(28).getType() : null);
		ingred.add((p.getInventory().getItem(29) != null) ? p.getInventory().getItem(29).getType() : null);
		
		ItemStack out = (p.getInventory().getItem(17) != null) ? p.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);
		
		ShapelessRecipe r = new ShapelessRecipe(out);
		
		boolean empty = true;
		
		for(Material m : ingred){
			if(m != null){
				r.addIngredient(m);
				empty = false;
			}
		}
		
		if(empty || out.getType() == Material.AIR){
			p.sendMessage(ChatColor.RED + "Bad recipe contruction in inventory");
			return;
		}
		
		SerializableShapelessRecipe r1 = new SerializableShapelessRecipe(r);
		SerializedRecipe r2 = new SerializedRecipe(r1, name);
		
		if(recipeExists(r2)){
			p.sendMessage(ChatColor.RED + "A recipe with that name or result already exists");
			return;
		}
		
		try {
			ObjectHandler.write(r2, RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + name + ".rec");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SerializedRecipe r3 = null;
		try {
			r3 = (SerializedRecipe)ObjectHandler.read(RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + name + ".rec");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		RecipeCreator.instance.getServer().addRecipe(r3.getRecipe());
		p.sendMessage(ChatColor.GREEN + "Shapeless recipe '"+r3.getName()+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN + "Shapeless recipe '"+r3.getName()+"' created");
	}
	
	public static void createShaped(Player p, String name){
		createRecipes();
		Material tl = (p.getInventory().getItem(9) != null) ? p.getInventory().getItem(9).getType() : Material.AIR;
		Material tm = (p.getInventory().getItem(10) != null) ? p.getInventory().getItem(10).getType() : Material.AIR;
		Material tr = (p.getInventory().getItem(11) != null) ? p.getInventory().getItem(11).getType() : Material.AIR;
		
		Material ml = (p.getInventory().getItem(18) != null) ? p.getInventory().getItem(18).getType() : Material.AIR;
		Material mm = (p.getInventory().getItem(19) != null) ? p.getInventory().getItem(19).getType() : Material.AIR;
		Material mr = (p.getInventory().getItem(20) != null) ? p.getInventory().getItem(20).getType() : Material.AIR;
		
		Material bl = (p.getInventory().getItem(27) != null) ? p.getInventory().getItem(27).getType() : Material.AIR;
		Material bm = (p.getInventory().getItem(28) != null) ? p.getInventory().getItem(28).getType() : Material.AIR;
		Material br = (p.getInventory().getItem(29) != null) ? p.getInventory().getItem(29).getType() : Material.AIR;
		
		ItemStack out = (p.getInventory().getItem(17) != null) ? p.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);
		
		if((tl == Material.AIR && tm == Material.AIR && tr == Material.AIR && ml == Material.AIR && mm == Material.AIR && mr == Material.AIR && bl == Material.AIR && bm == Material.AIR && br == Material.AIR) || out.getType() == Material.AIR){
			p.sendMessage(ChatColor.RED + "Bad recipe contruction in inventory");
			return;
		}
		
		ShapedRecipe r = new ShapedRecipe(out);
		r.shape(new String[] {"abc", "def", "ghi"});
		
		if(tl != Material.AIR){
			r.setIngredient('a', tl);
		}
		if(tm != Material.AIR){
			r.setIngredient('b', tm);
		}
		if(tr != Material.AIR){
			r.setIngredient('c', tr);
		}
		if(ml != Material.AIR){
			r.setIngredient('d', ml);
		}
		if(mm != Material.AIR){
			r.setIngredient('e', mm);
		}
		if(mr != Material.AIR){
			r.setIngredient('f', mr);
		}
		if(bl != Material.AIR){
			r.setIngredient('g', bl);
		}
		if(bm != Material.AIR){
			r.setIngredient('h', bm);
		}
		if(br != Material.AIR){
			r.setIngredient('i', br);
		}
		
		SerializableShapedRecipe r1 = new SerializableShapedRecipe(r);
		SerializedRecipe r2 = new SerializedRecipe(r1, name);
		
		if(recipeExists(r2)){
			p.sendMessage(ChatColor.RED + "A recipe with that name or result already exists");
			return;
		}
		
		try {
			ObjectHandler.write(r2, RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + name + ".rec");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		SerializedRecipe r3 = null;
		try {
			r3 = (SerializedRecipe)ObjectHandler.read(RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + name + ".rec");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		RecipeCreator.instance.getServer().addRecipe(r3.getRecipe());
		p.sendMessage(ChatColor.GREEN + "Shaped recipe '"+r3.getName()+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN + "Shaped recipe '"+r3.getName()+"' created");
		
	}
	
	public static ArrayList<SerializedRecipe> getRecipes(){
		createRecipes();
		ArrayList<SerializedRecipe> ret = new ArrayList<SerializedRecipe>();
		for(File recipe : new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").listFiles()){
			SerializedRecipe r3 = null;
			try {
				r3 = (SerializedRecipe)ObjectHandler.read(recipe.getPath());
			} catch (Exception e){
				
			}
			ret.add(r3);
		}
		return ret;
	}
	
	public static void loadRecipes(){
		for(SerializedRecipe r : getRecipes()){
			RecipeCreator.instance.getServer().addRecipe(r.getRecipe());
			RecipeCreator.instance.console.sendMessage(ChatColor.GREEN + "[RecipeCreator] Added recipe '" + r.getName() + "'");
		}
	}
	
	public static boolean recipeExists(SerializedRecipe r){
		ArrayList<SerializedRecipe> recs = getRecipes();
		boolean ret = false;
		for(SerializedRecipe rec : recs){
			if(rec.getResult() == r.getResult() || rec.getName().equalsIgnoreCase(r.getName())){
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	public static boolean removeRecipe(String name){
		return new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + name + ".rec").delete();
	}
	
	public static void createRecipes(){
		if(!RecipeCreator.instance.getDataFolder().exists()){
			RecipeCreator.instance.getDataFolder().mkdir();
		}
		if(!new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").exists()){
			new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").mkdir();
		}
	}
}
