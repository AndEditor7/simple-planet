package com.andedit.planet.util;

import java.util.Random;

public class SimplexNoises {
	private final long[] seeds;
	private double gain = 0.5;
	
	public SimplexNoises(int count) {
		this(count, new Random());
	}
	
	public SimplexNoises(int count, long seed) {
		this(count, new Random(seed));
	}

	public SimplexNoises(int count, Random rng) {
		seeds = new long[count];

		for (int i = 0; i < count; i++) {
			seeds[i] = rng.nextLong();
		}
	}
	
	public SimplexNoises setGain(double gain) {
		this.gain = gain;
		return this;
	}

	public double get(double x, double y) {
		double result = OpenSimplex2S.noise2(seeds[0], x, y), amp = 1;

		for (int i = 1; i < seeds.length; i++) {
			x *= 2d; y *= 2d;
			
			amp *= gain;
			result += OpenSimplex2S.noise2(seeds[i], x, y) * amp;
		}

		return result;
	}

	public double get(double x, double y, double z) {
		double result = OpenSimplex2S.noise3_Fallback(seeds[0], x, y, z), amp = 1;

		for (int i = 1; i < seeds.length; i++) {
			x *= 2d; y *= 2d; z *= 2d;
			
			amp *= gain;
			result += OpenSimplex2S.noise3_Fallback(seeds[i], x, y, z) * amp;
		}

		return result;
	}
}
