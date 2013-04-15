package com.fiscalleti.recipecreator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.fiscalleti.recipecreator.serialization.SerializedRecipe;

public class Events implements Listener{
	@EventHandler
	public void onRecipe(CraftItemEvent e){
		if(RecipeCreator.instance.getConfig().getBoolean("Permissions-Enabled")){
			SerializedRecipe r = null;
			for(SerializedRecipe rec : Recipes.getRecipes()){
				if(rec.getResult().getType() == e.getCurrentItem().getType()){
					r = rec;
					break;
				}
			}
			if(r != null){
				if(!((RecipeCreator.instance.getServer().getPlayer(e.getWhoClicked().getName()).hasPermission(r.permission)) || (RecipeCreator.instance.getServer().getPlayer(e.getWhoClicked().getName()).hasPermission("recipecreator.recipes.*")))){
					e.setCancelled(true);
					RecipeCreator.instance.getServer().getPlayer(e.getWhoClicked().getName()).sendMessage(ChatColor.RED + "You don't have permission to craft that item");
				}
			}else{
				if(!(e.getCurrentItem() == null || e.getCurrentItem().getType() == null || e.getCurrentItem().getType() == Material.AIR)){
					RecipeCreator.instance.console.sendMessage(ChatColor.RED + "[Recipe Creator] Error while checking permission for " + e.getWhoClicked().getName());
				}
			}
		}
	}
}
