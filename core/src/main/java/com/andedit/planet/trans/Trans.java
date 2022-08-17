package com.andedit.planet.trans;

import com.badlogic.gdx.math.Matrix4;

/** planet transformations component */
public interface Trans {
	void update(float delta);
	void apply(Matrix4 mat);
	void reset();
}
