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
import com.andedit.planet.util.TexBinder;
import com.andedit.planet.util.Util;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Cubemap.CubemapSide;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.FacedCubemapData;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

public class Planet implements Disposable {
	
	public static final int LEVEL = 5; // 6 or 7
	public static final int RES = 1024;
	
	private static final List<Vector3> POSITIONS;
	private static final List<GridPoint3> INDICES;
	private static final ByteBuffer BUFFER;
	private static final int IDXBUF, IDXSIZE;
	private static final int SIZE;
	private static final TexBinder COLOR_BIND = new TexBinder();
	private static final TexBinder NORMAL_BIND = new TexBinder();
	
	public final Vector3 lightDir = new Vector3(-0.6f, 0.7f, 0.3f).nor();
	
	private final Cubemap colorMap;
	private final Cubemap normalMap;
	private final ArrayList<Pixmap> pixmaps;
	private final int vertBuf;
	
	private ShapeGen shape;
	private MaterialGen material;
	
	public Planet() {
		pixmaps = new ArrayList<>(12);
		vertBuf = gl.glGenBuffer();
		
		colorMap  = newCubemap(Format.RGB888, false);
		COLOR_BIND.bind(colorMap);
		colorMap.unsafeSetFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		normalMap = newCubemap(Format.RGB888, false);
		NORMAL_BIND.bind(normalMap);
		normalMap.unsafeSetFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TexBinder.deactive();
	}
	
	public void setShapeGen(ShapeGen shape) {
		this.shape = shape;
	}
	
	public void setMaterialGen(MaterialGen material) {
		this.material = material;
	}
	
	public void calulate() {
		shape.start(false);
		var pos = new Vector3();
		var buf = BUFFER.asFloatBuffer();
		for (int i = 0; i < SIZE; i++) {
			shape.apply(pos.set(POSITIONS.get(i)));
			buf.put(pos.x);
			buf.put(pos.y);
			buf.put(pos.z);
		}
		buf.flip();
		
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, vertBuf);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, buf.remaining() * Float.BYTES, buf, GL20.GL_STREAM_DRAW);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		
		var org  = new Vector3();
		var rite = new Vector3();
		var uPos = new Vector3();
		var vPos = new Vector3();
		shape.start(true);
		material.start();
		for (var side : CubemapSide.values()) {
			var colorPix  = Util.getPixmap(colorMap, side);
			var normalPix = Util.getPixmap(normalMap, side);
			
			var up = side.up;
			var dir = side.direction;
			rite.set(dir).crs(up);
			System.out.println("side: " + side);
			System.out.println("dir: " + dir);
			System.out.println("up: " + up);
			System.out.println("rite: " + rite);
			
			float mask = RES - 1;
			for (int x = 0; x < RES; x++) {
				float u = x / mask;
				uPos.set(rite).scl(u).sub(rite.x/2f, rite.y/2f, rite.z/2f).scl(2);
				for (int y = 0; y < RES; y++) {
					float v = y / mask;
					vPos.set(up).scl(v).sub(up.x/2f, up.y/2f, up.z/2f).scl(2);
					org.set(uPos).add(vPos).add(dir).nor();
					
					normalPix.setColor((org.x/2f)+0.5f, (org.y/2f)+0.5f, (org.z/2f)+0.5f, 1);
					normalPix.drawPixel(x, y);
					
					shape.apply(pos.set(org));
					colorPix.setColor(material.getColor(pos, org));
					colorPix.drawPixel(x, y);
				}
			}
			
		}
		
		COLOR_BIND.bind(colorMap);
		colorMap.getCubemapData().consumeCubemapData();
		
		NORMAL_BIND.bind(normalMap);
		normalMap.getCubemapData().consumeCubemapData();
		
		TexBinder.deactive();
	}
	
	public void render(Camera camera) {
		var shader = Assets.PLANET_SHADER;
		
		shader.bind();
		COLOR_BIND.bind(colorMap);
		NORMAL_BIND.bind(normalMap);
		
		shader.setUniformMatrix("u_mat", camera.combined);
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
		gl.glDeleteBuffer(vertBuf);
		colorMap.dispose();
		normalMap.dispose();
		pixmaps.forEach(Pixmap::dispose);
	}
	
	private Cubemap newCubemap(Format format, boolean useMipmap) {
		return new Cubemap(
		new PixmapTextureData(addPixmap(new Pixmap(RES, RES, format)), null, useMipmap, false),
		new PixmapTextureData(addPixmap(new Pixmap(RES, RES, format)), null, useMipmap, false),
		new PixmapTextureData(addPixmap(new Pixmap(RES, RES, format)), null, useMipmap, false),
		new PixmapTextureData(addPixmap(new Pixmap(RES, RES, format)), null, useMipmap, false),
		new PixmapTextureData(addPixmap(new Pixmap(RES, RES, format)), null, useMipmap, false),
		new PixmapTextureData(addPixmap(new Pixmap(RES, RES, format)), null, useMipmap, false));
	}
	
	private Pixmap addPixmap(Pixmap pixmap) {
		pixmaps.add(pixmap);
		return pixmap;
	}
	
	static {
		POSITIONS = new ArrayList<>(100<<LEVEL);
		INDICES = new IcoSphereGen().create(POSITIONS, LEVEL);
		BUFFER = Statics.buffer(INDICES.size() * 3);
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