package com.andedit.planet.interfaces;

/** Properties interface for planet. */
public interface Properties {
	Properties INSTANCE = new Properties() {};
	
	default float getShininessMax() {
		return 64;
	}
	
	default float getSpecularMax() {
		return 1;
	}
	
	default float getGamma() {
		return 2.0f;
	}
	
	/** Ambient brightness in front of the sunlight. */
	default float getAmb() {
		return 0.2f;
	}
}
