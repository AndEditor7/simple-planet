package com.andedit.planet.gen;

import com.andedit.planet.gen.feature.CratersFeature;
import com.andedit.planet.interfaces.MaterialGen;
import com.andedit.planet.interfaces.ShapeGen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

public class MercuryGen implements ShapeGen, MaterialGen {
	
	private final CratersFeature craters;
	
	public MercuryGen() {
		var rand = new RandomXS128(723749116500712L);
		craters = new CratersFeature(rand, 500, 10);
	}
	
	@Override
	public float genShape(Vector3 point) {
		return craters.apply(point);
	}
	
	@Override
	public void genMaterial(Vector3 point, Vector3 original, Color colorOut, Material materialOut) {
		colorOut.set(Color.LIGHT_GRAY);
		materialOut.set(0.05f, 0.1f);
	}
}
