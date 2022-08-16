package com.andedit.planet.gen.util;

import com.badlogic.gdx.math.Vector3;

@FunctionalInterface
public interface NoiseFilter {
	float evaluate(Vector3 point);
}
