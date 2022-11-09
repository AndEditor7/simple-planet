package com.andedit.planet;

import static com.badlogic.gdx.Gdx.gl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.andedit.planet.thread.DaemonThreadFactory;
import com.andedit.planet.util.IcoSphere;
import com.andedit.planet.world.Planet;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;

public class Statics {
	
	private static IntArray BUFFERS = new IntArray();
	
	public static ExecutorService PLANET_EXECUTOR;
	public static int SPHERE_BUF;
	
	static void init() {
		int threads = Runtime.getRuntime().availableProcessors();
		threads = MathUtils.clamp(MathUtils.roundPositive(threads / 1.5f), 1, 6);
		PLANET_EXECUTOR = Executors.newFixedThreadPool(threads, new DaemonThreadFactory("PlanetGen"));
		
		SPHERE_BUF = gl.glGenBuffer();
		var buffer = BufferUtils.newFloatBuffer(IcoSphere.SIZE * 3);
		buffer.clear();
		for (var pos : IcoSphere.POSITIONS) {
			buffer.put(pos.x);
			buffer.put(pos.y);
			buffer.put(pos.z);
		}
		buffer.flip();
		
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, SPHERE_BUF);
		gl.glBufferData(GL20.GL_ARRAY_BUFFER, buffer.remaining() * Float.BYTES, buffer, GL20.GL_STATIC_DRAW);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		putBuffer(SPHERE_BUF);
	}
	
	public static void putBuffer(int buffer) {
		if (!BUFFERS.contains(buffer)) {
			BUFFERS.add(buffer);
		}
	}

	static void dispose() {
		if (BUFFERS.notEmpty()) {
			var buffer = BufferUtils.newIntBuffer(BUFFERS.size);
			buffer.put(BUFFERS.items, 0, BUFFERS.size);
			buffer.flip();
			gl.glDeleteBuffers(buffer.remaining(), buffer);
			BUFFERS.clear();
		}
		
		PLANET_EXECUTOR.shutdown();
		try {
			PLANET_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
	}
}
