package com.andedit.planet;

import com.andedit.planet.util.AssetManager;
import com.andedit.planet.util.Inputs;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Collections;

public class Main extends Base {
	public static final Main main = new Main();
	
	private AssetManager asset;

	@Override
	public void create() {
		Collections.allocateIterators = true;
		stage = new Stage();
		Gdx.input.setInputProcessor(new InputMultiplexer(stage, inputs, Inputs.input));
		
		asset = new AssetManager();
		setScreen(new Loading(asset));
		
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
	}

	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
		asset.dispose();
		Statics.dispose();
	}
}