package com.fiscalleti.recipecreator.serialization;

import java.io.Serializable;

public class RecipeInformation implements Serializable{
	private static final long serialVersionUID = 240936078011108649L;

	private int version;
	private boolean generated;
	
	public RecipeInformation(){
		this.version = 4;
		this.generated = true;
	}
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public boolean isGenerated() {
		return generated;
	}
	public void setGenerated(boolean generated) {
		this.generated = generated;
	}
}
