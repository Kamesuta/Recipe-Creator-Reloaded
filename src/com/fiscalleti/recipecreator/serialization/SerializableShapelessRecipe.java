package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

public class SerializableShapelessRecipe implements Serializable{
	private static final long serialVersionUID = -3375750271937486423L;
	SerializableItemStack result;
	List<SerializableItemStack> ingredients = new ArrayList<SerializableItemStack>();
	public SerializableShapelessRecipe(ShapelessRecipe r){
		this.result = new SerializableItemStack(r.getResult());
		for(ItemStack i : r.getIngredientList()){
			ingredients.add(new SerializableItemStack(i));
		}
	}
	
	public ShapelessRecipe unbox(){
		ShapelessRecipe r = new ShapelessRecipe(this.result.unbox());
		for(SerializableItemStack i : ingredients){
			r.addIngredient(i.unbox().getData());
		}
		return r;
	}
}
