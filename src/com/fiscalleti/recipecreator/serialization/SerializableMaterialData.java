package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;

import org.bukkit.Material;
import org.bukkit.material.MaterialData;

public class SerializableMaterialData implements Serializable{
	private static final long serialVersionUID = -1658699504610420686L;
	private final byte data;
	private final Material m; 
	public SerializableMaterialData(MaterialData data1){
		this.data = data1.getData();
		this.m = data1.getItemType();
	}
	
	public MaterialData unbox(){
		MaterialData m = new MaterialData(this.m);
		m.setData(this.data);
		
		return m;
	}
}
