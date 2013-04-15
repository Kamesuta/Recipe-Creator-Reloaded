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
	
	public String permission;
	
	private int type;
	private String id;
	
	SerializableShapedRecipe shapedRecipe;
	SerializableShapelessRecipe shapelessRecipe;
	
	public SerializedRecipe(SerializableShapedRecipe r, String id){
		this.shapedRecipe = r;
		this.type = SerializedRecipe.TYPE_SHAPED;
		this.id = id;
		this.permission = "recipecreator.recipes." + getResult().getType().name();
	}
	
	public SerializedRecipe(SerializableShapelessRecipe r, String id){
		this.shapelessRecipe = r;
		this.type = SerializedRecipe.TYPE_SHAPELESS;
		this.id = id;
		this.permission = "recipecreator.recipes." + getResult().getType().name();
		
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
	
	public ArrayList<ItemStack> getIngredients(){
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		if(getIngredientMap() != null){
			//Shaped
			for(Character i : getIngredientMap().keySet()){
				ret.add(getIngredientMap().get(i));
			}
			return ret;
		}else{
			//Shapeless
			for(ItemStack i : shapelessRecipe.unbox().getIngredientList()){
				ret.add(i);
			}
			return ret;
		}
	}
	
	public Recipe getRecipe(){
		return (type == SerializedRecipe.TYPE_SHAPED) ? shapedRecipe.unbox() : shapelessRecipe.unbox();
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

	
	

}
