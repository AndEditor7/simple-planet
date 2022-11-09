package com.andedit.planet.world;

import static com.badlogic.gdx.Gdx.gl;

import java.util.Iterator;

import com.andedit.planet.Assets;
import com.andedit.planet.Statics;
import com.andedit.planet.graphic.vertex.VertContext;
import com.andedit.planet.util.IcoSphere;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Renderer {
	private static final Vector3 POS1 = new Vector3();
	private static final Vector3 POS2 = new Vector3();
	
	private final Array<SpaceObj> array;
	private final Array<SpaceObj> order;
	
	public Renderer(Array<SpaceObj> array) {
		this.array = array;
		array.ordered = false;
		order = new Array<>();
	}
	
	public void add(SpaceObj obj) {
		array.add(obj);
	}
	
	public void remove(SpaceObj obj) {
		array.removeValue(obj, true);
	}
	
	public void clear() {
		array.clear();
	}
	
	public Iterator<SpaceObj> iterator() {
		return array.iterator();
	}
	
	public void render(Camera camera) {
		if (array.isEmpty()) return;
		
		for (var obj : array) {
			obj.render(camera);
		}
		
		var pos = camera.position;
		order.clear();
		order.addAll(array);
		order.sort((a, b) -> {
			var posA = a.getTrans().getTranslation(POS1);
			var posB = b.getTrans().getTranslation(POS2);
			return MathUtils.round(Math.signum(pos.dst(posB) - pos.dst(posA)));
		});
		
		gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnable(GL20.GL_BLEND);
		gl.glCullFace(GL20.GL_FRONT);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, Statics.SPHERE_BUF);
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, IcoSphere.IDXBUF);
		
		ShaderProgram shader = null;
		for (var obj : order) {
			if (!obj.canRender()) return;
			var atmo = obj.getAtmo();
			if (atmo == null) continue;
			
			if (shader != atmo.getShader()) {
				if (shader != null) {
					VertContext.unVertAttrs(shader, Assets.ATMOPLANET_VERTEX);
				}
				shader = atmo.getShader();
				shader.bind();
				atmo.initShader(camera);
				VertContext.setVertAttrs(shader, Assets.ATMOPLANET_VERTEX);
			}
			
			atmo.render(obj);
		}
		
		if (shader != null) {
			VertContext.unVertAttrs(shader, Assets.ATMOPLANET_VERTEX);
		}
		
		gl.glBindBuffer(GL20.GL_ELEMENT_ARRAY_BUFFER, 0);
		gl.glBindBuffer(GL20.GL_ARRAY_BUFFER, 0);
		gl.glCullFace(GL20.GL_BACK);
		gl.glDisable(GL20.GL_BLEND);
	}
}
