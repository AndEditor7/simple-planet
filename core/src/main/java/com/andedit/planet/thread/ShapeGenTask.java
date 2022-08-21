package com.andedit.planet.thread;

import java.nio.FloatBuffer;

import com.andedit.planet.gen.shape.ShapeGen;
import com.andedit.planet.world.Planet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Null;

public class ShapeGenTask implements Runnable {
	
	private final FloatBuffer buffer;
	private final ShapeGen shape;
	private final Runnable postRun;
	
	public ShapeGenTask(FloatBuffer buffer, ShapeGen shape, @Null Runnable postRun) {
		this.buffer = buffer;
		this.shape = shape;
		this.postRun = postRun;
	}

	@Override
	public void run() {
		var pos = new Vector3();
		synchronized (buffer) {
			buffer.clear();
			for (int i = 0; i < Planet.SIZE; i++) {
				shape.apply(pos.set(Planet.POSITIONS.get(i)));
				//pos.set(Planet.POSITIONS.get(i));
				buffer.put(pos.x);
				buffer.put(pos.y);
				buffer.put(pos.z);
			}
			buffer.flip();
		}
		
		if (postRun != null) {
			Gdx.app.postRunnable(postRun);
		}
	}
}
