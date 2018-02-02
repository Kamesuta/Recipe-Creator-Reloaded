package net.teamfruit.rerecipecreators;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;

import net.teamfruit.rerecipecreators.serialization.SerializedRecipe;

public class Events implements Listener {
	public Events(final Plugin plugin) {
	}

	@EventHandler
	public void onPreItemCrafted(final PrepareItemCraftEvent e) {
		final Inventory inv = e.getInventory();
		if (inv instanceof CraftingInventory) {
			final CraftingInventory inventory = (CraftingInventory) inv;
			final InventoryType invtype = inventory.getType();
			if (invtype==InventoryType.WORKBENCH||invtype==InventoryType.CRAFTING||invtype==InventoryType.FURNACE) {
				final Pair<String, SerializedRecipe> name = ReRecipeCreators.instance.recipestorage.getFromIngredients(inventory.getRecipe());
				if (name!=null) {
					boolean failed = false;
					if (ReRecipeCreators.instance.getConfig().getBoolean("Permissions-Enabled"))
						for (final HumanEntity player : e.getViewers())
							if (!hasPermission(player, name)) {
								failed = true;
								break;
							}
					if (!failed) {
						final SerializedRecipe recipe = name.getRight();
						if (recipe!=null)
							inventory.setResult(recipe.getCraftResult());
					} else
						inventory.setResult(null);
				}
			}
		}
	}

	@EventHandler
	public void onItemCrafted(final CraftItemEvent e) {
		if (ReRecipeCreators.instance.getConfig().getBoolean("Permissions-Enabled")) {
			final CraftingInventory inventory = e.getInventory();
			final InventoryType invtype = inventory.getType();
			if (invtype==InventoryType.WORKBENCH||invtype==InventoryType.CRAFTING||invtype==InventoryType.FURNACE) {
				final Pair<String, SerializedRecipe> name = ReRecipeCreators.instance.recipestorage.getFromIngredients(e.getRecipe());
				if (name!=null) {
					final Player player = ReRecipeCreators.instance.getServer().getPlayer(e.getWhoClicked().getUniqueId());
					if (!hasPermission(player, name)) {
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED+"You don't have permission to craft that item");
					}
				}
			}
		}
	}

	private boolean hasPermission(final Permissible player, final Pair<String, SerializedRecipe> name) {
		if (!ReRecipeCreators.hasPermission(player, "rerecipecreators.recipes."+name.getLeft()))
			return false;
		final Set<String> aliases = name.getRight().getAlias();
		for (final String alias : aliases)
			if (!ReRecipeCreators.hasPermission(player, "rerecipecreators.recipes."+alias))
				return false;
		return true;
	}

	/*
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent e) {
		if ("".isEmpty())
			return;
		this.plugin.getServer().getScheduler().runTask(this.plugin, new Runnable() {
			@Override
			public void run() {
				final Inventory inv = e.getInventory();
				if (inv instanceof CraftingInventory) {
					final CraftingInventory inventory = (CraftingInventory) inv;
					final InventoryType invtype = inventory.getType();
					if (invtype==InventoryType.WORKBENCH||invtype==InventoryType.CRAFTING||invtype==InventoryType.FURNACE) {
	
						Bukkit.getLogger().info(e.getAction()+" : "+ArrayUtils.toString(inventory.getContents()));
						final Pair<String, SerializedRecipe> name = ReRecipeCreators.instance.recipestorage.getFromIngredients(inventory);
						if (name!=null) {
							boolean failed = false;
							for (final HumanEntity player : e.getViewers())
								if (!ReRecipeCreators.hasPermission(player, "rerecipecreators.recipes."+name.getLeft())) {
									failed = true;
									break;
								}
							if (!failed) {
								final Recipe recipe = name.getRight().getRecipe();
								if (recipe!=null)
									inventory.setResult(recipe.getResult());
							} else
								inventory.setResult(null);
						}
					}
				}
			}
		});
	}*/
}
