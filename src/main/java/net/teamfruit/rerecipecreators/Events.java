package net.teamfruit.rerecipecreators;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;

public class Events implements Listener {
	@EventHandler
	public void onRecipe(final CraftItemEvent e) {
		if (ReRecipeCreators.instance.getConfig().getBoolean("Permissions-Enabled")) {
			final CraftingInventory inventory = e.getInventory();
			final InventoryType invtype = inventory.getType();
			if (invtype==InventoryType.WORKBENCH||invtype==InventoryType.CRAFTING||invtype==InventoryType.FURNACE) {
				final String name = ReRecipeCreators.instance.recipestorage.getIDFromRecipe(e.getRecipe());
				if (name!=null) {
					final Player player = ReRecipeCreators.instance.getServer().getPlayer(e.getWhoClicked().getUniqueId());
					if (!ReRecipeCreators.hasPermission(player, "rerecipecreators.recipes."+name)) {
						e.setCancelled(true);
						player.sendMessage(ChatColor.RED+"You don't have permission to craft that item");
					}
				}
			}
		}
	}
}
