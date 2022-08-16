package com.andedit.planet.gen.material;

import com.andedit.planet.util.SimplexNoises;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class PrideGen implements MaterialGen {
	
	private static final double COL_SCL = 2;
	
	public Flag flag = Flag.TRANS;
	public SimplexNoises color = new SimplexNoises(3);
	
	{
		color.setGain(0.6);
	}

	@Override
	public Color getColor(Vector3 pos, Vector3 original) {
		/*
		float y = ((pos.y * 0.55f) + 0.5f) * flag.size();
		y += color.get(pos.x*COL_SCL, pos.y*COL_SCL, pos.z*COL_SCL) * 0.8;
		int i = MathUtils.clamp(MathUtils.floor(y), 0, flag.size()-1);
		*/
		
		float y = (float)(color.get(pos.x*COL_SCL, pos.y*COL_SCL, pos.z*COL_SCL) * flag.size());
		int i = MathUtils.clamp(MathUtils.floor((y/1.5f)+2f), 0, flag.size()-1);
		
		return COLOR.set(flag.get(i));
	}
	
	public static enum Flag {
		LESBIAN(newCol(165, 0, 100), newCol(216, 101, 168), newCol(255, 255, 255), newCol(255, 155, 89), newCol(216, 43, 0)), 
		GAY(newCol(120, 7, 140), newCol(0, 77, 255), newCol(0, 128, 38), newCol(255, 237, 0), newCol(255, 140, 0), newCol(229, 0, 0)), 
		BISEXUAL(newCol(8, 63, 165), newCol(8, 63, 165), newCol(153, 76, 149), newCol(204, 10, 113), newCol(204, 10, 113)), 
		TRANS(newCol(94, 212, 255), newCol(255, 175, 191), newCol(255, 255, 255), newCol(255, 175, 191), newCol(94, 212, 255)), 
		NON_BINARY(newCol(51, 51, 51), newCol(157, 91, 211), newCol(255, 255, 255), newCol(255, 244, 53)), 
		PAN_SEXUAL(newCol(33, 177, 255), newCol(255, 216, 0), newCol(255, 33, 140)), 
		GENDER_FLUID(newCol(51, 63, 191), newCol(35, 35, 35), newCol(193, 26, 219), newCol(255, 255, 255), newCol(255, 117, 162));
		
		private final Color[] colors;
		
		Flag(Color... colors) {
			this.colors = colors;
		}
		
		public Color get(int idx) {
			return colors[idx];
		}
		
		public int size() {
			return colors.length;
		}
		
		private static Color newCol(int r, int g, int b) {
			return new Color(r/255f, g/255f, b/255f, 1);
		}
	}
}
