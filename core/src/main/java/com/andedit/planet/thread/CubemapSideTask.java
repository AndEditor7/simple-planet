package com.andedit.planet.thread;

import com.andedit.planet.gen.Material;
import com.andedit.planet.interfaces.MaterialGen;
import com.andedit.planet.interfaces.ShapeGen;
import com.andedit.planet.world.Planet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap.CubemapSide;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Null;
import com.badlogic.gdx.graphics.Pixmap;

public class CubemapSideTask implements Runnable {
	private static final float MASK = Planet.RES - 1;
	
	private final ShapeGen shape;
	private final MaterialGen material;
	private final Pixmap colourMap, normalMap;
	private final CubemapSide side;
	private final Vector3 rite;
	private final Object lock;
	private final Runnable postRun;
	
	public CubemapSideTask(ShapeGen shape, MaterialGen material, Pixmap colourMap, Pixmap normalMap, CubemapSide side, Object lock, @Null Runnable postRun) {
		this.shape = shape;
		this.material = material;
		this.colourMap = colourMap;
		this.normalMap = normalMap;
		this.side = side;
		this.lock = lock;
		this.postRun = postRun;
		
		var vec = new Vector3(side.direction).crs(side.up);
		vec.x = MathUtils.round(vec.x);
		vec.y = MathUtils.round(vec.y);
		vec.z = MathUtils.round(vec.z);
		rite = new Vector3(vec);
	}

	//     3
	//     |
	// 4---+---2
	//     |
	//     1
	@Override
	public void run() {
		var pos  = new Vector3();
		var org  = new Vector3();
		var uPos = new Vector3();
		var vPos = new Vector3();
		
		var pos1 = new Vector3();
		var pos2 = new Vector3();
		var pos3 = new Vector3();
		var pos4 = new Vector3();
		
		var nor1 = new Vector3();
		var nor2 = new Vector3();
		
		var dir = side.direction;
		var color = new Color();
		var matrial = new Material();
		
		var map = new float[Planet.RES+2][Planet.RES+2];
		
		synchronized (lock) {
			for (int x = -1; x < Planet.RES+1; x++)
			for (int y = -1; y < Planet.RES+1; y++) {
				calcPosU(uPos, x);
				calcPosV(vPos, y);
				pos.set(uPos).add(vPos).add(dir).nor();
				map[x+1][y+1] = shape.genShape(pos);
			}
			
			for (int x = 0; x < Planet.RES; x++)
			for (int y = 0; y < Planet.RES; y++) {
				calcPosU(uPos, x);
				calcPosV(vPos, y);
				org.set(uPos).add(vPos).add(dir).nor();
				pos.set(org).scl(map[x+1][y+1]);
				material.genMaterial(pos, org, color, matrial);
				colourMap.setColor(color.r, color.g, color.b, matrial.specular);
				colourMap.drawPixel(x, y);
				
				calcPosU(uPos, x);
				calcPosV(vPos, y-1);
				pos1.set(uPos).add(vPos).add(dir).nor().scl(map[x+1][y]);
				
				calcPosU(uPos, x+1);
				calcPosV(vPos, y);
				pos2.set(uPos).add(vPos).add(dir).nor().scl(map[x+2][y+1]);
				
				calcPosU(uPos, x);
				calcPosV(vPos, y+1);
				pos3.set(uPos).add(vPos).add(dir).nor().scl(map[x+1][y+2]);
				
				calcPosU(uPos, x-1);
				calcPosV(vPos, y);
				pos4.set(uPos).add(vPos).add(dir).nor().scl(map[x][y+1]);
				
				// Normalize the "triangle" with three vertices (v1, v2, v3)
				nor1.set(pos2.x - pos3.x, pos2.y - pos3.y, pos2.z - pos3.z);
				nor1.crs(pos1.x - pos3.x, pos1.y - pos3.y, pos1.z - pos3.z);
				
				// Normalize the second part of the "triangle" with three vertices (v2, v4, v1)
				nor2.set(pos4.x - pos1.x, pos4.y - pos1.y, pos4.z - pos1.z);
				nor2.crs(pos3.x - pos1.x, pos3.y - pos1.y, pos3.z - pos1.z);
				
				// Add both normals together then normalize the vector.
				pos.set(nor1).add(nor2).nor();
				
				normalMap.setColor((pos.x*0.5f)+0.5f, (pos.y*0.5f)+0.5f, (pos.z*0.5f)+0.5f, matrial.shininess);
				normalMap.drawPixel(x, y);
			}
		}
		
		// Do the post-run in the main thread if available.
		// Usually used for uploading/updating the texture to the GPU.
		if (postRun != null) {
			Gdx.app.postRunnable(postRun);
		}
	}
	
	private void calcPosU(Vector3 uPos, int x) {
		var vec = rite; uPos.set(vec).scl(x/MASK).sub(vec.x*0.5f, vec.y*0.5f, vec.z*0.5f).scl(2);
	}
	
	private void calcPosV(Vector3 vPos, int y) {
		var vec = side.up; vPos.set(vec).scl(y/MASK).sub(vec.x*0.5f, vec.y*0.5f, vec.z*0.5f).scl(2);
	}
}
