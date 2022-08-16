package com.andedit.planet.gen.material;

import com.badlogic.gdx.math.Vector3;

/** Generator interface for generating planet. */
public interface MaterialGen {
	Vector3 INFO = new Vector3();
	
	/** @return x for color, y for shimmer, z for specular strength. */
	Vector3 getInfo(Vector3 point, Vector3 original);
	
	default void start() {
		
	}
}
