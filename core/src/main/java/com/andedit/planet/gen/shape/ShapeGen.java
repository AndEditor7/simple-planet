package com.andedit.planet.gen.shape;

import com.badlogic.gdx.math.Vector3;

/** Shape generator interface for planet. */
@FunctionalInterface
public interface ShapeGen {
	void apply(Vector3 point);
}
