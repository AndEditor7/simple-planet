package com.andedit.planet.gen;

import java.util.Random;

import com.badlogic.gdx.math.Vector3;

import make.some.noise.Noise;

public class NormalNoise extends Noise implements NoiseFilter {
	/** The noise amplitude. */
	public float amb = 1;
	
	public NormalNoise() {
		super(new Random().nextInt());
	}
	
	@Override
	public float evaluate(Vector3 point) {
		return getConfiguredNoise(point.x, point.y, point.z) * amb;
	}
}
