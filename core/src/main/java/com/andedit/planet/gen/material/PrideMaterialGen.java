package com.andedit.planet.gen.material;

import com.andedit.planet.gen.NormalNoise;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class PrideMaterialGen implements MaterialGen {
	
	public Flag flag = Flag.values()[MathUtils.random.nextInt(7)];
	public NormalNoise color = new NormalNoise();
	
	{
		color.setFractalOctaves(3);
		color.setFractalGain(0.6f);
		color.setFrequency(2);
		color.amb = 1.0f; // 0.7
	}

	@Override
	public Color getColor(Vector3 pos, Vector3 original) {
		float y = ((pos.y * 0.55f) + 0.5f) * flag.size();
		y += color.evaluate(pos);
		int i = MathUtils.clamp(MathUtils.floor(y), 0, flag.size()-1);
		
		//float y = (float)(color.evaluate(pos) * flag.size());
		//int i = MathUtils.clamp(MathUtils.floor((y/1.5f)+2f), 0, flag.size()-1);
		
		return flag.get(i);
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
