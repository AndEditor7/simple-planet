package com.andedit.planet.gen.shape;

import com.andedit.planet.util.SimplexNoises;
import com.andedit.planet.util.Util;
import com.badlogic.gdx.math.Vector3;

public class SimpleShapeGen implements ShapeGen {
	
	private static final double SCL = 1.8;

	public SimplexNoises map = new SimplexNoises(5);
	
	{
		map.setGain(0.55);
	}
	
	@Override
	public void apply(Vector3 point) {
		var val = map.get(point.x*SCL, point.y*SCL, point.z*SCL);
		if (val < 0.1) {
			val = Util.lerp(val, 0, 0.7);
		}
		val *= 0.06;
		val += 1.0;
		point.scl((float)val);
	}

}
