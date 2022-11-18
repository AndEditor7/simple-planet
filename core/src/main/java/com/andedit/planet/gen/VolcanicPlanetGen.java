package com.andedit.planet.gen;

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

public class VolcanicPlanetGen implements ShapeGen, MaterialGen, Properties {
	
	private static Color COLOR = Color.valueOf("#E0E0E0");
	private static Color LIGHT = new Color(250/255f, 30/255f, 7/255f, 1f);

	private final MultiNoise shape;
	private final MultiNoise light;
	
	public VolcanicPlanetGen() {
		var rand = new RandomXS128(7138185802731187345L);
		shape = new MultiNoise();
		light = new MultiNoise();
		NormalNoise noise;
		
		noise = new NormalNoise(rand);
		noise.setFractalOctaves(7+Planet.QUALITY);
		noise.setFractalGain(0.75f);
		noise.setFrequency(2);
		light.add(noise, Operator.ADD);
		
		noise = new NormalNoise(rand);
		noise.setNoiseType(Noise.HONEY_FRACTAL);
		noise.setFractalType(Noise.RIDGED_MULTI);
		noise.setFractalOctaves(5+Planet.QUALITY); // 6
		noise.setFractalGain(0.6f);
		noise.setFrequency(5);
		noise.amb = 0.006f;
		shape.add(noise, Operator.ADD);
		
		noise = new NormalNoise(rand);
		noise.setFractalOctaves(5+Planet.QUALITY); // 6
		noise.setFractalGain(0.7f);
		noise.setFrequency(7.5f);
		noise.amb = 0.003f;
		shape.add(noise, Operator.ADD);
	}
	
	@Override
	public float genShape(Vector3 point) {
		return shape.evaluate(point)+1f;
	}
	
	@Override
	public void genMaterial(Vector3 point, Vector3 original, Color colorOut, Material materialOut) {
		colorOut.set(Color.WHITE);
		
		float value = light.evaluate(original);
		float offset = 0.2f;
		if (value > offset) {
			value -= offset;
			colorOut.set(LIGHT);
			colorOut.mul((float)Math.pow(value+1f, 24));
			materialOut.luminance = 1;
		} else {
			materialOut.luminance = 0;
		}
	}
	
	public float getGamma() {
		return 2.0f;
	}
	
	public float getAmb() {
		return 0.2f;
	}
}
