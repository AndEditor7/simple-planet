package com.andedit.planet.gen.noise;

import java.util.Random;

import com.andedit.planet.util.Noise;

public class NormalNoise extends Noise implements NoiseFilter {
	/** The noise amplitude. */
	public float amb = 1;
	
	public NormalNoise() {
		super(new Random().nextInt());
	}
	
	@Override
	public float evaluate(float x, float y, float z) {
		return getConfiguredNoise(x, y, z) * amb;
	}
}
