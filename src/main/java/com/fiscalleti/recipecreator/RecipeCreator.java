package com.fiscalleti.recipecreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.fiscalleti.recipecreator.serialization.RecipeStorage;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;

import guava10.com.google.common.collect.Lists;

public class RecipeCreator extends JavaPlugin {

	public static RecipeCreator instance;
	public ConsoleCommandSender console;
	public Events events;
	public RecipeStorage recipestorage;

	@Override
	public void onEnable() {
		instance = this;
		this.console = getServer().getConsoleSender();
		this.events = new Events();
		this.recipestorage = RecipeStorage.createRecipesStorage(getDataFolder());
		getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(this.events, this);
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		// getServer().clearRecipes();
		Recipes.loadRecipes(new CommandSender[] { this.console });
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String commandLabel, final String[] args) {

		if (commandLabel.equalsIgnoreCase("recipe")) {
			if (args.length<1) {
				// /recipe
				sender.sendMessage(ChatColor.YELLOW+"--------- "+ChatColor.GREEN+"Recipe Creator"+ChatColor.YELLOW+" ---------");
				sender.sendMessage(ChatColor.GREEN+getDescription().getDescription());
				sender.sendMessage(ChatColor.YELLOW+"Author: "+ChatColor.GREEN+getDescription().getAuthors().get(0));
				sender.sendMessage(ChatColor.YELLOW+"Version: "+ChatColor.GREEN+getDescription().getVersion());

				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				// /recipe help
				final ConcurrentHashMap<String, Boolean> permissable = new ConcurrentHashMap<String, Boolean>();
				permissable.put("Add", sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.add.shaped")||((Player) sender).hasPermission("recipecreator.add.shapeless") : true);
				permissable.put("Remove", sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.remove") : true);
				permissable.put("Info", sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.info") : true);
				permissable.put("Lookup", sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.lookup") : true);
				permissable.put("Reset", sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.reset") : true);
				permissable.put("Reload", sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.reload") : true);
				permissable.put("Perms", sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.permissioncontrol") : true);
				sender.sendMessage(ChatColor.YELLOW+"--------- "+ChatColor.GREEN+"Recipe Creator"+ChatColor.YELLOW+" ---------");
				sender.sendMessage(ChatColor.GRAY+"Recipe Creator Commands");
				if (permissable.get("Add"))
					sender.sendMessage(ChatColor.GOLD+"/recipe add [shaped/shapeless]: "+ChatColor.WHITE+"Addes a recipe to the game");
				if (permissable.get("Remove"))
					sender.sendMessage(ChatColor.GOLD+"/recipe remove <recipe-id / ALL>: "+ChatColor.WHITE+"Removes a recipe from the game");
				if (permissable.get("Info"))
					sender.sendMessage(ChatColor.GOLD+"/recipe info [recipe-id]: "+ChatColor.WHITE+"Retrieves a recipes information");
				if (permissable.get("Lookup"))
					sender.sendMessage(ChatColor.GOLD+"/recipe lookup <ITEM_NAME>: "+ChatColor.WHITE+"Retrieves an items Recipe ID's");
				if (permissable.get("Reset"))
					sender.sendMessage(ChatColor.GOLD+"/recipe reset: "+ChatColor.WHITE+"Resets the recipes to default");
				if (permissable.get("Reload"))
					sender.sendMessage(ChatColor.GOLD+"/recipe reload: "+ChatColor.WHITE+"Reloads the recipes");
				if (permissable.get("Perms"))
					sender.sendMessage(ChatColor.GOLD+"/recipe permissions <enable/disable>: "+ChatColor.WHITE+"Enabled or disables crafting permissions");
				return true;
			}

			if (args[0].equalsIgnoreCase("add")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED+"That command must be used in game");
					return true;
				}
				final Player player = (Player) sender;
				String type = "shaped";
				if (args.length>1)
					type = args[1];

				if (!(type.equalsIgnoreCase("shaped")||type.equalsIgnoreCase("shapeless"))) {
					player.sendMessage(ChatColor.RED+"Usage: /recipe add [shaped/shapeless]");
					return true;
				}

				if (type.equalsIgnoreCase("shapeless")) {
					if (!player.hasPermission("recipecreator.add.shapeless")) {
						player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
						return true;
					}
					Recipes.createShapeless(player);
					return true;
				}

				if (type.equalsIgnoreCase("shaped")) {
					if (!player.hasPermission("recipecreator.add.shaped")) {
						player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
						return true;
					}
					Recipes.createShaped(player);
					return true;
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("remove")) {
				final boolean hasperm = sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.remove") : true;

				if (!hasperm) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				if (!(args.length>1)) {
					sender.sendMessage(ChatColor.RED+"Usage: /recipe remove <recipe-id / ALL>");
					return true;
				}

				if (args[1].equalsIgnoreCase("ALL")) {
					sender.sendMessage(ChatColor.RED+"=== REMOVING ALL RECIPES FROM SYSTEM ===");
					RecipeCreator.instance.getServer().clearRecipes();
					Recipes.loadRecipes(new CommandSender[] { this.console, sender });
					sender.sendMessage(ChatColor.RED+"=== ALL RECIPES REMOVED ===");
					return true;
				}

				if (Recipes.removeRecipe(args[1], new CommandSender[] { sender, this.console })) {
					sender.sendMessage(ChatColor.YELLOW+"Recipe removed!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED+"There was an error while removing that recipe. Does it exist?");
					return true;
				}

			}

			if (args[0].equalsIgnoreCase("lookup")) {
				final boolean hasperm = sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.lookup") : true;

				if (!hasperm) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				if (!(args.length>1)) {
					sender.sendMessage(ChatColor.RED+"Usage: /recipe lookup <BLOCK_NAME>");
					return true;
				}

				final List<SerializedRecipe> recs = RecipeCreator.instance.recipestorage.getRecipes();
				final List<SerializedRecipe> rets = Lists.newArrayList();
				for (final SerializedRecipe r : recs)
					if (r.getResult().getData().getItemType().name().equalsIgnoreCase(args[1])||args[1].equalsIgnoreCase(String.valueOf(r.getResult().getTypeId()))||args[1].equalsIgnoreCase(String.valueOf(r.getResult().getTypeId())+":"+Byte.toString(r.getResult().getData().getData())))
						rets.add(r);

				if (rets.size()<1) {
					sender.sendMessage(ChatColor.RED+"No recipes found with result '"+args[1]+"'");
					return true;
				}

				String ids = "";

				for (final SerializedRecipe r : rets)
					ids = ids.equalsIgnoreCase("") ? r.getId() : ids+", "+r.getId();

				sender.sendMessage(ChatColor.GREEN+"Recipe Results for query '"+args[1]+"'");
				sender.sendMessage(ChatColor.YELLOW+"ID's: "+ids);
				return true;
			}

			if (args[0].equalsIgnoreCase("info")) {
				final boolean hasperm = sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.info") : true;

				if (!hasperm) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				if (!(args.length>1)) {
					sender.sendMessage(ChatColor.RED+"Usage: /recipe info <recipe-id>");
					return true;
				}

				if (!NumberUtils.isNumber(args[1])) {
					sender.sendMessage(ChatColor.RED+"Error: '"+args[1]+"' is not a valid recipe ID.");
					return true;
				}

				if (!Recipes.recipeExists(args[1])) {
					sender.sendMessage(ChatColor.RED+"Error: Could not find recipe under recipe ID '"+args[1]+"'.");
					return true;
				}
				//53 max
				final SerializedRecipe r = RecipeCreator.instance.recipestorage.getRecipe(args[1]);
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GREEN+"Recipe info for recipe ID '"+ChatColor.YELLOW+args[1]+ChatColor.GREEN+"'");
				sender.sendMessage(ChatColor.YELLOW+"Output: "+ChatColor.GREEN+"["+r.getResult().getType().name()+" x "+r.getResult().getAmount()+"] "+(r.getResult().getEnchantments().size()>0 ? ChatColor.BLUE+"[ENCHANTED]" : ""));

				final String type = r.getType().name;
				sender.sendMessage(ChatColor.YELLOW+"Type: "+ChatColor.GREEN+type);
				final List<ItemStack> ingredients = r.getIngredients();
				String ing = "";
				final ArrayList<String> already = new ArrayList<String>();
				for (final ItemStack i : ingredients)
					if (i!=null)
						if (!already.contains(Functions.is2s(i))) {
							ing = ing.equalsIgnoreCase("") ? Functions.is2s(i) : ing+", "+Functions.is2s(i);
							already.add(Functions.is2s(i));
						}
				sender.sendMessage(ChatColor.YELLOW+"Ingredients: "+ChatColor.GREEN+ing);
				sender.sendMessage(ChatColor.YELLOW+"Permission: "+ChatColor.GREEN+r.permission);
				return true;
			}

			if (args[0].equalsIgnoreCase("reload")) {
				final boolean hasperm = sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.reload") : true;

				if (!hasperm) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				Recipes.regenerateRecipes(new CommandSender[] { sender });
				Recipes.loadRecipes(new CommandSender[] { sender });
				return true;
			}

			if (args[0].equalsIgnoreCase("reset")) {
				final boolean hasperm = sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.reset") : true;

				if (!hasperm) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				sender.sendMessage(ChatColor.RED+"=== RESETTING ALL RECIPES TO DEFAULT ===");
				Recipes.resetAllRecipes(sender instanceof Player ? new CommandSender[] { sender, this.console } : new CommandSender[] { this.console });
				sender.sendMessage(ChatColor.RED+"=== RECIPES RESET TO DEFAULT ===");
				return true;
			}

			if (args[0].equalsIgnoreCase("permissions")) {
				final boolean hasperm = sender instanceof Player ? ((Player) sender).hasPermission("recipecreator.permissioncontrol") : true;

				if (!hasperm) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				if (!(args.length>1)) {
					sender.sendMessage(ChatColor.RED+"Usage: /recipe permissions <enable/disable>");
					return true;
				}

				if (args[1].equalsIgnoreCase("enable")) {
					getConfig().set("Permissions-Enabled", true);
					saveConfig();
					sender.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Permissions "+ChatColor.GREEN+"enabled!");
				}
				if (args[1].equalsIgnoreCase("disable")) {
					getConfig().set("Permissions-Enabled", false);
					saveConfig();
					sender.sendMessage(ChatColor.YELLOW+"[RecipeCreator] Permissions "+ChatColor.RED+"disabled!");
				}
				return true;
			}

			sender.sendMessage(ChatColor.RED+"Bad Command Usage. Type /recipe help");

		}

		return true;
	}

}
