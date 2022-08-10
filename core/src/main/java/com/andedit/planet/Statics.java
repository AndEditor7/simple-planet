package com.andedit.planet;

import java.nio.ByteBuffer;

import com.badlogic.gdx.utils.BufferUtils;

public class Statics {
	
	private static ByteBuffer BUFFER;
	private static int VERTEX_SIZE;
	
	static void init() {
		VERTEX_SIZE = Assets.PLANET_CONTEXT.getAttrs().vertexSize;
	}
	
	public static ByteBuffer buffer(int vertSize) {
		if (BUFFER != null) {
			return BUFFER;
		}
		
		return BUFFER = BufferUtils.newByteBuffer(VERTEX_SIZE * vertSize);
	}
	
	public static ByteBuffer buffer() {
		return BUFFER;
	}

	static void dispose() {
		
	}
}
