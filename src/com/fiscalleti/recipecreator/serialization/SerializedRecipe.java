package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class SerializedRecipe implements Serializable{
	private static final long serialVersionUID = -996412512091646013L;
	public static final int TYPE_SHAPELESS = 0;
	public static final int TYPE_SHAPED = 1;
	public static final int TYPE_FURNACE = 2;
	
	public boolean defaultbukkit;
	
	public String permission;
	
	private int type;
	private String id;
	
	SerializableShapedRecipe shapedRecipe;
	SerializableShapelessRecipe shapelessRecipe;
	SerializableFurnaceRecipe furnacerecipe;
	
	public static String typeToString(int typein){
		if(typein == TYPE_SHAPELESS){
			return "Shapeless";
		}else if(typein == TYPE_SHAPED){
			return "Shaped";
		}else if(typein == TYPE_FURNACE){
			return "Furnace";
		}else{
			return "Shaped";
		}
	}
	
	public SerializedRecipe(SerializableShapedRecipe r, String id, boolean defaultbukkit){
		this.shapedRecipe = r;
		this.type = SerializedRecipe.TYPE_SHAPED;
		this.id = id;
		this.permission = "recipecreator.recipes." + getResult().getType().name();
		this.defaultbukkit = defaultbukkit;
	}
	
	public SerializedRecipe(SerializableShapelessRecipe r, String id, boolean defaultbukkit){
		this.shapelessRecipe = r;
		this.type = SerializedRecipe.TYPE_SHAPELESS;
		this.id = id;
		this.permission = "recipecreator.recipes." + getResult().getType().name();
		this.defaultbukkit = defaultbukkit;
	}
	
	public SerializedRecipe(SerializableFurnaceRecipe r, String id, boolean defaultbukkit){
		this.furnacerecipe = r;
		this.type = SerializedRecipe.TYPE_FURNACE;
		this.id = id;
		this.permission = "recipecreator.recipes." + getResult().getType().name();
		this.defaultbukkit = defaultbukkit;
	}

	public String[] getShape(){
		return (type == SerializedRecipe.TYPE_SHAPED) ? shapedRecipe.unbox().getShape() : null;
	}
	
	public ItemStack getResult(){
		switch(type){
			case SerializedRecipe.TYPE_FURNACE:
				return furnacerecipe.unbox().getResult();
			case SerializedRecipe.TYPE_SHAPED:
				return shapedRecipe.unbox().getResult();
			case SerializedRecipe.TYPE_SHAPELESS:
				return shapelessRecipe.unbox().getResult();
			default:
				return null;
		}	
	}
	
	public Map<Character, ItemStack> getIngredientMap(){
		return (type == SerializedRecipe.TYPE_SHAPED) ? shapedRecipe.unbox().getIngredientMap() : null;
	}
	
	public ArrayList<ItemStack> getIngredients(){
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		switch(type){
			case SerializedRecipe.TYPE_FURNACE:
				ret.add(furnacerecipe.unbox().getInput());
				return ret;
			case SerializedRecipe.TYPE_SHAPED:
				for(Character i : getIngredientMap().keySet()){
					ret.add(getIngredientMap().get(i));
				}
				return ret;
			case SerializedRecipe.TYPE_SHAPELESS:
				for(ItemStack i : shapelessRecipe.unbox().getIngredientList()){
					ret.add(i);
				}
				return ret;
		    default:
		    	return null;
		}
	}
	
	public Recipe getRecipe(){
		switch (type){
			case SerializedRecipe.TYPE_FURNACE:
				return furnacerecipe.unbox();
			case SerializedRecipe.TYPE_SHAPED:
				return shapedRecipe.unbox();
			case SerializedRecipe.TYPE_SHAPELESS:
				return shapelessRecipe.unbox();
		    default:
		    	return null;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public boolean isDefaultBukkit(){
		return this.defaultbukkit;
	}
	

}
