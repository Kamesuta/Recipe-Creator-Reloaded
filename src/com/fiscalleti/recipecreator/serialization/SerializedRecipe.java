package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.Map;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class SerializedRecipe implements Serializable{
	private static final long serialVersionUID = -996412512091646013L;
	public static final int TYPE_SHAPELESS = 0;
	public static final int TYPE_SHAPED = 1;
	
	private int type;
	private String name;
	
	SerializableShapedRecipe shapedRecipe;
	SerializableShapelessRecipe shapelessRecipe;
	
	public SerializedRecipe(SerializableShapedRecipe r, String name){
		this.shapedRecipe = r;
		this.type = SerializedRecipe.TYPE_SHAPED;
		this.name = name;
	}
	
	public SerializedRecipe(SerializableShapelessRecipe r, String name){
		this.shapelessRecipe = r;
		this.type = SerializedRecipe.TYPE_SHAPELESS;
		this.name = name;
	}

	public String[] getShape(){
		return (type == SerializedRecipe.TYPE_SHAPED) ? shapedRecipe.unbox().getShape() : null;
	}
	
	public ItemStack getResult(){
		return (type == SerializedRecipe.TYPE_SHAPED) ? shapedRecipe.unbox().getResult() : shapelessRecipe.unbox().getResult();
	}
	
	public Map<Character, ItemStack> getIngredientMap(){
		return (type == SerializedRecipe.TYPE_SHAPED) ? shapedRecipe.unbox().getIngredientMap() : null;
	}
	
	public Recipe getRecipe(){
		return (type == SerializedRecipe.TYPE_SHAPED) ? shapedRecipe.unbox() : shapelessRecipe.unbox();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
