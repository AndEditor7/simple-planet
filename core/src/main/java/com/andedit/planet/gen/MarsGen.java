package com.andedit.planet.gen;

import static com.badlogic.gdx.Gdx.gl;
import static com.badlogic.gdx.math.Interpolation.pow2Out;
import static com.badlogic.gdx.math.Interpolation.smooth;

import com.andedit.planet.Assets;
import com.andedit.planet.gen.noise.MultiNoise;
import com.andedit.planet.gen.noise.NoiseFilter;
import com.andedit.planet.gen.noise.NormalNoise;
import com.andedit.planet.interfaces.Atmosphere;
import com.andedit.planet.interfaces.MaterialGen;
import com.andedit.planet.interfaces.ShapeGen;
import com.andedit.planet.util.IcoSphere;
import com.andedit.planet.util.Noise;
import com.andedit.planet.util.Operator;
import com.andedit.planet.world.Planet;
import com.andedit.planet.world.SpaceObj;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class MarsGen implements ShapeGen, MaterialGen, Atmosphere {
	
	private final Color light = Color.valueOf("FF825A");
	private final Color dark = Color.valueOf("3D251E");
	private final Color amto = Color.valueOf("FFB8A0");
	
	private final MultiNoise matNoises;
	private final MultiNoise noises;
	private final NoiseFilter noisey;
	
	{
		matNoises = new MultiNoise();
		
		var normal = new NormalNoise();
		normal.setNoiseType(Noise.FOAM_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFoamSharpness(5);
		normal.setFractalOctaves(6 + Planet.QUALITY);
		normal.setFractalGain(0.4f);
		normal.setFrequency(3f);
		normal.amb = 1.3f;
		matNoises.add(normal, (a, b) -> {
			return a + (b-0.25f);
		});
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.FOAM_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFoamSharpness(2);
		normal.setFractalOctaves(5);
		normal.setFractalGain(0.5f);
		normal.setFrequency(5f);
		normal.amb = 0.1f;
		matNoises.add(normal, (a, b) -> {
			return Math.max(a, b+0.2f);
		});
		
		normal = new NormalNoise();
		normal.setFractalOctaves(5 + Planet.QUALITY);
		normal.setFractalGain(0.6f);
		normal.setFrequency(4.0f);
		normal.amb = 0.2f;
		noisey = normal;
		
		
		noises = new MultiNoise();
		noises.amb = 0.06f; // 0.1f
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.HONEY_FRACTAL);
		normal.setFractalType(Noise.RIDGED_MULTI);
		normal.setFractalOctaves(6 + Planet.QUALITY); // 6
		normal.setFractalGain(0.5f);
		normal.setFrequency(2);
		normal.amb = 1f;
		noises.add(normal, Operator.ADD);
		
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.SIMPLEX_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFractalOctaves(4);
		normal.setFractalGain(0.5f);
		normal.setFrequency(1.5f);
		normal.amb = 1.5f;
		noises.add(normal, (a, b) -> {
			return a * (smooth.apply(MathUtils.clamp(b - 0.1f, 0.0f, 1f))+0.3f) ;
		});
		
		normal = new NormalNoise();
		normal.setNoiseType(Noise.SIMPLEX_FRACTAL);
		normal.setFractalType(Noise.FBM);
		normal.setFractalOctaves(6 + Planet.QUALITY); // 6
		normal.setFractalGain(0.55f);
		normal.setFrequency(3f);
		normal.amb = 0.2f; // 0.2f
		noises.add(normal, Operator.ADD);
	}
	
	@Override
	public float genShape(Vector3 point) {
		return noises.evaluate(point)+1f;
	}

	@Override
	public void genMaterial(Vector3 point, Vector3 original, Color colorOut, Material materialOut) {
		float value = matNoises.evaluate(point.x, point.y * 1.2f, point.z);
		value = pow2Out.apply(MathUtils.clamp(value, 0, 1));
		colorOut.set(light).lerp(dark, value);
		colorOut.mul(noisey.evaluate(point)+1f);
		
		materialOut.set(1f, 1f);
		materialOut.scl(value * 0.2f);
	}

	@Override
	public void render(SpaceObj obj) {
		var shader = getShader();
		shader.setUniformf("u_lightDir", obj.getLightDir());
		shader.setUniformf("u_color", amto.r, amto.g, amto.b);
		shader.setUniformf("u_strength", 2.3f);
		shader.setUniformf("u_gamma", 2.5f);
		shader.setUniformMatrix("u_posTrans", obj.getAtmoMat().scl(1.1f));
		
		gl.glDrawElements(GL20.GL_TRIANGLES, IcoSphere.IDXSIZE, GL20.GL_UNSIGNED_INT, 0);
	}

	@Override
	public ShaderProgram getShader() {
		return Assets.ATMOPLANET_SHADER;
	}
}
