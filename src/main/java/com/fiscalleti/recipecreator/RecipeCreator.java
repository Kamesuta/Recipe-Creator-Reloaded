package com.fiscalleti.recipecreator;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import com.fiscalleti.recipecreator.Recipes.RecipeBuilder;
import com.fiscalleti.recipecreator.Recipes.RecipeBuilder.RecipeIngredients;
import com.fiscalleti.recipecreator.Recipes.RecipeBuilder.RecipeResult;
import com.fiscalleti.recipecreator.serialization.RecipeStorage;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe.RecipeType;
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

		if (!commandLabel.equalsIgnoreCase("recipe"))
			return true;

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
				sender.sendMessage(ChatColor.GOLD+"/recipe lookup [result/ingredient/recipe]: "+ChatColor.WHITE+"Retrieves an items Recipe ID's");
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
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED+"That command must be used in game");
				return true;
			}
			final Player player = (Player) sender;

			if (!hasPermission(sender, "recipecreator.lookup")) {
				sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
				return true;
			}

			final List<String> rets = Lists.newArrayList();

			if (args.length>1) {
				final String type = args[1];

				if (type.equalsIgnoreCase("result")) {
					final ItemStack playerItemStack = player.getInventory().getItem(17);
					for (final Entry<String, SerializedRecipe> entry : RecipeCreator.instance.recipestorage.getRecipes().entrySet()) {
						final ItemStack itemStack = entry.getValue().getRecipe().getResult();
						if (playerItemStack!=null&&playerItemStack.equals(itemStack))
							rets.add(entry.getKey());
					}
				} else if (type.equalsIgnoreCase("ingredient")) {
					if (args.length<=2) {
						sender.sendMessage(ChatColor.RED+"/recipe lookup ingredient <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}
					final String argument = args[2];

					RecipeBuilder recipebuilder;
					if (argument.equalsIgnoreCase("shapeless"))
						recipebuilder = new RecipeBuilder.ShapelessRecipeBuilder(player);
					else if (argument.equalsIgnoreCase("shaped"))
						recipebuilder = new RecipeBuilder.ShapedRecipeBuilder(player);
					else if (argument.equalsIgnoreCase("shaped_trim"))
						recipebuilder = new RecipeBuilder.TrimmedShapedRecipeBuilder(player);
					else if (argument.equalsIgnoreCase("furnace"))
						recipebuilder = new RecipeBuilder.FurnaceRecipeBuilder(player);
					else {
						sender.sendMessage(ChatColor.RED+"/recipe lookup ingredient <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}

					final RecipeIngredients<?> ingredients = recipebuilder.toIngredients();
					for (final Entry<String, SerializedRecipe> entry : RecipeCreator.instance.recipestorage.getRecipes().entrySet()) {
						final SerializedRecipe recipe0 = entry.getValue();
						if (recipe0==null)
							continue;
						final Recipe recipe1 = recipe0.getRecipe();
						if (recipe1==null)
							continue;
						final RecipeIngredients<?> ingredients1 = RecipeIngredients.createFromRecipe(recipe1);
						if (Objects.equals(ingredients, ingredients1))
							rets.add(entry.getKey());
					}
				} else if (type.equalsIgnoreCase("recipe")) {
					if (args.length<=2) {
						sender.sendMessage(ChatColor.RED+"/recipe lookup recipe <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}
					final String argument = args[2];

					RecipeBuilder recipebuilder;
					if (argument.equalsIgnoreCase("shapeless"))
						recipebuilder = new RecipeBuilder.ShapelessRecipeBuilder(player);
					else if (argument.equalsIgnoreCase("shaped"))
						recipebuilder = new RecipeBuilder.ShapedRecipeBuilder(player);
					else if (argument.equalsIgnoreCase("shaped_trim"))
						recipebuilder = new RecipeBuilder.TrimmedShapedRecipeBuilder(player);
					else if (argument.equalsIgnoreCase("furnace"))
						recipebuilder = new RecipeBuilder.FurnaceRecipeBuilder(player);
					else {
						sender.sendMessage(ChatColor.RED+"/recipe lookup ingredient <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}

					final RecipeResult result = recipebuilder.toResult();
					final RecipeIngredients<?> ingredients = recipebuilder.toIngredients();
					for (final Entry<String, SerializedRecipe> entry : RecipeCreator.instance.recipestorage.getRecipes().entrySet()) {
						final SerializedRecipe recipe0 = entry.getValue();
						if (recipe0==null)
							continue;
						final Recipe recipe1 = recipe0.getRecipe();
						if (recipe1==null)
							continue;
						final RecipeResult result1 = RecipeResult.createFromRecipe(recipe1);
						final RecipeIngredients<?> ingredients1 = RecipeIngredients.createFromRecipe(recipe1);
						if (Objects.equals(result, result1)&&Objects.equals(ingredients, ingredients1))
							rets.add(entry.getKey());
					}
				} else {
					sender.sendMessage(ChatColor.RED+"/recipe lookup [result/ingredient/recipe]");
					return true;
				}
			} else
				rets.addAll(RecipeCreator.instance.recipestorage.getRecipes().keySet());

			if (rets.size()<1) {
				sender.sendMessage(ChatColor.RED+"No recipes found");
				return true;
			}

			final String ids = StringUtils.join(rets, ChatColor.GRAY+", "+ChatColor.YELLOW);

			sender.sendMessage(ChatColor.DARK_GREEN+"Recipe Results");
			sender.sendMessage(ChatColor.DARK_GREEN+"ID's: "+ChatColor.YELLOW+ids);
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
			final String name = args[1];

			//53 max
			final SerializedRecipe recipe = RecipeCreator.instance.recipestorage.getRecipe(name);
			if (recipe==null) {
				sender.sendMessage(ChatColor.RED+"No recipes found");
				return true;
			}

			sender.sendMessage("");
			sender.sendMessage(ChatColor.GREEN+"Recipe info for recipe ID '"+ChatColor.YELLOW+args[1]+ChatColor.GREEN+"'");
			final ItemStack itemStack = recipe.getRecipe().getResult();
			sender.sendMessage(ChatColor.YELLOW+"Output: "+ChatColor.GREEN+"["+itemStack.getType().name()+" x "+itemStack.getAmount()+"] "+(itemStack.getEnchantments().size()>0 ? ChatColor.BLUE+"[ENCHANTED]" : ""));

			final RecipeType type = recipe.getType();
			final Recipe recipe0 = recipe.getRecipe();
			sender.sendMessage(ChatColor.YELLOW+"Type: "+ChatColor.GREEN+type.name);
			switch (type) {
				case SHAPED:
					final ShapedRecipe recipe1 = (ShapedRecipe) recipe0;
					sender.sendMessage(ChatColor.YELLOW+"Shape:");
					for (final String line : recipe1.getShape())
						sender.sendMessage("  "+ChatColor.GREEN+"["+StringUtils.join(ArrayUtils.toObject(line.toCharArray()), "][")+"]");
					sender.sendMessage(ChatColor.YELLOW+"Ingredients:");
					for (final Entry<Character, ItemStack> entry : recipe1.getIngredientMap().entrySet()) {
						final Character key = entry.getKey();
						final MaterialData data = entry.getValue().getData();
						sender.sendMessage("  "+ChatColor.GREEN+"["+key+"]"+ChatColor.GRAY+" : "+ChatColor.DARK_GREEN+data);
					}
					break;
				case SHAPELESS:
					final ShapelessRecipe recipe2 = (ShapelessRecipe) recipe0;
					sender.sendMessage(ChatColor.YELLOW+"Ingredients:");
					for (final ItemStack item : recipe2.getIngredientList())
						sender.sendMessage("  "+ChatColor.DARK_GREEN+item.getData());
					break;
				case FURNACE:
					final FurnaceRecipe recipe3 = (FurnaceRecipe) recipe0;
					sender.sendMessage(ChatColor.YELLOW+"Ingredient:");
					sender.sendMessage("  "+ChatColor.DARK_GREEN+recipe3.getInput().getData());
					break;
				default:
					break;
			}
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
			sender.sendMessage(ChatColor.YELLOW+"Permission: "+ChatColor.GREEN+"recipecreator.recipes."+name);
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
		return true;
	}

}
