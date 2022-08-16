package com.andedit.planet.gen.util;

import com.badlogic.gdx.math.Vector3;

import make.some.noise.Noise;

public class NormalNoise extends Noise implements NoiseFilter {
	@Override
	public float evaluate(Vector3 point) {
		return getConfiguredNoise(point.x, point.y, point.z);
	}
}
