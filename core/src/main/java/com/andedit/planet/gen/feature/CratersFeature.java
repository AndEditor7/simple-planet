package com.andedit.planet.gen.feature;

import static com.badlogic.gdx.math.Interpolation.smooth;

import java.util.random.RandomGenerator;

import com.andedit.planet.gen.noise.NoiseFilter;
import com.badlogic.gdx.math.Interpolation.ExpIn;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CratersFeature {
	
	public static final float DEPTH = 0.6f;
	
	private static final float MIN_SIZE = 0.01f;
	
	public NoiseFilter warping = NoiseFilter.ZERO;
	
	private final int amount; // 5000
	private final Crater cratersLocs[];
	
	public CratersFeature(RandomGenerator random, int amount) {
		cratersLocs = new Crater[this.amount = amount];
		var exp = new ExpIn(2, 12);
		for (int i = 0; i < amount; i++) {
			float size = MathUtils.lerp(MIN_SIZE, 0.08f, exp.apply(random.nextFloat()));
			
			float u = random.nextFloat();
			float v = random.nextFloat();
			float theta = MathUtils.PI2 * u; // azimuthal angle
			float phi = MathUtils.acos(2f * v - 1f); // polar angle
			cratersLocs[i] = new Crater(new Vector3().setFromSpherical(theta, phi), size);
		}
	}
	
	public float apply(Vector3 point, float value) {
		var warp = warping.evaluate(point);
		var vector = new Vector3(point).nor();
		for (var crater : cratersLocs) {
			float x = (vector.dst(crater.location)+warp) / crater.size;
			float min = 0.5f;
			float size = 0.32f * (1f-((crater.size-MIN_SIZE)*0.7f));
			value *= flatten(x, min, size);
			value += function(x, min, size) * 0.0018f;
		}
		return value;
	}
	
	public static float function(float x, float min, float size) {
		float val = 0;
		size *= 0.5f;
		if (x > min) {
			float start = (x-min) / size;
			if (start > 1f) {
				val = smooth.apply(1f, DEPTH, Math.min(start-1f, 1f));
			} else {
				val = smooth.apply(val, 1f, Math.min(start, 1f));
			}
		}
		return val - DEPTH;
	}
	
	public static float flatten(float x, float min, float size) {
		float val = 0;
		size *= 0.5f;
		if (x > min) {
			float start = (x-min) / size;
			if (start > 1f) {
				val = smooth.apply(Math.min(start-1f, 1f));
			}
		}
		return MathUtils.lerp(val, 1f, 0.4f); // 0.3f
	}
	
	private class Crater {
		final Vector3 location;
		final float size;
		
		Crater(Vector3 location, float size) {
			this.location = location;
			this.size = size;
		}
	}
}
