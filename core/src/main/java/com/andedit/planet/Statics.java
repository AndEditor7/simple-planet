package com.andedit.planet;

import java.nio.ByteBuffer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.IntArray;

public class Statics {
	
	private static ByteBuffer BUFFER;
	private static int VERTEX_SIZE;
	private static IntArray BUFFERS = new IntArray();
	
	static void init() {
		VERTEX_SIZE = Assets.PLANET_CONTEXT.getAttrs().vertexSize;
	}
	
	public static ByteBuffer buffer(int indexSize) {
		if (BUFFER != null) {
			return BUFFER;
		}
		
		return BUFFER = BufferUtils.newByteBuffer(indexSize * Integer.BYTES);
	}
	
	public static ByteBuffer buffer() {
		return BUFFER;
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
	}
}
