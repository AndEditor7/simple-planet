package com.andedit.planet.world;

import static com.badlogic.gdx.Gdx.gl;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.andedit.planet.Assets;
import com.andedit.planet.Statics;
import com.andedit.planet.gen.material.MaterialGen;
import com.andedit.planet.gen.shape.ShapeGen;
import com.andedit.planet.graphic.SideTextureData;
import com.andedit.planet.thread.CubemapSideTask;
import com.andedit.planet.thread.ShapeGenTask;
import com.andedit.planet.trans.Trans;
import com.andedit.planet.util.IcoSphereGen;
import com.andedit.planet.util.TexBinder;
import com.andedit.planet.util.Util;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Cubemap.CubemapSide;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;

public class Planet implements Disposable {
	
	public static final int LEVEL = 6; // 5 or 6
	public static final int RES = 1024 / 2; // 512
	public static final int SIZE;
	public static final List<Vector3> POSITIONS;
	
	private static final List<GridPoint3> INDICES;
	private static final int IDXBUF, IDXSIZE;
	private static final TexBinder COLOR_BIND = new TexBinder();
	private static final TexBinder NORMAL_BIND = new TexBinder();
	
	public final Vector3 lightDir = new Vector3(-0.6f, 0.2f, 0.25f).nor();
	
	private final Cubemap colorMap;
	private final Cubemap normalMap;
	private final ArrayList<Pixmap> pixmaps;
	private final Object[] locks;
	private final int vertBuf;
	private final ExecutorService executor;
	private final ArrayList<Future<?>> futures;
	private final FloatBuffer buffer;
	
	private ShapeGen shape;
	private MaterialGen material;
	private Trans trans;
	
	private final Matrix4 posMat;
	private final Matrix3 norMat;
	private boolean notCalulated = true;
	private boolean isDisposed;
	
	public Planet() {
		buffer = BufferUtils.newFloatBuffer(SIZE * 3);
		System.out.println("floats: " + buffer.capacity());
		
		locks = new Object[6];
		for (int i = 0; i < locks.length; i++) {
			locks[i] = new Object();
		}
		
		pixmaps = new ArrayList<>(12);
		vertBuf = gl.glGenBuffer();
		
		colorMap  = newCubemap(Format.RGB888);
		COLOR_BIND.bind(colorMap);
		colorMap.unsafeSetFilter(TextureFilter.Linear, TextureFilter.Linear);
		colorMap.unsafeSetAnisotropicFilter(8);
		
		normalMap = newCubemap(Format.RGB888);
		NORMAL_BIND.bind(normalMap);
		normalMap.unsafeSetFilter(TextureFilter.Linear, TextureFilter.Linear);
		normalMap.unsafeSetAnisotropicFilter(8);
		
		TexBinder.deactive();
		
		posMat = new Matrix4();
		norMat = new Matrix3();
		executor = Statics.PLANET_EXECUTOR;
		futures = new ArrayList<>();
	}
	
	public void setShapeGen(ShapeGen shape) {
		this.shape = shape;
	}
	
	public void setMaterialGen(MaterialGen material) {
		this.material = material;
	}
	
	public void setTrans(Trans trans) {
		this.trans = trans;
	}
	
