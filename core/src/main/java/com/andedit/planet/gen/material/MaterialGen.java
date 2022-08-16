package com.andedit.planet.gen.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/** Generator interface for generating planet. */
public interface MaterialGen {
	Color COLOR = new Color();
	
	/** @return x for color, y for shimmer, z for specular strength. */
	Color getColor(Vector3 point, Vector3 original);
	
	default void start() {
		
	}
}
