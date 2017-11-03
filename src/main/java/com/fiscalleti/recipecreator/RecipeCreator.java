package com.fiscalleti.recipecreator;

import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import com.fiscalleti.recipecreator.Recipes.RecipeBuilder;
import com.fiscalleti.recipecreator.serialization.RecipeStorage;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;
import com.google.common.collect.Lists;

public class RecipeCreator extends JavaPlugin {

	public static RecipeCreator instance;
	public Logger log;
	public ConsoleCommandSender console;
	public Events events;
	public RecipeStorage recipestorage;
	public RecipeRegistrar reciperegistrar;

	@Override
	public void onEnable() {
		instance = this;
		this.log = getLogger();
		this.console = getServer().getConsoleSender();
		this.events = new Events();
		this.recipestorage = RecipeStorage.createRecipesStorage(getDataFolder());
		this.reciperegistrar = new RecipeRegistrar(getServer());
		getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(this.events, this);
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		// getServer().clearRecipes();
		Recipes.loadRecipes(ChatOutput.create(this.console));
	}

	public static boolean hasPermission(final Permissible permissible, final String node) {
		if (!(permissible instanceof Player))
			return true;
		if (permissible.hasPermission(node)||permissible.hasPermission("*"))
			return true;
		for (int y = 0; y<node.length(); y++)
			if (node.charAt(y)=='.')
				if (permissible.hasPermission(node.substring(0, y)+".*"))
					return true;
		return false;
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
				sender.sendMessage(ChatColor.YELLOW+"--------- "+ChatColor.GREEN+"Recipe Creator"+ChatColor.YELLOW+" ---------");
				sender.sendMessage(ChatColor.GRAY+"Recipe Creator Commands");
				if (
					hasPermission(sender, "recipecreator.add.shaped")||hasPermission(sender, "recipecreator.add.shaped.trim")||
							hasPermission(sender, "recipecreator.add.shapeless")||hasPermission(sender, "recipecreator.add.furnace")
				)
					sender.sendMessage(ChatColor.GOLD+"/recipe add <shaped/shaped_trim/shapeless/furnace> <recipe-name>: "+ChatColor.WHITE+"Addes a recipe to the game");
				if (hasPermission(sender, "recipecreator.remove"))
					sender.sendMessage(ChatColor.GOLD+"/recipe remove <recipe-name>: "+ChatColor.WHITE+"Removes a recipe from the game");
				if (hasPermission(sender, "recipecreator.info"))
					sender.sendMessage(ChatColor.GOLD+"/recipe info <recipe-name>: "+ChatColor.WHITE+"Retrieves a recipes information");
				if (hasPermission(sender, "recipecreator.lookup"))
					sender.sendMessage(ChatColor.GOLD+"/recipe lookup <ITEM_NAME>: "+ChatColor.WHITE+"Retrieves an items Recipe ID's");
				if (hasPermission(sender, "recipecreator.reset"))
					sender.sendMessage(ChatColor.GOLD+"/recipe reset: "+ChatColor.WHITE+"Resets the recipes to default");
				if (hasPermission(sender, "recipecreator.reload"))
					sender.sendMessage(ChatColor.GOLD+"/recipe reload: "+ChatColor.WHITE+"Reloads the recipes");
				if (hasPermission(sender, "recipecreator.permissioncontrol"))
					sender.sendMessage(ChatColor.GOLD+"/recipe permissions <enable/disable>: "+ChatColor.WHITE+"Enabled or disables crafting permissions");
				return true;
			}

