package com.andedit.planet.gen.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/** Material generator interface for planet. */
public interface MaterialGen {
	/** @return color */
	Color getColor(Vector3 point, Vector3 original);
}
