package com.andedit.planet.world;

import static com.badlogic.gdx.Gdx.gl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.andedit.planet.Assets;
import com.andedit.planet.Statics;
import com.andedit.planet.gen.material.MaterialGen;
import com.andedit.planet.gen.shape.ShapeGen;
import com.andedit.planet.util.IcoSphereGen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Planet implements Disposable {
	
	public static final int LEVEL = 6; // 6 or 7
	
	private static final List<Vector3> POSITIONS;
	private static final List<GridPoint3> INDICES;
	private static final ByteBuffer BUFFER;
	private static final int IDXBUF, IDXSIZE;
	private static final int SIZE;
	
	public final Vector3 lightDir = new Vector3(-0.6f, 0.7f, 0.3f).nor();
	
	private final Vector3[] normals;
	private final Vector3[] positions;
	
	private final int vertBuf;
	
	private ShapeGen shape;
	private MaterialGen material;
	
	public Planet() {
		normals = new Vector3[SIZE];
		positions = new Vector3[SIZE];
		
		for (int i = 0; i < SIZE; i++) {
			normals[i] = new Vector3();
			positions[i] = new Vector3();
		}
		
		vertBuf = gl.glGenBuffer();
	}
	
	public void setShapeGen(ShapeGen shape) {
		this.shape = shape;
	}
	
	public void setMaterialGen(MaterialGen material) {
		this.material = material;
	}
	
	public void calulate() {
		shape.start();
		for (int i = 0; i < normals.length; i++) {
			normals[i].setZero();
			shape.apply(positions[i].set(POSITIONS.get(i)));
		}
		
		var vec = new Vector3();
		for (var tri : INDICES) {
			var a = positions[tri.x];
			var b = positions[tri.y];
			var c = positions[tri.z];
			
			vec.set(b.x - a.x, b.y - a.y, b.z - a.z);
			vec.crs(c.x - a.x, c.y - a.y, c.z - a.z).nor();
			
			normals[tri.x].add(vec);
			normals[tri.y].add(vec);
			normals[tri.z].add(vec);
		}
		
		material.start();
		var buf = BUFFER.asFloatBuffer();
		for (int i = 0; i < SIZE; i++) {
			var pos = positions[i];
			var nor = normals[i].nor();
			var info = material.getInfo(pos, POSITIONS.get(i));
			
			buf.put(pos.x);
			buf.put(pos.y);
			buf.put(pos.z);
			buf.put(info.x); // color
			buf.put(nor.x);
			buf.put(nor.y);
			buf.put(nor.z);
			buf.put(info.y); // shimmer
			buf.put(info.z); // specular strength
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
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, IDXBUF);
		Assets.PLANET_CONTEXT.setVertexAttributes(null);
		
		gl.glDrawElements(GL20.GL_TRIANGLES, IDXSIZE, GL20.GL_UNSIGNED_INT, 0);
		
		Assets.PLANET_CONTEXT.unVertexAttributes();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void dispose() {
		gl.glDeleteBuffer(vertBuf);
	}
	
	static {
		POSITIONS = new ArrayList<>(100<<LEVEL);
		INDICES = new IcoSphereGen().create(POSITIONS, LEVEL);
		BUFFER = Statics.buffer(POSITIONS.size());
		BUFFER.clear();
		SIZE = POSITIONS.size();
		
		var buf = BUFFER.asIntBuffer();
		for (var tri : INDICES) {
			buf.put(tri.x);
			buf.put(tri.y);
			buf.put(tri.z);
		}
		buf.flip();
		
		IDXBUF = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, IDXBUF);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buf.remaining() * Integer.BYTES, buf, GL20.GL_STATIC_DRAW);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		IDXSIZE = buf.remaining();
		
		Statics.putBuffer(IDXBUF);
	}
}