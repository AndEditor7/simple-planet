package com.andedit.planet.util;

import static com.badlogic.gdx.Gdx.gl;

import java.util.ArrayList;
import java.util.List;

import com.andedit.planet.Statics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ObjectIntMap;

// Source: http://blog.andreaskahler.com/2009/06/creating-icosphere-mesh-in-code.html
public class IcoSphere {

	private int index;
	private ObjectIntMap<Long> middlePointIndexCache;
	private List<Vector3> positions;

	// add vertex to mesh, fix position to be on unit sphere, return index
	private int addVertex(Vector3 pos) {
		positions.add(pos.nor());
		return index++;
	}

	// return index of point in the middle of p1 and p2
	private int getMiddlePoint(int p1, int p2) {
		// first check if we have it already
		boolean firstIsSmaller = p1 < p2;
		long smallerIndex = firstIsSmaller ? p1 : p2;
		long greaterIndex = firstIsSmaller ? p2 : p1;
		long key = (smallerIndex << 32) + greaterIndex;

		int ret = middlePointIndexCache.get(key, Integer.MIN_VALUE);
		if (ret != Integer.MIN_VALUE) {
			return ret;
		}

		// not in cache, calculate it
		Vector3 point1 = positions.get(p1);
		Vector3 point2 = positions.get(p2);
		
		float x = (point1.x + point2.x) / 2.0f;
		float y = (point1.y + point2.y) / 2.0f;
		float z = (point1.z + point2.z) / 2.0f;
		Vector3 middle = new Vector3(x, y, z);

		// add vertex makes sure point is on unit sphere
		int i = addVertex(middle);

		// store it, return index
		middlePointIndexCache.put(key, i);
		return i;
	}

	/** @return triangle indices */
	public List<GridPoint3> create(List<Vector3> positions, int recursionLevel) {
		int size = 100<<recursionLevel;
		this.positions = positions;
		this.middlePointIndexCache = new ObjectIntMap<Long>(size);
		this.index = 0;

		// create 12 vertices of a icosahedron
		float t = (float) ((1.0 + Math.sqrt(5.0)) / 2.0);

		addVertex(new Vector3(-1, t, 0));
		addVertex(new Vector3(1, t, 0));
		addVertex(new Vector3(-1, -t, 0));
		addVertex(new Vector3(1, -t, 0));

		addVertex(new Vector3(0, -1, t));
		addVertex(new Vector3(0, 1, t));
		addVertex(new Vector3(0, -1, -t));
		addVertex(new Vector3(0, 1, -t));

		addVertex(new Vector3(t, 0, -1));
		addVertex(new Vector3(t, 0, 1));
		addVertex(new Vector3(-t, 0, -1));
		addVertex(new Vector3(-t, 0, 1));

		// create 20 triangles of the icosahedron
		var faces = new ArrayList<GridPoint3>(100);

		// 5 faces around point 0
		faces.add(new GridPoint3(0, 11, 5));
		faces.add(new GridPoint3(0, 5, 1));
		faces.add(new GridPoint3(0, 1, 7));
		faces.add(new GridPoint3(0, 7, 10));
		faces.add(new GridPoint3(0, 10, 11));

		// 5 adjacent faces
		faces.add(new GridPoint3(1, 5, 9));
		faces.add(new GridPoint3(5, 11, 4));
		faces.add(new GridPoint3(11, 10, 2));
		faces.add(new GridPoint3(10, 7, 6));
		faces.add(new GridPoint3(7, 1, 8));

		// 5 faces around point 3
		faces.add(new GridPoint3(3, 9, 4));
		faces.add(new GridPoint3(3, 4, 2));
		faces.add(new GridPoint3(3, 2, 6));
		faces.add(new GridPoint3(3, 6, 8));
		faces.add(new GridPoint3(3, 8, 9));

		// 5 adjacent faces
		faces.add(new GridPoint3(4, 9, 5));
		faces.add(new GridPoint3(2, 4, 11));
		faces.add(new GridPoint3(6, 2, 10));
		faces.add(new GridPoint3(8, 6, 7));
		faces.add(new GridPoint3(9, 8, 1));

		// refine triangles
		for (int i = 0; i < recursionLevel; i++) {
			var faces2 = new ArrayList<GridPoint3>(100<<i);
			for (var tri : faces) {
				// replace triangle by 4 triangles
				int a = getMiddlePoint(tri.x, tri.y);
				int b = getMiddlePoint(tri.y, tri.z);
				int c = getMiddlePoint(tri.z, tri.x);

				faces2.add(new GridPoint3(tri.x, a, c));
				faces2.add(new GridPoint3(tri.y, b, a));
				faces2.add(new GridPoint3(tri.z, c, b));
				faces2.add(new GridPoint3(a, b, c));
			}
			faces = faces2;
		}

		return faces;
	}
	
	public static final int LEVEL = 6; // 5 or 6
	public static final int SIZE;
	public static final List<Vector3> POSITIONS;
	public static final List<GridPoint3> INDICES;
	public static final int IDXBUF, IDXSIZE;
	
	static {
		POSITIONS = new ArrayList<>(100<<LEVEL);
		INDICES = new IcoSphere().create(POSITIONS, LEVEL);
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
