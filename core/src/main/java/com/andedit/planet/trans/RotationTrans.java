package com.andedit.planet.trans;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class RotationTrans implements Trans {
	private float rad;

	@Override
	public void update(float delta) {
		rad += 0.1f * delta;
	}

	@Override
	public void apply(Matrix4 mat) {
		mat.idt().scl(1).rotateRad(Vector3.Y, rad);
	}

	@Override
	public void reset() {
		rad = 0;
	}
}
