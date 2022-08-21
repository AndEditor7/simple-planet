package com.andedit.planet.gen;

import com.badlogic.gdx.math.Vector3;

/** A NoiseFilter interface. It takes a Vector3 input and output the value. */
@FunctionalInterface
public interface NoiseFilter {
	float evaluate(Vector3 point);
}
