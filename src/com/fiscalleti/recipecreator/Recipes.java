package com.fiscalleti.recipecreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.permissions.Permission;

import com.fiscalleti.recipecreator.serialization.ObjectHandler;
import com.fiscalleti.recipecreator.serialization.RecipeInformation;
import com.fiscalleti.recipecreator.serialization.SerializableShapedRecipe;
import com.fiscalleti.recipecreator.serialization.SerializableShapelessRecipe;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;

public class Recipes {
	public static void createShapeless(Player p){
		createRecipesDirectory();
		ArrayList<ItemStack> ingred = new ArrayList<ItemStack>();
		//9, 10, 11, 18, 19, 20, 27, 28, 29 .. 17
		ingred.add((p.getInventory().getItem(9) != null) ? p.getInventory().getItem(9) : null);
		ingred.add((p.getInventory().getItem(10) != null) ? p.getInventory().getItem(10) : null);
		
		ingred.add((p.getInventory().getItem(18) != null) ? p.getInventory().getItem(18) : null);
		ingred.add((p.getInventory().getItem(19) != null) ? p.getInventory().getItem(19) : null);
		
		ItemStack out = (p.getInventory().getItem(17) != null) ? p.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);
		
		ShapelessRecipe r = new ShapelessRecipe(out);
		
		boolean empty = true;
		
		for(ItemStack m : ingred){
			if(m != null){
				r.addIngredient(m.getData());
				empty = false;
			}
		}
		
		if(empty || out.getType() == Material.AIR){
			p.sendMessage(ChatColor.RED + "Bad recipe contruction in inventory");
			return;
		}
		
		String name = String.valueOf(getRecipes().size());
		
		SerializableShapelessRecipe r1 = new SerializableShapelessRecipe(r);
		SerializedRecipe r2 = new SerializedRecipe(r1, name);
		
