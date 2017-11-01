package com.fiscalleti.recipecreator;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

public class Events implements Listener {
	@EventHandler
	public void onRecipe(final CraftItemEvent e) {
		if (RecipeCreator.instance.getConfig().getBoolean("Permissions-Enabled")) {
			/*
			CraftingInventory inventory = e.getInventory();
			InventoryType invtype = inventory.getType();
			if ((invtype==InventoryType.WORKBENCH||invtype==InventoryType.CRAFTING||invtype==InventoryType.FURNACE)) {
				SerializedRecipe r = null;
				for (final SerializedRecipe rec : RecipeCreator.instance.recipestorage.getRecipes())
					if (rec.getRecipe().getResult().getType()==e.getCurrentItem().getType()) {
						r = rec;
						break;
					}
				if (r!=null) {
					if (!(RecipeCreator.instance.getServer().getPlayer(e.getWhoClicked().getName()).hasPermission(r.permission)||RecipeCreator.instance.getServer().getPlayer(e.getWhoClicked().getName()).hasPermission("recipecreator.recipes.*"))) {
						e.setCancelled(true);
						RecipeCreator.instance.getServer().getPlayer(e.getWhoClicked().getName()).sendMessage(ChatColor.RED+"You don't have permission to craft that item");
					}
				} else if (!(e.getCurrentItem()==null||e.getCurrentItem().getType()==null||e.getCurrentItem().getType()==Material.AIR))
					RecipeCreator.instance.console.sendMessage(ChatColor.RED+"[Recipe Creator] Error while checking permission for "+e.getWhoClicked().getName());
			}*/;
			;
		}
	}
}
