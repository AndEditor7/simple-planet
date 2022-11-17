package com.andedit.planet.gen;

import com.andedit.planet.gen.feature.CratersFeature;
import com.andedit.planet.gen.noise.MultiNoise;
import com.andedit.planet.gen.noise.NormalNoise;
import com.andedit.planet.interfaces.MaterialGen;
import com.andedit.planet.interfaces.Properties;
import com.andedit.planet.interfaces.ShapeGen;
import com.andedit.planet.util.Noise;
import com.andedit.planet.util.Operator;
import com.andedit.planet.world.Planet;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

public class MercuryGen implements ShapeGen, MaterialGen, Properties {
	
	private static Color COLOR = Color.valueOf("#E0E0E0");
	
	private final CratersFeature craters;
	private final MultiNoise noises;
	
	public MercuryGen() {
		var rand = new RandomXS128(723749116500712L);
		craters = new CratersFeature(rand, 4000);
		noises = new MultiNoise();
		NormalNoise noise;
		MultiNoise multi;
		
		craters.warping = multi = new MultiNoise();
		noise = new NormalNoise(rand);
		noise.setFractalOctaves(2+Planet.QUALITY); // 2
		noise.setFractalGain(0.7f);
		noise.setFrequency(70f);
		multi.add(noise, Operator.ADD);
		multi.amb = 0.007f;
		
		
		noise = new NormalNoise(rand);
		noise.setNoiseType(Noise.HONEY_FRACTAL);
		noise.setFractalType(Noise.RIDGED_MULTI);
		noise.setFractalOctaves(6+Planet.QUALITY); // 6
		noise.setFractalGain(0.6f);
		noise.setFrequency(8);
		noise.amb = 0.002f;
		noises.add(noise, Operator.ADD);
		
		noise = new NormalNoise(rand);
		noise.setFractalOctaves(6+Planet.QUALITY); // 6
		noise.setFractalGain(0.7f);
		noise.setFrequency(10);
		noise.amb = 0.003f;
		noises.add(noise, Operator.ADD);
	}
	
	@Override
	public float genShape(Vector3 point) {
		float value = noises.evaluate(point);
		value = craters.apply(point, value);
		return value+1f;
	}
	
	@Override
	public void genMaterial(Vector3 point, Vector3 original, Color colorOut, Material materialOut) {
		colorOut.set(COLOR);
		materialOut.set(0.05f, 0.1f);
	}
	
	@Override
	public float getGamma() {
		return 2.0f;
	}
	
	@Override
	public float getAmb() {
		return 0.1f;
	}
}
