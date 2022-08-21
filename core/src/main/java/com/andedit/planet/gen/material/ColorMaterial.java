package com.andedit.planet.gen.material;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class ColorMaterial implements MaterialGen {

	private final Color color;
	
	public ColorMaterial(Color color) {
		this.color = color;
	}
	
	@Override
	public Color getColor(Vector3 point, Vector3 original) {
		return color;
	}
}
