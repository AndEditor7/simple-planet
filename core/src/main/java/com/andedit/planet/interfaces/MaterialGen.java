package com.andedit.planet.interfaces;

import com.andedit.planet.gen.Material;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/** Material generator interface for planet. */
@FunctionalInterface
public interface MaterialGen {
	/** @param point of noise filtered position in sphere.
	 *  @param original position in sphere.
	 *  @param colorOut
	 *  @param materialOut */
	void genMaterial(Vector3 point, Vector3 original, Color colorOut, Material materialOut);
}
