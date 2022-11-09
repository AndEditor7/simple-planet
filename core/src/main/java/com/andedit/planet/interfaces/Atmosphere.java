package com.andedit.planet.interfaces;

import com.andedit.planet.world.SpaceObj;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface Atmosphere {
	
	ShaderProgram getShader();
	
	/** The shader is already bind. */
	default void initShader(Camera camera) {
		var shader = getShader();
		shader.setUniformMatrix("u_projTrans", camera.combined);
		shader.setUniformf("u_position", camera.position);
	}
	
	/** Shader, vertex and index are already bind. */
	void render(SpaceObj obj);
}
