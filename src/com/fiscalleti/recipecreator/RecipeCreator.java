package com.fiscalleti.recipecreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import org.bukkit.plugin.java.JavaPlugin;

import com.fiscalleti.recipecreator.serialization.ObjectHandler;
import com.fiscalleti.recipecreator.serialization.SerializedRecipe;



public class RecipeCreator extends JavaPlugin{
	
	public static RecipeCreator instance;
	public ConsoleCommandSender console;
	@Override
	public void onEnable(){
		instance = this;
		console = getServer().getConsoleSender();
		Recipes.loadRecipes();
		if(!getDataFolder().exists()){
			getDataFolder().mkdir();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("createRecipe")){
			if(!(sender instanceof Player)){
				sender.sendMessage(ChatColor.RED + "That command must be used in game");
				return true;
			}
			Player player = (Player)sender;
			
			if(!(args.length > 1)){
				player.sendMessage(ChatColor.RED + "Usage: /createrecipe <name> [shaped/shapeless]");
				return true;
			}
			
			String type = args[1];
			
			if(!(type.equalsIgnoreCase("shaped") || type.equalsIgnoreCase("shapeless"))){
				player.sendMessage(ChatColor.RED + "Usage: /createrecipe <name> [shaped/shapeless]");
				return true;
			}
			
			if(type.equalsIgnoreCase("shapeless")){
				if(!player.hasPermission("recipecreator.shapeless")){
					player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}
				Recipes.createShapeless(player, args[0]);
			}
			
			if(type.equalsIgnoreCase("shaped")){
				if(!player.hasPermission("recipecreator.shaped")){
					player.sendMessage(ChatColor.RED + "You don't have permission to do that.");
					return true;
				}
				Recipes.createShaped(player, args[0]);
			}		
		}
		
		if(commandLabel.equalsIgnoreCase("removeRecipe")){
			boolean hasperm = (sender instanceof Player) ? ((Player)sender).hasPermission("recipecreator.remove") : true;
			
			if(!hasperm){
				sender.sendMessage(ChatColor.RED + "You don't have permission to do that.");
				return true;
			}
			
			
			if(!(args.length > 0)){
				sender.sendMessage(ChatColor.RED + "Usage: /removerecipe <recipename>");
				return true;
			}
			
			if(Recipes.removeRecipe(args[0])){
				sender.sendMessage(ChatColor.YELLOW + "Recipe removed. You must now reload the server to unload the recipe.");
				return true;
			}else{
				sender.sendMessage(ChatColor.RED + "There was an error while removing that recipe. Doesn it exist?");
				return true;
			}
			
		}
		
		return true;
	}

}
