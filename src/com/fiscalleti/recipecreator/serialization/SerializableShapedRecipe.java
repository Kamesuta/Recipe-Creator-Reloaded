package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class SerializableShapedRecipe implements Serializable{
	private static final long serialVersionUID = 3801078178397385670L;
	String[] shape;
	Map<Character, SerializableItemStack> ingredientMap;
	SerializableItemStack result;
	public SerializableShapedRecipe(ShapedRecipe recipe){
		this.shape = recipe.getShape();
		ConcurrentHashMap<Character, SerializableItemStack> m = new ConcurrentHashMap<Character, SerializableItemStack>();
		for(Character c : recipe.getIngredientMap().keySet()){
			ItemStack ing = recipe.getIngredientMap().get(c);
			if(ing != null){
				SerializableItemStack i = new SerializableItemStack(ing);
				m.put(c, i);
			}
		}
		this.ingredientMap = m;
		this.result = new SerializableItemStack(recipe.getResult());
	}
	
	public ShapedRecipe unbox(){
		ShapedRecipe s = new ShapedRecipe(this.result.unbox());
		s.shape(this.shape);
		for(Character c : this.ingredientMap.keySet()){
			s.setIngredient(c, this.ingredientMap.get(c).unbox().getData());
		}
		return s;
	}
	
}
