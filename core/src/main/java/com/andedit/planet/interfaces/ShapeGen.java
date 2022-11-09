package com.andedit.planet.interfaces;

import com.badlogic.gdx.math.Vector3;

/** Shape generator interface for planet. */
@FunctionalInterface
public interface ShapeGen {
	/** point in sphere and return the scale value. Default value: 1
	 * @param point the point in sphere, do not modify! */
	float genShape(Vector3 point);
}
