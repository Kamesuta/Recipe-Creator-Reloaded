package com.fiscalleti.recipecreator;

import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

public class Functions {
	public static String is2s(final ItemStack i) {
		final String data = !(i.getData()==null)&&!(i.getData().getData()==(byte) 0) ? ":"+String.valueOf(i.getData().getData()) : "";
		return i.getType().name()+data;
	}

	public static String stringArrayToString(final String[] in) {
		String ret = "";
		for (final String s : in)
			ret = ret.equalsIgnoreCase("") ? s : ret+", "+s;
		return ret;
	}

	public static boolean permissionUsed(final String perm) {
		for (final Permission p : RecipeCreator.instance.getServer().getPluginManager().getPermissions())
			if (p.getName().equalsIgnoreCase(perm))
				return true;
		return false;
	}

}
