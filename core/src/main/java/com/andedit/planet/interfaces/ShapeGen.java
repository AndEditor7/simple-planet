package com.andedit.planet.interfaces;

import com.badlogic.gdx.math.Vector3;

/** Shape generator interface for planet. */
@FunctionalInterface
public interface ShapeGen {
	/** point in sphere to modify */
	void genShape(Vector3 point);
}
