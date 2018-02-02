package net.teamfruit.rerecipecreators;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
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
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.material.MaterialData;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.Lists;

import net.teamfruit.rerecipecreators.Recipes.CustomRecipe.FurnaceCustomRecipe;
import net.teamfruit.rerecipecreators.Recipes.CustomRecipe.ShapedCustomRecipe;
import net.teamfruit.rerecipecreators.Recipes.CustomRecipe.ShapelessCustomRecipe;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients.FurnaceRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients.ShapedRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients.ShapelessRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeIngredients.TrimmedShapedRecipeIngredients;
import net.teamfruit.rerecipecreators.Recipes.RecipeResult;
import net.teamfruit.rerecipecreators.serialization.RecipeStorage;
import net.teamfruit.rerecipecreators.serialization.SerializedRecipe;

public class ReRecipeCreators extends JavaPlugin {
	private boolean initialized;

	public static ReRecipeCreators instance;
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
		this.events = new Events(this);
		this.recipestorage = RecipeStorage.createRecipesStorage(getDataFolder());
		this.reciperegistrar = new RecipeRegistrar(getServer());
		getConfig().options().copyDefaults(true);
		saveConfig();
		getServer().getPluginManager().registerEvents(this.events, this);
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		// getServer().clearRecipes();
		Recipes.loadRecipes(ChatOutput.create(this.console));
		this.initialized = true;
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
		if (!this.initialized) {
			sender.sendMessage("Re:RecipeCreators is not available");
			return true;
		}

		if (!commandLabel.equalsIgnoreCase("recipe"))
			return true;

