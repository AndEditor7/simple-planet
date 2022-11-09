package com.andedit.planet.gen.feature;

import java.util.random.RandomGenerator;

import com.andedit.planet.util.Util;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Interpolation.ExpIn;

public class CratersFeature {
	
	private final int amount;
	private final Crater cratersLocs[];
	
	public CratersFeature(RandomGenerator random, int amount, double bias) {
		cratersLocs = new Crater[this.amount = amount];
		var exp = new ExpIn(2, 30);
		for (int i = 0; i < amount; i++) {
			float size = MathUtils.lerp(0.015f, 0.2f, exp.apply(random.nextFloat()));
			
			float u = random.nextFloat();
			float v = random.nextFloat();
			float theta = MathUtils.PI2 * u; // azimuthal angle
			float phi = MathUtils.acos(2f * v - 1f); // polar angle
			cratersLocs[i] = new Crater(new Vector3().setFromSpherical(theta, phi), size);
		}
	}
	
	public float apply(Vector3 point) {
		var value = 0f;
		var vector = new Vector3(point).nor();
		for (var crater : cratersLocs) {
			float x = vector.dst(crater.location) / crater.size;
			value += function(x) * 0.001f;
		}
		return value;
	}
	
	public static float function(float x) {
		float val = 0;
		float depth = 0.6f;
		float min = 0.5f;
		float size = 0.1f;
		if (x > min) {
			float start = (x-min) / size;
			if (start > 1f) {
				val = lerp(1f, depth, (start-1f));
			} else {
				val = lerp(val, 1f, start);
			}
		}
		return val - depth;
	}
	
	public static float lerp (float fromValue, float toValue, float p) {
		p = Math.min(p, 1f);
		return fromValue + (toValue - fromValue) * (p * p * (3 - 2 * p));
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
