package com.fiscalleti.recipecreator;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.bukkit.plugin.java.JavaPlugin;

import com.fiscalleti.recipecreator.serialization.RecipeInformation;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;




public class RecipeCreator extends JavaPlugin{

	public static RecipeCreator instance;
	public ConsoleCommandSender console;
	public Events events;
	@Override
	public void onEnable(){
		instance = this;
		console = getServer().getConsoleSender();
		events = new Events();
		getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(events, this);
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}
		getServer().clearRecipes();
		if(!Recipes.defaultsGenerated() || Recipes.getRecipeVersion() != new RecipeInformation().getVersion()){
			Recipes.deleteAllRecipes();
			Recipes.generateRecipes(new CommandSender[] {console});
		}
		Recipes.loadRecipes(new CommandSender[] {console});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){

		if(commandLabel.equalsIgnoreCase("recipe")){
			if(args.length < 1){
				// /recipe
				sender.sendMessage(ChatColor.YELLOW + "--------- " + ChatColor.GREEN + "Recipe Creator" + ChatColor.YELLOW + " ---------");
				sender.sendMessage(ChatColor.GREEN + getDescription().getDescription());
				sender.sendMessage(ChatColor.YELLOW + "Author: " + ChatColor.GREEN + getDescription().getAuthors().get(0));
				sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.GREEN + getDescription().getVersion());

				return true;
			}
			if(args[0].equalsIgnoreCase("help")){
				// /recipe help
				ConcurrentHashMap<String, Boolean> permissable = new ConcurrentHashMap<String, Boolean>();
				permissable.put("Add", (sender instanceof Player) ? (((Player)sender).hasPermission("recipecreator.add.shaped") || ((Player)sender).hasPermission("recipecreator.add.shapeless")) : true);
				permissable.put("Remove", (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.remove") : true);
				permissable.put("Info", (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.info") : true);
				permissable.put("Lookup", (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.lookup") : true);
				permissable.put("Reset", (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.reset") : true);
				permissable.put("Reload", (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.reload") : true);
				permissable.put("Perms", (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.permissioncontrol") : true);
				sender.sendMessage(ChatColor.YELLOW + "--------- " + ChatColor.GREEN + "Recipe Creator" + ChatColor.YELLOW + " ---------");
				sender.sendMessage(ChatColor.GRAY + "Recipe Creator Commands");
				if(permissable.get("Add")){
					sender.sendMessage(ChatColor.GOLD + "/recipe add [shaped/shapeless]: " + ChatColor.WHITE + "Addes a recipe to the game");
				}
				if(permissable.get("Remove")){
					sender.sendMessage(ChatColor.GOLD + "/recipe remove <recipe-id / ALL>: " + ChatColor.WHITE + "Removes a recipe from the game");
				}
				if(permissable.get("Info")){
					sender.sendMessage(ChatColor.GOLD + "/recipe info [recipe-id]: " + ChatColor.WHITE + "Retrieves a recipes information");
				}
				if(permissable.get("Lookup")){
					sender.sendMessage(ChatColor.GOLD + "/recipe lookup <ITEM_NAME>: " + ChatColor.WHITE + "Retrieves an items Recipe ID's");
				}
				if(permissable.get("Reset")){
					sender.sendMessage(ChatColor.GOLD + "/recipe reset: " + ChatColor.WHITE + "Resets the recipes to default");
				}
				if(permissable.get("Reload")){
					sender.sendMessage(ChatColor.GOLD + "/recipe reload: " + ChatColor.WHITE + "Reloads the recipes");
				}
				if(permissable.get("Perms")){
					sender.sendMessage(ChatColor.GOLD + "/recipe permissions <enable/disable>: " + ChatColor.WHITE + "Enabled or disables crafting permissions");
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("add")){
				if(!(sender instanceof Player)){
					sender.sendMessage(ChatColor.RED + "That command must be used in game");
					return true;
				}
				Player player = (Player)sender;
				String type = "shaped";
				if((args.length > 1)){
					type = args[1];
				}

				if(!(type.equalsIgnoreCase("shaped") || type.equalsIgnoreCase("shapeless"))){
					player.sendMessage(ChatColor.RED + "Usage: /recipe add [shaped/shapeless]");
					return true;
				}

				if(type.equalsIgnoreCase("shapeless")){
					if(!player.hasPermission("recipecreator.add.shapeless")){
						player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						return true;
					}
					Recipes.createShapeless(player);
					return true;
				}

				if(type.equalsIgnoreCase("shaped")){
					if(!player.hasPermission("recipecreator.add.shaped")){
						player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
						return true;
					}
					Recipes.createShaped(player);
					return true;
				}
				return true;
			}

			if(args[0].equalsIgnoreCase("remove")){
				boolean hasperm = (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.remove") : true;

				if(!hasperm){
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}


				if(!(args.length > 1)){
					sender.sendMessage(ChatColor.RED + "Usage: /recipe remove <recipe-id / ALL>");
					return true;
				}

				if(args[1].equalsIgnoreCase("ALL")){
					sender.sendMessage(ChatColor.RED + "=== REMOVING ALL RECIPES FROM SYSTEM ===");
					Recipes.deleteAllRecipes();
					RecipeCreator.instance.getServer().clearRecipes();
					Recipes.loadRecipes(new CommandSender[] {console, sender});
					sender.sendMessage(ChatColor.RED + "=== ALL RECIPES REMOVED ===");
					return true;
				}

				if(Recipes.removeRecipe(args[1], new CommandSender[] {sender, console})){
					sender.sendMessage(ChatColor.YELLOW + "Recipe removed!");
					return true;
				}else{
					sender.sendMessage(ChatColor.RED + "There was an error while removing that recipe. Does it exist?");
					return true;
				}

			}

			if(args[0].equalsIgnoreCase("lookup")){
				boolean hasperm = (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.lookup") : true;

				if(!hasperm){
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}

				if(!(args.length > 1)){
					sender.sendMessage(ChatColor.RED + "Usage: /recipe lookup <BLOCK_NAME>");
					return true;
				}

				ArrayList<SerializedRecipe> recs = Recipes.getRecipes();
				ArrayList<SerializedRecipe> rets = new ArrayList<SerializedRecipe>();
				for(SerializedRecipe r : recs){//System.out.println(r.getResult().getData().getItemType().name());
					if(r.getResult().getData().getItemType().name().equalsIgnoreCase(args[1]) || args[1].equalsIgnoreCase(String.valueOf(r.getResult().getTypeId())) || args[1].equalsIgnoreCase(String.valueOf(r.getResult().getTypeId()) + ":" + Byte.toString(r.getResult().getData().getData()))){
						rets.add(r);
					}
				}

				if(rets.size() < 1){
					sender.sendMessage(ChatColor.RED + "No recipes found with result '"+args[1]+"'");
					return true;
				}

				String ids = "";

				for(SerializedRecipe r : rets){
					ids = (ids.equalsIgnoreCase("")) ? r.getId() : ids + ", " + r.getId();
				}

				sender.sendMessage(ChatColor.GREEN + "Recipe Results for query '"+args[1]+"'");
				sender.sendMessage(ChatColor.YELLOW + "ID's: " + ids);
				return true;
			}

			if(args[0].equalsIgnoreCase("info")){
				boolean hasperm = (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.info") : true;

				if(!hasperm){
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}

				if(!(args.length > 1)){
					sender.sendMessage(ChatColor.RED + "Usage: /recipe info <recipe-id>");
					return true;
				}

				if(!Functions.isInt(args[1])){
					sender.sendMessage(ChatColor.RED + "Error: '" + args[1] + "' is not a valid recipe ID.");
					return true;
				}

				if(!Recipes.recipeExists(args[1])){
					sender.sendMessage(ChatColor.RED + "Error: Could not find recipe under recipe ID '" + args[1] + "'.");
					return true;
				}
				//53 max
				SerializedRecipe r = Recipes.getRecipe(Integer.parseInt(args[1]));
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GREEN + "Recipe info for recipe ID '" + ChatColor.YELLOW + args[1] + ChatColor.GREEN + "'");
				sender.sendMessage(ChatColor.YELLOW + "Output: " + ChatColor.GREEN + "[" + r.getResult().getType().name() + " x " + r.getResult().getAmount() + "] " + ((r.getResult().getEnchantments().size() > 0) ? ChatColor.BLUE + "[ENCHANTED]" : ""));


				String type = SerializedRecipe.typeToString(r.getType());
				sender.sendMessage(ChatColor.YELLOW + "Type: " + ChatColor.GREEN + type);
				List<ItemStack> ingredients = r.getIngredients();
				String ing = "";
				ArrayList<String> already = new ArrayList<String>();
				for(ItemStack i : ingredients){
					if(i != null){
						if(!already.contains(Functions.is2s(i))){
							ing = (ing.equalsIgnoreCase("")) ? Functions.is2s(i) : ing + ", " + Functions.is2s(i) ;
							already.add(Functions.is2s(i));
						}
					}
				}
				sender.sendMessage(ChatColor.YELLOW + "Ingredients: " + ChatColor.GREEN + ing);
				sender.sendMessage(ChatColor.YELLOW + "Permission: " + ChatColor.GREEN  + r.permission);
				sender.sendMessage(ChatColor.YELLOW + "Default Bukkit: " + ((r.isDefaultBukkit()) ? ChatColor.GREEN : ChatColor.RED) + r.isDefaultBukkit());
				return true;
			}

			if(args[0].equalsIgnoreCase("reload")){
				boolean hasperm = (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.reload") : true;

				if(!hasperm){
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}

				Recipes.regenerateRecipes(new CommandSender[] {sender});
				Recipes.loadRecipes(new CommandSender[] {sender});
				return true;
			}

			if(args[0].equalsIgnoreCase("reset")){
				boolean hasperm = (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.reset") : true;

				if(!hasperm){
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}

				sender.sendMessage(ChatColor.RED + "=== RESETTING ALL RECIPES TO DEFAULT ===");
				Recipes.resetAllRecipes((sender instanceof Player) ? new CommandSender[] {sender, console} : new CommandSender[] {console});
				sender.sendMessage(ChatColor.RED + "=== RECIPES RESET TO DEFAULT ===");
				return true;
			}

			if(args[0].equalsIgnoreCase("permissions")){
				boolean hasperm = (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.permissioncontrol") : true;

				if(!hasperm){
					sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}

				if(!(args.length > 1)){
					sender.sendMessage(ChatColor.RED + "Usage: /recipe permissions <enable/disable>");
					return true;
				}

				if(args[1].equalsIgnoreCase("enable")){
					getConfig().set("Permissions-Enabled", true);
					saveConfig();
					sender.sendMessage(ChatColor.YELLOW + "[RecipeCreator] Permissions "+ChatColor.GREEN+"enabled!");
				}
				if(args[1].equalsIgnoreCase("disable")){
					getConfig().set("Permissions-Enabled", false);
					saveConfig();
					sender.sendMessage(ChatColor.YELLOW + "[RecipeCreator] Permissions "+ChatColor.RED+"disabled!");
				}
				return true;
			}

			sender.sendMessage(ChatColor.RED + "Bad Command Usage. Type /recipe help");





		}

		return true;
	}


}
