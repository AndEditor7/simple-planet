package com.andedit.planet.gen.shape;

import static com.badlogic.gdx.math.MathUtils.random;

import com.andedit.planet.gen.noise.MultiNoise;
import com.andedit.planet.gen.noise.NormalNoise;
import com.andedit.planet.interfaces.ShapeGen;
import com.andedit.planet.util.Noise;
import com.andedit.planet.util.Operator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class CustomShapeGen implements ShapeGen {
	public final MultiNoise noises;
	
	{
		noises = new MultiNoise();
		var noise = new NormalNoise();
		noise.setSeed(random.nextInt());
		noise.setFractalOctaves(5); // 9
		noise.setFrequency(1.3f);
		noise.setNoiseType(Noise.SIMPLEX_FRACTAL);
		noise.setFractalType(Noise.FBM);
		noise.setFractalGain(0.5f);
		noises.amb = 0.4f;
		noises.add(noise, Operator.ADD);
	}

	@Override
	public float genShape(Vector3 point) {
		float value = noises.evaluate(point);
		float h = 0.1f;
		if (value < h) {
			value = MathUtils.lerp(value, h, 0.4f);
		}
		return value + 1.0f;
	}
}
