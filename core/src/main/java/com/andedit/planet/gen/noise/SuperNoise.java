package com.andedit.planet.gen.noise;

import java.util.Random;

import com.andedit.planet.util.OpenSimplex2S;

public class SuperNoise implements NoiseFilter {
	
	private final long seed = new Random().nextLong();
	
	public float freq = 1;
	public float amb = 1;
	
	@Override
	public float evaluate(float x, float y, float z) {
		return OpenSimplex2S.noise3_Fallback(seed, x * freq, y * freq, z * freq) * amb;
	}
}
