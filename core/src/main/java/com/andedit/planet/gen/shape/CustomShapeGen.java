package com.andedit.planet.gen.shape;

import static com.badlogic.gdx.math.MathUtils.random;

import java.util.ArrayList;

import com.andedit.planet.gen.util.NoiseFilter;
import com.andedit.planet.gen.util.NormalNoise;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import make.some.noise.Noise;

public class CustomShapeGen implements ShapeGen {
	public final ArrayList<NoiseFilter> filters = new ArrayList<>();
	public float gain = 1;
	public float amp = 1;
	
	{
		var noise = new NormalNoise();
		noise.setSeed(random.nextInt());
		noise.setFractalOctaves(4);
		noise.setFrequency(2f);
		noise.setNoiseType(Noise.SIMPLEX_FRACTAL);
		noise.setFractalType(Noise.FBM);
		amp = 0.2f;
		filters.add(noise);
	}

	@Override
	public void apply(Vector3 point) {
		float value = 0;
		for (var noise : filters) {
			value += noise.evaluate(point);
		}
		float h = 0.1f;
		if (value < h) {
			value = MathUtils.lerp(value, h, 0.7f);
		}
		point.scl(value * amp + 1f);
	}
}
