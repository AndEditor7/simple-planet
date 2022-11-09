package com.andedit.planet.gen;

import com.andedit.planet.gen.noise.MultiNoise;
import com.andedit.planet.gen.noise.NoiseFilter;
import com.andedit.planet.gen.noise.NormalNoise;
import com.andedit.planet.gen.noise.SuperNoise;
import com.andedit.planet.interfaces.MaterialGen;
import com.andedit.planet.interfaces.ShapeGen;
import com.andedit.planet.util.Noise;
import com.andedit.planet.util.Operator;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class EarthGen implements ShapeGen, MaterialGen {
	
	private final NoiseFilter filter;
	
	{
		var noises = new MultiNoise();
		
		var noise = new NormalNoise();
		noise.amb = 0.15f;
		noise.setFrequency(2);
		noise.setInterpolation(Noise.LINEAR);
		noise.setNoiseType(Noise.SIMPLEX);
		noises.add(noise, Operator.ADD);
		
		var simplex = new SuperNoise();
		simplex.freq = 2;
		simplex.amb = 0.15f;
		
		filter = noise;
	}

	@Override
	public void genShape(Vector3 point) {
		point.scl(filter.evaluate(point)+1f);
	}
	
	@Override
	public void genMaterial(Vector3 point, Vector3 original, Color colorOut, Material materialOut) {
		colorOut.set(Color.WHITE);
	}
}
