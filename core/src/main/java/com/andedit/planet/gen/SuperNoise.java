package com.andedit.planet.gen;

import java.util.Random;

import com.andedit.planet.util.OpenSimplex2S;
import com.badlogic.gdx.math.Vector3;

public class SuperNoise implements NoiseFilter {
	
	private final long seed = new Random().nextLong();
	
	public float freq = 1;
	public float amb = 1;

	@Override
	public float evaluate(Vector3 point) {
		return OpenSimplex2S.noise3_Fallback(seed, point.x * freq, point.y * freq, point.z * freq) * amb;
	}
}
