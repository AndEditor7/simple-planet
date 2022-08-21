package com.andedit.planet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.andedit.planet.thread.DaemonThreadFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;

public class Statics {
	
	private static IntArray BUFFERS = new IntArray();
	
	public static ExecutorService PLANET_EXECUTOR;
	
	static void init() {
		int threads = Runtime.getRuntime().availableProcessors();
		threads = MathUtils.clamp(MathUtils.roundPositive(threads / 1.5f), 1, 7);
		PLANET_EXECUTOR = Executors.newFixedThreadPool(threads, new DaemonThreadFactory("PlanetGen"));
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
			Gdx.gl.glDeleteBuffers(buffer.remaining(), buffer);
			BUFFERS.clear();
		}
		
		PLANET_EXECUTOR.shutdown();
		try {
			PLANET_EXECUTOR.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
		}
	}
}
