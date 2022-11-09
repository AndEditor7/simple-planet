package com.andedit.planet;

import com.andedit.planet.util.API;
import com.andedit.planet.util.AssetManager;
import com.andedit.planet.util.Inputs;
import com.andedit.planet.util.Noise;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Collections;

public class Main extends Base {
	public static API api;
	public static final Main main = new Main();
	
	private AssetManager asset;

	@Override
	public void create() {
		Noise.instance.getSimplex(0, 0, 0);
		Collections.allocateIterators = true;
		stage = new Stage();
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, inputs, Inputs.input));
		
		asset = new AssetManager();
		setScreen(new Loading(asset));
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glCullFace(GL20.GL_BACK);
		Gdx.gl.glEnable(GL20.GL_CULL_FACE);
		//Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
		ShaderProgram.pedantic = false;
		//api.glPolygonMode(GL20.GL_FRONT_AND_BACK, false);
	}

	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
		asset.dispose();
		Statics.dispose();
	}
}