		if (args.length<1) {
			// /recipe
			sender.sendMessage(ChatColor.YELLOW+"--------- "+ChatColor.GREEN+"Re: RecipeCreators"+ChatColor.YELLOW+" ---------");
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
				hasPermission(sender, "rerecipecreators.add.shaped")||hasPermission(sender, "rerecipecreators.add.shaped.trim")||
						hasPermission(sender, "rerecipecreators.add.shapeless")||hasPermission(sender, "rerecipecreators.add.furnace")
			)
				sender.sendMessage(ChatColor.GOLD+"/recipe add <shaped/shaped_trim/shapeless/furnace> <recipe-name>: "+ChatColor.WHITE+"Addes a recipe to the game");
			if (hasPermission(sender, "rerecipecreators.remove"))
				sender.sendMessage(ChatColor.GOLD+"/recipe remove <recipe-name>: "+ChatColor.WHITE+"Removes a recipe from the game");
			if (hasPermission(sender, "rerecipecreators.info"))
				sender.sendMessage(ChatColor.GOLD+"/recipe info <recipe-name>: "+ChatColor.WHITE+"Retrieves a recipes information");
			if (hasPermission(sender, "rerecipecreators.lookup"))
				sender.sendMessage(ChatColor.GOLD+"/recipe lookup [result/ingredient/recipe]: "+ChatColor.WHITE+"Retrieves an items Recipe ID's");
			if (hasPermission(sender, "rerecipecreators.reload"))
				sender.sendMessage(ChatColor.GOLD+"/recipe reload: "+ChatColor.WHITE+"Reloads the recipes");
			if (hasPermission(sender, "rerecipecreators.permissioncontrol"))
				sender.sendMessage(ChatColor.GOLD+"/recipe permissions <enable/disable>: "+ChatColor.WHITE+"Enabled or disables crafting permissions");
			return true;
		}

		if (args[0].equalsIgnoreCase("add")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED+"That command must be used in game");
				return true;
			}
			final Player player = (Player) sender;
			final PlayerInventory playerinv = player.getInventory();

			if (args.length>2) {
				final String type = args[1];
				final String name = args[2];
				final String[] alias = Arrays.copyOfRange(args, 3, args.length);

				if (type.equalsIgnoreCase("shapeless")) {
					if (!hasPermission(player, "rerecipecreators.add.shapeless")) {
						player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
						return true;
					}
					final ShapelessRecipeIngredients ingredients = new ShapelessRecipeIngredients();
					ingredients.fromInventoryItem(playerinv);
					final RecipeResult result = new RecipeResult();
					result.fromInventoryItem(playerinv);
					final SerializedRecipe recipe = new ShapelessCustomRecipe(ingredients, result).toSerializedRecipe();
					if (recipe==null) {
						player.sendMessage(ChatColor.RED+"Invalid Recipe.");
						return true;
					}
					Recipes.add(ChatOutput.create(player), recipe, name, alias);
					return true;
				}

				if (type.equalsIgnoreCase("shaped")) {
					if (!hasPermission(player, "rerecipecreators.add.shaped")) {
						player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
						return true;
					}
					final ShapedRecipeIngredients ingredients = new ShapedRecipeIngredients();
					ingredients.fromInventoryItem(playerinv);
					final RecipeResult result = new RecipeResult();
					result.fromInventoryItem(playerinv);
					final SerializedRecipe recipe = new ShapedCustomRecipe(ingredients, result).toSerializedRecipe();
					if (recipe==null) {
						player.sendMessage(ChatColor.RED+"Invalid Recipe.");
						return true;
					}
					Recipes.add(ChatOutput.create(player), recipe, name, alias);
					return true;
				}

				if (type.equalsIgnoreCase("shaped_trim")) {
					if (!hasPermission(player, "rerecipecreators.add.shaped.trim")) {
						player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
						return true;
					}
					final ShapedRecipeIngredients ingredients = new TrimmedShapedRecipeIngredients();
					ingredients.fromInventoryItem(playerinv);
					final RecipeResult result = new RecipeResult();
					result.fromInventoryItem(playerinv);
					final SerializedRecipe recipe = new ShapedCustomRecipe(ingredients, result).toSerializedRecipe();
					if (recipe==null) {
						player.sendMessage(ChatColor.RED+"Invalid Recipe.");
						return true;
					}
					Recipes.add(ChatOutput.create(player), recipe, name, alias);
					return true;
				}

				if (type.equalsIgnoreCase("furnace")) {
					if (!hasPermission(player, "rerecipecreators.add.furnace")) {
						player.sendMessage(ChatColor.RED+"You don't have permission to do that.");
						return true;
					}
					final FurnaceRecipeIngredients ingredients = new FurnaceRecipeIngredients();
					ingredients.fromInventoryItem(playerinv);
					final RecipeResult result = new RecipeResult();
					result.fromInventoryItem(playerinv);
					final SerializedRecipe recipe = new FurnaceCustomRecipe(ingredients, result).toSerializedRecipe();
					if (recipe==null) {
						player.sendMessage(ChatColor.RED+"Invalid Recipe.");
						return true;
					}
					Recipes.add(ChatOutput.create(player), recipe, name, alias);
					return true;
				}
			}

			player.sendMessage(ChatColor.RED+"Usage: /recipe add <shaped/shaped_trim/shapeless/furnace> <name> [alias1] [alias2]...");
			return true;
		}

		if (args[0].equalsIgnoreCase("remove")) {
			if (!hasPermission(sender, "rerecipecreators.remove")) {
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

		if (args[0].equalsIgnoreCase("alias")) {
			if (!hasPermission(sender, "rerecipecreators.alias")) {
				sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
				return true;
			}

			if (args.length>1) {
				final String type = args[1];

				if (type.equalsIgnoreCase("add")) {
					if (args.length<=2) {
						sender.sendMessage(ChatColor.RED+"/recipe alias add <name> <alias1> <alias2>...");
						return true;
					}
					final String name = args[2];
					final String[] alias = Arrays.copyOfRange(args, 3, args.length);

					final SerializedRecipe recipe = ReRecipeCreators.instance.recipestorage.getRecipe(name);
					if (recipe==null) {
						sender.sendMessage(ChatColor.RED+"No recipes found");
						return true;
					}

					if (alias.length>0) {
						recipe.getAlias().addAll(Arrays.asList(alias));
						ReRecipeCreators.instance.recipestorage.putRecipe(name, recipe);
					}

					sender.sendMessage(ChatColor.YELLOW+"Recipe alias changed");
					return true;
				} else if (type.equalsIgnoreCase("remove")) {
					if (args.length<=2) {
						sender.sendMessage(ChatColor.RED+"/recipe alias add <name> <alias1> <alias2>...");
						return true;
					}
					final String name = args[2];
					final String[] alias = Arrays.copyOfRange(args, 3, args.length);

					final SerializedRecipe recipe = ReRecipeCreators.instance.recipestorage.getRecipe(name);
					if (recipe==null) {
						sender.sendMessage(ChatColor.RED+"No recipes found");
						return true;
					}

					if (alias.length>0) {
						recipe.getAlias().removeAll(Arrays.asList(alias));
						ReRecipeCreators.instance.recipestorage.putRecipe(name, recipe);
					}

					sender.sendMessage(ChatColor.YELLOW+"Recipe alias changed");
					return true;
				}

			}

		}

		if (args[0].equalsIgnoreCase("lookup")) {

			if (!hasPermission(sender, "rerecipecreators.lookup")) {
				sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
				return true;
			}

			final List<String> rets = Lists.newArrayList();

			if (args.length>1) {
				if (!"".isEmpty()) {
					sender.sendMessage(ChatColor.RED+"This feature is not implemented.");
					return true;
				}

				final String type = args[1];

				if (type.equalsIgnoreCase("alias")) {
					if (args.length<=2) {
						sender.sendMessage(ChatColor.RED+"/recipe lookup alias <name/alias>");
						return true;
					}
					final String argument = args[2];

					for (final Entry<String, SerializedRecipe> entry : ReRecipeCreators.instance.recipestorage.getRecipes().entrySet()) {
						final String name = entry.getKey();
						if (name.equals(argument))
							rets.add(name);
						else {
							final Set<String> alias = entry.getValue().getAlias();
							if (alias.contains(name))
								rets.add(name);
						}
					}
				} else if (type.equalsIgnoreCase("result")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"That command must be used in game");
						return true;
					}
					final Player player = (Player) sender;
					final PlayerInventory playerinv = player.getInventory();

					final RecipeResult result = new RecipeResult();
					result.fromInventoryItem(player.getInventory());
					for (final Entry<String, SerializedRecipe> entry : ReRecipeCreators.instance.recipestorage.getRecipes().entrySet()) {
						final String name = entry.getKey();
						final SerializedRecipe srecipe = entry.getValue();
						final ItemStack itemStack1 = srecipe.getCraftResult();
						if (Objects.equals(result.getCraftResult(), itemStack1))
							rets.add(name);
						else {
							final ItemStack itemStack2 = srecipe.getRecipe().getResult();
							if (Objects.equals(result.getResult(), itemStack2))
								rets.add(name);
						}
					}
				} else if (type.equalsIgnoreCase("ingredient")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"That command must be used in game");
						return true;
					}
					final Player player = (Player) sender;
					final PlayerInventory playerinv = player.getInventory();

					if (args.length<=2) {
						sender.sendMessage(ChatColor.RED+"/recipe lookup ingredient <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}
					final String argument = args[2];

					RecipeIngredients<?> ingredients;
					if (argument.equalsIgnoreCase("shapeless"))
						ingredients = new ShapelessRecipeIngredients();
					else if (argument.equalsIgnoreCase("shaped"))
						ingredients = new ShapedRecipeIngredients();
					else if (argument.equalsIgnoreCase("shaped_trim"))
						ingredients = new TrimmedShapedRecipeIngredients();
					else if (argument.equalsIgnoreCase("furnace"))
						ingredients = new FurnaceRecipeIngredients();
					else {
						sender.sendMessage(ChatColor.RED+"/recipe lookup ingredient <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}

					ingredients.fromInventoryItem(playerinv);

					for (final Entry<String, SerializedRecipe> entry : ReRecipeCreators.instance.recipestorage.getRecipes().entrySet()) {
						final SerializedRecipe recipe0 = entry.getValue();
						if (recipe0==null)
							continue;
						if (RecipeType.fromRecipe(recipe0.getRecipe())!=ingredients.getType())
							continue;
						final Recipe recipe1 = recipe0.getRecipe();
						if (recipe1==null)
							continue;
						final RecipeIngredients<?> ingredients1 = RecipeIngredients.createFromRecipe(recipe1);
						if (Objects.equals(ingredients, ingredients1))
							rets.add(entry.getKey());
					}
				} else if (type.equalsIgnoreCase("recipe")) {
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED+"That command must be used in game");
						return true;
					}
					final Player player = (Player) sender;
					final PlayerInventory playerinv = player.getInventory();

					if (args.length<=2) {
						sender.sendMessage(ChatColor.RED+"/recipe lookup recipe <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}
					final String argument = args[2];

					RecipeIngredients<?> ingredients;
					if (argument.equalsIgnoreCase("shapeless"))
						ingredients = new ShapelessRecipeIngredients();
					else if (argument.equalsIgnoreCase("shaped"))
						ingredients = new ShapedRecipeIngredients();
					else if (argument.equalsIgnoreCase("shaped_trim"))
						ingredients = new TrimmedShapedRecipeIngredients();
					else if (argument.equalsIgnoreCase("furnace"))
						ingredients = new FurnaceRecipeIngredients();
					else {
						sender.sendMessage(ChatColor.RED+"/recipe lookup ingredient <shaped/shaped_trim/shapeless/furnace>");
						return true;
					}
					final RecipeResult result = new RecipeResult();

					ingredients.fromInventoryItem(playerinv);
					result.fromInventoryItem(playerinv);

					for (final Entry<String, SerializedRecipe> entry : ReRecipeCreators.instance.recipestorage.getRecipes().entrySet()) {
						final SerializedRecipe recipe0 = entry.getValue();
						if (recipe0==null)
							continue;
						final Recipe recipe1 = recipe0.getRecipe();
						if (recipe1==null)
							continue;
						final RecipeResult result1 = new RecipeResult();
						result1.fromRecipe(recipe1);
						final RecipeIngredients<?> ingredients1 = RecipeIngredients.createFromRecipe(recipe1);
						if (Objects.equals(result, result1)&&Objects.equals(ingredients, ingredients1))
							rets.add(entry.getKey());
					}
				} else {
					sender.sendMessage(ChatColor.RED+"/recipe lookup [result/ingredient/recipe/alias]");
					return true;
				}
			} else
				rets.addAll(ReRecipeCreators.instance.recipestorage.getRecipes().keySet());

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
			if (!hasPermission(sender, "rerecipecreators.info")) {
				sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
				return true;
			}

			if (!(args.length>1)) {
				sender.sendMessage(ChatColor.RED+"Usage: /recipe info <recipe-id>");
				return true;
			}
			final String name = args[1];

			final SerializedRecipe recipe = ReRecipeCreators.instance.recipestorage.getRecipe(name);
			if (recipe==null) {
				sender.sendMessage(ChatColor.RED+"No recipes found");
				return true;
			}

			sender.sendMessage("");
			sender.sendMessage(ChatColor.GREEN+"Recipe info for recipe ID '"+ChatColor.YELLOW+args[1]+ChatColor.GREEN+"'");
			sender.sendMessage(ChatColor.GREEN+"Aliases: "+ChatColor.YELLOW+recipe.getAlias());
			final ItemStack itemStack = recipe.getCraftResult();
			if (itemStack!=null)
				sender.sendMessage(ChatColor.YELLOW+"Output: "+ChatColor.GREEN+"["+itemStack.getType().name()+" x "+itemStack.getAmount()+"] "+(itemStack.getEnchantments().size()>0 ? ChatColor.BLUE+"[ENCHANTED]" : ""));
			else
				sender.sendMessage(ChatColor.YELLOW+"Output: "+ChatColor.GRAY+"Default");
			final ItemStack itemStackAuto = recipe.getRecipe().getResult();
			sender.sendMessage(ChatColor.YELLOW+"AutoCraft Output: "+ChatColor.GREEN+"["+itemStackAuto.getType().name()+" x "+itemStackAuto.getAmount()+"] "+(itemStackAuto.getEnchantments().size()>0 ? ChatColor.BLUE+"[ENCHANTED]" : ""));

			final RecipeType type = RecipeType.fromRecipe(recipe.getRecipe());
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
						final ItemStack item = entry.getValue();
						if (item!=null) {
							final MaterialData data = item.getData();
							sender.sendMessage("  "+ChatColor.GREEN+"["+key+"]"+ChatColor.GRAY+" : "+ChatColor.DARK_GREEN+data);
						}
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
			sender.sendMessage(ChatColor.YELLOW+"Permission: "+ChatColor.GREEN+"rerecipecreators.recipes."+name);
			return true;
		}

		if (args[0].equalsIgnoreCase("reload")) {
			if (!hasPermission(sender, "rerecipecreators.reload")) {
				sender.sendMessage(ChatColor.RED+"You don't have permission to do that.");
				return true;
			}

			Recipes.loadRecipes(ChatOutput.create(sender));
			return true;
		}

		if (args[0].equalsIgnoreCase("permissions")) {
			if (!hasPermission(sender, "rerecipecreators.permissioncontrol")) {
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
				sender.sendMessage(ChatColor.YELLOW+"[Re:RecipeCreators] Permissions "+ChatColor.GREEN+"enabled!");
			}
			if (args[1].equalsIgnoreCase("disable")) {
				getConfig().set("Permissions-Enabled", false);
				saveConfig();
				sender.sendMessage(ChatColor.YELLOW+"[Re:RecipeCreators] Permissions "+ChatColor.RED+"disabled!");
			}
			return true;
		}

		sender.sendMessage(ChatColor.RED+"Bad Command Usage. Type /recipe help");
		return true;
	}

}
