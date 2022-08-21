package com.andedit.planet.gen.shape;

import com.andedit.planet.gen.MultiNoise;
import com.andedit.planet.gen.NormalNoise;
import com.andedit.planet.util.Operator;
import com.badlogic.gdx.math.Vector3;

import make.some.noise.Noise;

public class SimpleShapeGen implements ShapeGen {

	public final MultiNoise noises;
	
	{
		noises = new MultiNoise();
		noises.amb = 0.1f;
		
		NormalNoise normal = new NormalNoise();
		normal.setNoiseType(Noise.HONEY_FRACTAL);
		normal.setFractalType(Noise.RIDGED_MULTI);
		normal.setFractalOctaves(6); // 4
		normal.setFractalGain(0.5f);
		normal.setFrequency(2);
		normal.amb = 1.5f;
		noises.add(normal, Operator.ADD);
		
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.SIMPLEX_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFractalOctaves(2);
		normal.setFractalGain(0.5f);
		normal.setFrequency(1.5f);
		normal.amb = 1;
		noises.add(normal, (a, b) -> {
			return a * Math.max(0.2f, b) ;
		});
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.SIMPLEX_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFractalOctaves(7); // 5
		normal.setFractalGain(0.55f);
		normal.setFrequency(3.5f);
		normal.amb = 0.2f;
		noises.add(normal, Operator.ADD);
	}
	
	@Override
	public void apply(Vector3 point) {
		point.scl(noises.evaluate(point)+1f);
	}

}
