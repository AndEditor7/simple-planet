package com.andedit.planet.gen.noise;

import com.badlogic.gdx.math.Vector3;

/** A NoiseFilter interface. It takes a Vector3 input and output the value. */
@FunctionalInterface
public interface NoiseFilter {
	default float evaluate(Vector3 point) {
		return evaluate(point.x, point.y, point.z);
	}
	
	float evaluate(float x, float y, float z);
}
