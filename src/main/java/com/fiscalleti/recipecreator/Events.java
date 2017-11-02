package com.fiscalleti.recipecreator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
	@EventHandler
	public void onRecipe(final CraftItemEvent e) {
		if (RecipeCreator.instance.getConfig().getBoolean("Permissions-Enabled")) {
			final CraftingInventory inventory = e.getInventory();
			final InventoryType invtype = inventory.getType();
			if (invtype==InventoryType.WORKBENCH||invtype==InventoryType.CRAFTING||invtype==InventoryType.FURNACE) {
				final String name = RecipeCreator.instance.recipestorage.getIDFromRecipe(e.getRecipe());
				if (name!=null) {
					final Player player = RecipeCreator.instance.getServer().getPlayer(e.getWhoClicked().getUniqueId());
					if (!RecipeCreator.hasPermission(player, "recipecreator.recipes."+name)) {
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED+"You don't have permission to craft that item");
					}
				} else {
					final ItemStack current = e.getCurrentItem();
					if (!(current==null||current.getType()==null||current.getType()==Material.AIR))
						RecipeCreator.instance.console.sendMessage(ChatColor.RED+"[Recipe Creator] Error while checking permission for "+e.getWhoClicked().getName());
				}
			}
		}
	}
}
