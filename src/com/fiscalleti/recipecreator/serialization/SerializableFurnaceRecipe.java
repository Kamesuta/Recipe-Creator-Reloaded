package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;

import org.bukkit.inventory.FurnaceRecipe;

public class SerializableFurnaceRecipe implements Serializable{
	
	public static final int type = SerializedRecipe.TYPE_SHAPED;
	
	SerializableItemStack input;
	SerializableItemStack result;
	
	private static final long serialVersionUID = 5024505828440280152L;
	public SerializableFurnaceRecipe(FurnaceRecipe furnaceRecipe){
		input = new SerializableItemStack(furnaceRecipe.getInput());
		result = new SerializableItemStack(furnaceRecipe.getResult());
	}
	
	public FurnaceRecipe unbox(){
		FurnaceRecipe r = new FurnaceRecipe(this.result.unbox(), this.result.unbox().getData());
		r.setInput(this.input.unbox().getData());
		return r;
	}
	
}