		regenerateRecipes(new CommandSender[] {p, RecipeCreator.instance.console});
		
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
		p.sendMessage(ChatColor.GREEN + "Shapeless recipe '"+r3.getId()+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN + "Shapeless recipe '"+r3.getId()+"' created");
	}
	
	public static void createShaped(Player p){
		createRecipesDirectory();
		ItemStack tl = (p.getInventory().getItem(9) != null) ? p.getInventory().getItem(9) : new ItemStack(Material.AIR, 1);
		ItemStack tm = (p.getInventory().getItem(10) != null) ? p.getInventory().getItem(10) : new ItemStack(Material.AIR, 1);
		ItemStack tr = (p.getInventory().getItem(11) != null) ? p.getInventory().getItem(11) : new ItemStack(Material.AIR, 1);
		
		ItemStack ml = (p.getInventory().getItem(18) != null) ? p.getInventory().getItem(18) : new ItemStack(Material.AIR, 1);
		ItemStack mm = (p.getInventory().getItem(19) != null) ? p.getInventory().getItem(19) : new ItemStack(Material.AIR, 1);
		ItemStack mr = (p.getInventory().getItem(20) != null) ? p.getInventory().getItem(20) : new ItemStack(Material.AIR, 1);
		
		ItemStack bl = (p.getInventory().getItem(27) != null) ? p.getInventory().getItem(27) : new ItemStack(Material.AIR, 1);
		ItemStack bm = (p.getInventory().getItem(28) != null) ? p.getInventory().getItem(28) : new ItemStack(Material.AIR, 1);
		ItemStack br = (p.getInventory().getItem(29) != null) ? p.getInventory().getItem(29) : new ItemStack(Material.AIR, 1);
		
		ItemStack out = (p.getInventory().getItem(17) != null) ? p.getInventory().getItem(17) : new ItemStack(Material.AIR, 1);
		
		if((tl.getType() == Material.AIR && tm.getType() == Material.AIR && tr.getType() == Material.AIR && ml.getType() == Material.AIR && mm.getType() == Material.AIR && mr.getType() == Material.AIR && bl.getType() == Material.AIR && bm.getType() == Material.AIR && br.getType() == Material.AIR) || out.getType() == Material.AIR){
			p.sendMessage(ChatColor.RED + "Bad recipe contruction in inventory");
			return;
		}
		
		ShapedRecipe r = new ShapedRecipe(out);
		r.shape(new String[] {"abc", "def", "ghi"});
		
		if(tl.getType() != Material.AIR){
			r.setIngredient('a', tl.getData());
		}
		if(tm.getType() != Material.AIR){
			r.setIngredient('b', tm.getData());
		}
		if(tr.getType() != Material.AIR){
			r.setIngredient('c', tr.getData());
		}
		if(ml.getType() != Material.AIR){
			r.setIngredient('d', ml.getData());
		}
		if(mm.getType() != Material.AIR){
			r.setIngredient('e', mm.getData());
		}
		if(mr.getType() != Material.AIR){
			r.setIngredient('f', mr.getData());
		}
		if(bl.getType() != Material.AIR){
			r.setIngredient('g', bl.getData());
		}
		if(bm.getType() != Material.AIR){
			r.setIngredient('h', bm.getData());
		}
		if(br.getType() != Material.AIR){
			r.setIngredient('i', br.getData());
		}
		
		String name = String.valueOf(getRecipes().size());
		
		SerializableShapedRecipe r1 = new SerializableShapedRecipe(r);
		SerializedRecipe r2 = new SerializedRecipe(r1, name);
		
		regenerateRecipes(new CommandSender[] {p, RecipeCreator.instance.console});
		
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
		p.sendMessage(ChatColor.GREEN + "Shaped recipe '"+r3.getId()+"' created");
		RecipeCreator.instance.console.sendMessage(ChatColor.GREEN + "Shaped recipe '"+r3.getId()+"' created");
		
	}
	
	public static ArrayList<SerializedRecipe> getRecipes(){
		createRecipesDirectory();
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
	
	public static SerializedRecipe getRecipe(int recipeid){
		
		SerializedRecipe ret = null;
	
			try {
				ret = (SerializedRecipe)ObjectHandler.read(RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + String.valueOf(recipeid) + ".rec");
			} catch (Exception e){
				
			}
			
			
		return ret;
	
	}
	
	public static SerializedRecipe getRecipe(Recipe r){
		SerializedRecipe ret = null;
		if(r instanceof ShapedRecipe){
			ShapedRecipe r2 = (ShapedRecipe)r;
			for(SerializedRecipe r1 : getRecipes()){
				SerializedRecipe r3 = new SerializedRecipe(new SerializableShapedRecipe(r2), r1.getId());
				if(r1 == r3){
					ret = r1;
					break;
				}
			}
		}else if(r instanceof ShapelessRecipe){
			ShapelessRecipe r2 = (ShapelessRecipe)r;
			
			for(SerializedRecipe r1 : getRecipes()){
				SerializedRecipe r3 = new SerializedRecipe(new SerializableShapelessRecipe(r2), r1.getId());
				if(r1 == r3){
					ret = r1;
					break;
				}
			}
		}
		return ret;
		
	}
	
	public static void loadRecipes(CommandSender[] c){
		for(CommandSender cs : c){
			cs.sendMessage(ChatColor.YELLOW + "[RecipeCreator] Loading Recipe Files");
		}
		int count = 0;
		for(SerializedRecipe r : getRecipes()){
			RecipeCreator.instance.getServer().addRecipe(r.getRecipe());
			count ++;
		}
		for(CommandSender cs : c){
			cs.sendMessage(ChatColor.YELLOW + "[RecipeCreator] Done Loading Recipe Files! (" + count + " Recipes)");
		}
	}
	
	public static boolean recipeExists(SerializedRecipe r){
		ArrayList<SerializedRecipe> recs = getRecipes();
		boolean ret = false;
		for(SerializedRecipe rec : recs){
			if(rec.getId().equalsIgnoreCase(r.getId())){
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	public static boolean recipeExists(String name){
		ArrayList<SerializedRecipe> recs = getRecipes();
		boolean ret = false;
		for(SerializedRecipe rec : recs){
			if(rec.getId().equalsIgnoreCase(name)){
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	public static boolean removeRecipe(String name, CommandSender[] s){
		boolean removed = new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + name + ".rec").delete();
		RecipeCreator.instance.getServer().clearRecipes();
		regenerateRecipes(s);
		loadRecipes(s);
		return removed;
	}
	
	public static ArrayList<Recipe> getDefaultBukkitRecipes(){
		OutputStream real = System.out;
		System.setOut(new PrintStream(new NullOutputStream()));
		RecipeCreator.instance.getServer().resetRecipes();
		System.setOut(new PrintStream(real));
		ArrayList<Recipe> recipes = new ArrayList<Recipe>();
		Iterator<Recipe> recipes_i = RecipeCreator.instance.getServer().recipeIterator();
		while(recipes_i.hasNext()){
			Recipe r = recipes_i.next();
			recipes.add(r);
		}
		return recipes;
	}
	
	public static void generateRecipes(CommandSender[] c){
		for(CommandSender cs : c){
			cs.sendMessage(ChatColor.YELLOW + "[RecipeCreator] Generating Recipe Files");
		}
		ArrayList<Recipe> defaultbukkit = getDefaultBukkitRecipes();
		for(Recipe r : defaultbukkit){
			
			if(r instanceof ShapedRecipe){
				SerializableShapedRecipe ssr1 = new SerializableShapedRecipe((ShapedRecipe)r);
				SerializedRecipe sr1 = new SerializedRecipe(ssr1, String.valueOf(defaultbukkit.indexOf(r)));
				if(!recipeExists(sr1)){
					try {
						ObjectHandler.write(sr1, RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + sr1.getId() + ".rec");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}else if(r instanceof ShapelessRecipe){
				SerializableShapelessRecipe ssr1 = new SerializableShapelessRecipe((ShapelessRecipe)r);
				SerializedRecipe sr1 = new SerializedRecipe(ssr1,  String.valueOf(defaultbukkit.indexOf(r)));
				if(!recipeExists(sr1)){
					try {
						ObjectHandler.write(sr1, RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + sr1.getId() + ".rec");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		try {
			ObjectHandler.write(new RecipeInformation(), RecipeCreator.instance.getDataFolder() + File.separator + "generated.dat");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(CommandSender cs : c){
			cs.sendMessage(ChatColor.YELLOW + "[RecipeCreator] Done Generating Recipe Files!");
		}
	}
	
	public static void regenerateRecipes(CommandSender[] s){
		for(CommandSender sen : s){
			sen.sendMessage(ChatColor.YELLOW + "[Recipe Creator] Regenerating Recipes..");
		}
		
		ArrayList<SerializedRecipe> recs = getRecipes();
		ArrayList<SerializedRecipe> write = new ArrayList<SerializedRecipe>();
		int i = 0;
		int count = countRecipes();
		
		deleteAllRecipes();
		
		for(i=0;i<count;i++){
			SerializedRecipe rec = recs.get(i);
			rec.setId(String.valueOf(i));
			if(!recipeExists(rec)){
				try {
					ObjectHandler.write(rec, RecipeCreator.instance.getDataFolder() + File.separator + "recipes" + File.separator + rec.getId() + ".rec");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		for(CommandSender sen : s){
			sen.sendMessage(ChatColor.YELLOW + "[Recipe Creator] Done Regenerating Recipes!");
		}
	}
	
	public static void resetAllRecipes(CommandSender[] cs){
		deleteAllRecipes();
		generateRecipes(cs);
		loadRecipes(cs);
	}
	
	public static void deleteAllRecipes(){
		if(!new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").exists()){
			return;
		}
		for(File f : new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").listFiles()){
			f.delete();
		}
	}
	public static int countRecipes(){
		return new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").listFiles().length;
	}
	
	public static boolean defaultsGenerated(){
		boolean ret = false;
		if(!new File(RecipeCreator.instance.getDataFolder() + File.separator + "generated.dat").exists()){
			return false;
		}
		try {
			ret = ((RecipeInformation)ObjectHandler.read(RecipeCreator.instance.getDataFolder() + File.separator + "generated.dat")).isGenerated();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static int getRecipeVersion(){
		int ret = 0;
		if(!new File(RecipeCreator.instance.getDataFolder() + File.separator + "generated.dat").exists()){
			return 0;
		}
		try {
			ret = ((RecipeInformation)ObjectHandler.read(RecipeCreator.instance.getDataFolder() + File.separator + "generated.dat")).getVersion();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static void createRecipesDirectory(){
		if(!RecipeCreator.instance.getDataFolder().exists()){
			RecipeCreator.instance.getDataFolder().mkdir();
		}
		if(!new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").exists()){
			new File(RecipeCreator.instance.getDataFolder() + File.separator + "recipes").mkdir();
		}
	}
	
	private static class NullOutputStream extends OutputStream {
	    @Override
	    public void write(int b){
	         return;
	    }
	    @Override
	    public void write(byte[] b){
	         return;
	    }
	    @Override
	    public void write(byte[] b, int off, int len){
	         return;
	    }
	}
}
