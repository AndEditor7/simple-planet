package com.andedit.planet.gen.shape;

import static com.badlogic.gdx.math.Interpolation.smooth;

import com.andedit.planet.gen.noise.MultiNoise;
import com.andedit.planet.gen.noise.NormalNoise;
import com.andedit.planet.interfaces.ShapeGen;
import com.andedit.planet.util.Noise;
import com.andedit.planet.util.Operator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class SimpleShapeGen implements ShapeGen {

	public final MultiNoise noises;
	
	{
		noises = new MultiNoise();
		noises.amb = 0.07f; // 0.1f
		
		NormalNoise normal = new NormalNoise();
		normal.setNoiseType(Noise.HONEY_FRACTAL);
		normal.setFractalType(Noise.RIDGED_MULTI);
		normal.setFractalOctaves(6); // 6
		normal.setFractalGain(0.5f);
		normal.setFrequency(2);
		normal.amb = 1.2f;
		noises.add(normal, Operator.ADD);
		
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.SIMPLEX_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFractalOctaves(4);
		normal.setFractalGain(0.5f);
		normal.setFrequency(1.5f);
		normal.amb = 1.5f;
		noises.add(normal, (a, b) -> {
			return a * (smooth.apply(MathUtils.clamp(b - 0.1f, 0.0f, 1f))+0.1f) ;
		});
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.SIMPLEX_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFractalOctaves(6); // 6
		normal.setFractalGain(0.55f);
		normal.setFrequency(3f);
		normal.amb = 0.15f; // 0.2f
		noises.add(normal, Operator.ADD);
	}
	
	@Override
	public void genShape(Vector3 point) {
		point.scl(noises.evaluate(point)+1f);
	}

}
