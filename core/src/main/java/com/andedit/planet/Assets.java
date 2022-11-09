package com.andedit.planet;

import com.andedit.planet.graphic.vertex.VertContext;
import com.andedit.planet.util.AssetManager;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Assets {
	
	public static ShaderProgram PLANET_SHADER;
	public static VertContext PLANET_CONTEXT;
	
	// Atmosphere
	public static ShaderProgram ATMOPLANET_SHADER;
	public static VertexAttributes ATMOPLANET_VERTEX;
	
	static void load(AssetManager asset) {
		var resolver = asset.getFileHandleResolver();
		asset.setLoader(ShaderProgram.class, new ShaderProgramLoader(resolver));
		
		asset.load("shaders/planet.vert", ShaderProgram.class, t->PLANET_SHADER=t);
		asset.load("shaders/atmoplanet.vert", ShaderProgram.class, t->ATMOPLANET_SHADER=t);
	}
	
	static void get(AssetManager asset) {
		asset.getAll();
		PLANET_CONTEXT = VertContext.of(PLANET_SHADER, 
			new VertexAttribute(Usage.Position, 3, "a_position")
		);
		ATMOPLANET_VERTEX = new VertexAttributes( 
			new VertexAttribute(Usage.Position, 3, "a_position")
		);
	}
}