	public void calulate() {
		if (!futures.isEmpty()) return;
		
		submit(new ShapeGenTask(buffer, shape, () -> {
			if (isDisposed) return;
			synchronized (buffer) {
				gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vertBuf);
				gl.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.remaining() * Float.BYTES, buffer, GL20.GL_STREAM_DRAW);
				gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
			}
		}));
		
		for (var side : CubemapSide.values()) {
			var colourData = Util.getTexData(colorMap, side);
			var normalData = Util.getTexData(normalMap, side);
			var colourPix = Util.getPixmap(colourData);
			var normalPix = Util.getPixmap(normalData);
			
			submit(new CubemapSideTask(shape, material, colourPix, normalPix, side, locks[side.index], () -> {
				synchronized (locks[side.index]) {
					if (isDisposed) return;
					COLOR_BIND.bind(colorMap);
					colourData.consumeCustomData(side.glEnum);
					
					NORMAL_BIND.bind(normalMap);
					normalData.consumeCustomData(side.glEnum);
					
					TexBinder.deactive();
				}
			}));
		}
	}
	
	public void update(float deltra) {
		if (trans != null) {
			trans.update(deltra);
			trans.apply(posMat);
			norMat.set(posMat).inv().transpose();
		}
	}
	
	public void render(Camera camera) {
		Assets.PLANET_SHADER.bind();
		render(camera, Assets.PLANET_SHADER);
	}
	
	public void render(Camera camera, ShaderProgram shader) {
		if (!futures.isEmpty() && futures.stream().allMatch(Future::isDone)) {
			futures.clear();
			notCalulated = false;
		}
		if (notCalulated) return;
		
		COLOR_BIND.bind(colorMap);
		// update texture
		
		NORMAL_BIND.bind(normalMap);
		// update texture
		
		shader.setUniformMatrix("u_projTrans", camera.combined);
		shader.setUniformMatrix("u_posTrans", posMat);
		shader.setUniformMatrix("u_normTrans", norMat);
		shader.setUniformf("u_position", camera.position);
		shader.setUniformf("u_lightDir", lightDir);
		shader.setUniformi("u_colorMap", COLOR_BIND.unit);
		shader.setUniformi("u_normalMap", NORMAL_BIND.unit);
		
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vertBuf);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, IDXBUF);
		Assets.PLANET_CONTEXT.setVertexAttributes(null);
		
		gl.glDrawElements(GL20.GL_TRIANGLES, IDXSIZE, GL20.GL_UNSIGNED_INT, 0);
		
		Assets.PLANET_CONTEXT.unVertexAttributes();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		TexBinder.deactive();
	}

	@Override
	public void dispose() {
		isDisposed = true;
		gl.glDeleteBuffer(vertBuf);
		colorMap.dispose();
		normalMap.dispose();
		
		if (!futures.isEmpty()) {
			futures.forEach(f->{
				try {
					f.get();
				} catch (Exception e) {
				}
			});
		}
		
		pixmaps.forEach(Pixmap::dispose);
	}
	
	private Cubemap newCubemap(Format format) {
		return new Cubemap(
		new SideTextureData(addPixmap(new Pixmap(RES, RES, format)), locks[0]),
		new SideTextureData(addPixmap(new Pixmap(RES, RES, format)), locks[1]),
		new SideTextureData(addPixmap(new Pixmap(RES, RES, format)), locks[2]),
		new SideTextureData(addPixmap(new Pixmap(RES, RES, format)), locks[3]),
		new SideTextureData(addPixmap(new Pixmap(RES, RES, format)), locks[4]),
		new SideTextureData(addPixmap(new Pixmap(RES, RES, format)), locks[5]));
	}
	
	private Pixmap addPixmap(Pixmap pixmap) {
		pixmaps.add(pixmap);
		return pixmap;
	}
	
	private void submit(Runnable run) {
		futures.add(executor.submit(run));
	}
	
	static {
		POSITIONS = new ArrayList<>(100<<LEVEL);
		INDICES = new IcoSphereGen().create(POSITIONS, LEVEL);
		var buffer = BufferUtils.newIntBuffer(INDICES.size() * 3);
		SIZE = POSITIONS.size();
		
		for (var tri : INDICES) {
			buffer.put(tri.x);
			buffer.put(tri.y);
			buffer.put(tri.z);
		}
		buffer.flip();
		
		IDXBUF = gl.glGenBuffer();
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, IDXBUF);
		gl.glBufferData(GL20.GL_ELEMENT_ARRAY_BUFFER, buffer.remaining() * Integer.BYTES, buffer, GL20.GL_STATIC_DRAW);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		IDXSIZE = buffer.remaining();
		
		Statics.putBuffer(IDXBUF);
	}
}