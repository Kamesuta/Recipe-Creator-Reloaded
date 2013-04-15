package com.fiscalleti.recipecreator;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

public class Functions {
	public static boolean isInt(String in){
		try{
			Integer.parseInt(in);
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	
	public static String is2s(ItemStack i){
		String data = (!(i.getData() == null) && !(i.getData().getData() == (byte)0)) ? ":" + String.valueOf(i.getData().getData()) : "";
		return i.getType().name() + data;
	}
	
	public static String stringArrayToString(String[] in){
		String ret = "";
		for(String s : in){
			ret = (ret.equalsIgnoreCase("")) ? s : ret + ", " + s;
		}
		return ret;
	}
	
	public static boolean permissionUsed(String perm){
		for(Permission p : RecipeCreator.instance.getServer().getPluginManager().getPermissions()){
			if(p.getName().equalsIgnoreCase(perm)){
				return true;
			}
		}
		return false;
	}
	
	
}
