package com.andedit.planet.world;

import static com.badlogic.gdx.Gdx.gl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.andedit.planet.Assets;
import com.andedit.planet.Statics;
import com.andedit.planet.util.FastNoise;
import com.andedit.planet.util.IcoSphereGen;
import com.andedit.planet.util.OpenSimplex2S;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Planet implements Disposable {
	
	public static final float SIZE = 1;
	public static final int LEVEL = 6;
	
	private final Vector3[] norms;
	private final Vector3[] mods;
	private final List<Vector3> positions;
	private final List<GridPoint3> indices;
	private final ByteBuffer buffer;
	
	private final int idxSize;
	private final int idxBuf;
	private final int vertBuf;
	
	private final Vector3 lightDir = new Vector3(-0.6f, 0.7f, 0.3f).nor();
	private final float color = Color.toFloatBits(70, 250, 160, 255); // 255, 60, 100
	
	public Planet() {
		positions = new ArrayList<>(100<<LEVEL);
		indices = new IcoSphereGen().create(positions, LEVEL);
		buffer = Statics.buffer(positions.size());
		buffer.clear();
		System.out.println(positions.size());
		
		norms = new Vector3[positions.size()];
		mods = new Vector3[positions.size()];
		for (int i = 0; i < norms.length; i++) {
			norms[i] = new Vector3();
			mods[i] = new Vector3();
		}
		
		var buf = buffer.asIntBuffer();
		for (var tri : indices) {
			buf.put(tri.x);
			buf.put(tri.y);
			buf.put(tri.z);
		}
		buf.flip();
		
		idxBuf = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, idxBuf);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buf.remaining() * Integer.BYTES, buf, GL20.GL_STATIC_DRAW);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		idxSize = buf.remaining();
		
		vertBuf = gl.glGenBuffer();
	}
	
	private final long seed = MathUtils.random.nextLong();
	private double time;
	public void calulate() {
		
		// 1.0f
		time += 0.2f * Gdx.graphics.getDeltaTime();
		
		for (int i = 0; i < norms.length; i++) {
			norms[i].setZero();
			var pos = mods[i].set(positions.get(i));
			float scl = 2.0f;
			float val = (OpenSimplex2S.noise4_ImproveXYZ(seed, pos.x*scl, pos.y*scl, pos.z*scl, time) * 0.3f) + 1f;
			//val += (OpenSimplex2S.noise4_ImproveXYZ(237656804092349L, pos.x*scl*0.5f, pos.y*scl*0.5f, pos.z*scl*0.5f, time) * 0.15f);
			pos.scl(val);
		}
		
		var vec = new Vector3();
		for (var tri : indices) {
			var a = mods[tri.x];
			var b = mods[tri.y];
			var c = mods[tri.z];
			
			vec.set(b.x - a.x, b.y - a.y, b.z - a.z);
			vec.crs(c.x - a.x, c.y - a.y, c.z - a.z).nor();
			
			norms[tri.x].add(vec);
			norms[tri.y].add(vec);
			norms[tri.z].add(vec);
		}
		
		var buf = buffer.asFloatBuffer();
		for (int i = 0; i < positions.size(); i++) {
			var pos = mods[i];
			var nor = norms[i].nor();
			buf.put(pos.x);
			buf.put(pos.y);
			buf.put(pos.z);
			buf.put(color);
			buf.put(nor.x);
			buf.put(nor.y);
			buf.put(nor.z);
			buf.put(30);
			buf.put(0.8f);
		}
		
		buf.flip();
		
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vertBuf);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, buf.remaining() * Float.BYTES, buf, GL20.GL_STREAM_DRAW);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
	}
	
	public void render(Camera camera) {
		var shader = Assets.PLANET_SHADER;
		
		shader.bind();
		shader.setUniformMatrix("u_mat", camera.combined);
		shader.setUniformf("u_position", camera.position);
		shader.setUniformf("u_lightDir", lightDir);
		
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vertBuf);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, idxBuf);
		Assets.PLANET_CONTEXT.setVertexAttributes(null);
		
		gl.glDrawElements(GL20.GL_TRIANGLES, idxSize, GL20.GL_UNSIGNED_INT, 0);
		
		Assets.PLANET_CONTEXT.unVertexAttributes();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void dispose() {
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glDeleteBuffer(idxBuf);
		gl.glDeleteBuffer(vertBuf);
	}
}
