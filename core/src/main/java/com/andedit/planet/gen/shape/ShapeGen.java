package com.andedit.planet.gen.shape;

import com.badlogic.gdx.math.Vector3;

@FunctionalInterface
public interface ShapeGen {
	void apply(Vector3 point);
	default void start(boolean genNormal) {
		
	}
}
