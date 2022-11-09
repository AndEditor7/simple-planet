package com.andedit.planet.gen;

/** Material properties for planet generation. */
public class Material {
	/** Specular strength */
	public float specular;
	
	public float shininess;
	
	public void set(float specular, float shininess) {
		this.specular = specular;
		this.shininess = shininess;
		clamp();
	}
	
	public void scl(float value) {
		specular *= value;
		shininess *= value;
		clamp();
	}
	
	public void clamp() {
		if (specular < 0) specular = 0;
		else if (specular > 1) specular = 1;

		if (shininess < 0) shininess = 0;
		else if (shininess > 1) shininess = 1;
	}
}