			if (args[0].equalsIgnoreCase("add")) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED+"That command must be used in game");
					return true;
				}
				final Player player = (Player) sender;
				if (args.length>2) {
					final String type = args[1];
					final String name = args[2];

					if (type.equalsIgnoreCase("shapeless")) {
						if (!hasPermission(player, "recipecreator.add.shapeless")) {
							player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
							return true;
						}
						Recipes.add(ChatOutput.create(player), name, new RecipeBuilder.ShapelessRecipeBuilder(player).toSerializedRecipe());
						return true;
					}

					if (type.equalsIgnoreCase("shaped")) {
						if (!hasPermission(player, "recipecreator.add.shaped")) {
							player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
							return true;
						}
						Recipes.add(ChatOutput.create(player), name, new RecipeBuilder.ShapedRecipeBuilder(player).toSerializedRecipe());
						return true;
					}

					if (type.equalsIgnoreCase("shaped_trim")) {
						if (!hasPermission(player, "recipecreator.add.shaped.trim")) {
							player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
							return true;
						}
						Recipes.add(ChatOutput.create(player), name, new RecipeBuilder.TrimmedShapedRecipeBuilder(player).toSerializedRecipe());
						return true;
					}

					if (type.equalsIgnoreCase("furnace")) {
						if (!hasPermission(player, "recipecreator.add.furnace")) {
							player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
							return true;
						}
						Recipes.add(ChatOutput.create(player), name, new RecipeBuilder.FurnaceRecipeBuilder(player).toSerializedRecipe());
						return true;
					}
				}

				player.sendMessage(ChatColor.RED+"Usage: /recipe add <shaped/shaped_trim/shapeless/furnace> <name>");
				return true;
			}

			if (args[0].equalsIgnoreCase("remove")) {
				if (!hasPermission(sender, "recipecreator.remove")) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				if (!(args.length>1)) {
					sender.sendMessage(ChatColor.RED+"Usage: /recipe remove <recipe-name>");
					return true;
				}

				if (Recipes.removeRecipe(ChatOutput.create(sender, this.console), args[1])) {
					sender.sendMessage(ChatColor.YELLOW+"Recipe removed!");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED+"There was an error while removing that recipe. Does it exist?");
					return true;
				}

			}

			if (args[0].equalsIgnoreCase("lookup")) {
				if (!hasPermission(sender, "recipecreator.lookup")) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				if (!(args.length>1)) {
					sender.sendMessage(ChatColor.RED+"Usage: /recipe lookup <BLOCK_NAME>");
					return true;
				}

				final List<String> rets = Lists.newArrayList();
				for (final Entry<String, SerializedRecipe> entry : RecipeCreator.instance.recipestorage.getRecipes().entrySet()) {
					final ItemStack itemStack = entry.getValue().getRecipe().getResult();
					if (
						StringUtils.equalsIgnoreCase(itemStack.getType().name(), args[1])||
								StringUtils.equalsIgnoreCase(String.valueOf(itemStack.getTypeId()), args[1])||
								StringUtils.equalsIgnoreCase(itemStack.getTypeId()+":"+itemStack.getData().getData(), args[1])
					)
						rets.add(entry.getKey());
				}

				if (rets.size()<1) {
					sender.sendMessage(ChatColor.RED+"No recipes found with result '"+args[1]+"'");
					return true;
				}

				final String ids = StringUtils.join(rets, ", ");

				sender.sendMessage(ChatColor.GREEN+"Recipe Results for query '"+args[1]+"'");
				sender.sendMessage(ChatColor.YELLOW+"ID's: "+ids);
				return true;
			}

			if (args[0].equalsIgnoreCase("info")) {
				if (!hasPermission(sender, "recipecreator.info")) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				if (!(args.length>1)) {
					sender.sendMessage(ChatColor.RED+"Usage: /recipe info <recipe-id>");
					return true;
				}

				//53 max
				final SerializedRecipe r = RecipeCreator.instance.recipestorage.getRecipe(args[1]);
				sender.sendMessage("");
				sender.sendMessage(ChatColor.GREEN+"Recipe info for recipe ID '"+ChatColor.YELLOW+args[1]+ChatColor.GREEN+"'");
				final ItemStack itemStack = r.getRecipe().getResult();
				sender.sendMessage(ChatColor.YELLOW+"Output: "+ChatColor.GREEN+"["+itemStack.getType().name()+" x "+itemStack.getAmount()+"] "+(itemStack.getEnchantments().size()>0 ? ChatColor.BLUE+"[ENCHANTED]" : ""));

				final String type = r.getType().name;
				sender.sendMessage(ChatColor.YELLOW+"Type: "+ChatColor.GREEN+type);
				/*final List<ItemStack> ingredients = r.getRecipe().getIngredients();
				String ing = "";
				final List<String> already = Lists.newArrayList();
				for (final ItemStack i : ingredients)
					if (i!=null)
						if (!already.contains(Functions.is2s(i))) {
							ing = ing.equalsIgnoreCase("") ? Functions.is2s(i) : ing+", "+Functions.is2s(i);
							already.add(Functions.is2s(i));
						}
				sender.sendMessage(ChatColor.YELLOW+"Ingredients: "+ChatColor.GREEN+ing);
				*/
				//sender.sendMessage(ChatColor.YELLOW+"Permission: "+ChatColor.GREEN+r.permission);
				return true;
			}

			if (args[0].equalsIgnoreCase("reload")) {
				if (!hasPermission(sender, "recipecreator.reload")) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}

				Recipes.loadRecipes(ChatOutput.create(sender));
				return true;
			}

			if (args[0].equalsIgnoreCase("reset")) {
				if (!hasPermission(sender, "recipecreator.reset")) {
					sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
					return true;
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("permissions")) {
				if (!hasPermission(sender, "recipecreator.permissioncontrol")) {
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
