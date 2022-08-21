package com.andedit.planet.gen;

import com.andedit.planet.gen.material.MaterialGen;
import com.andedit.planet.gen.shape.ShapeGen;
import com.andedit.planet.util.Operator;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import make.some.noise.Noise;

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
	public void apply(Vector3 point) {
		point.scl(filter.evaluate(point)+1f);
	}
	
	@Override
	public Color getColor(Vector3 point, Vector3 original) {
		return Color.WHITE;
	}
}
