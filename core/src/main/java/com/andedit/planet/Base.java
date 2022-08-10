package com.andedit.planet;

import com.andedit.planet.util.Inputs;
import com.andedit.planet.util.Util;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ObjectSet;

abstract class Base extends Game {

	public Stage stage;
	public final InputMultiplexer inputs;
	
	protected Screen newScreen;
	protected final ObjectSet<String> inputLocks;
	
	private boolean isCatched;
	
	{
		inputs = new InputMultiplexer();
		inputLocks = new ObjectSet<>();
	}

	@Override
	public void render() {
		nextScreen();
		super.render();
		Gdx.gl.glUseProgram(0);
		
		stage.act();
		stage.draw();
		Gdx.gl.glUseProgram(0);
		Inputs.reset();
	}

	protected void nextScreen() {
		if (newScreen == null)
			return;

		if (screen != null)
			screen.hide();
		
		screen = newScreen;
		newScreen = null;

		stage.clear(); // Always clear UI when switching screen.
		inputs.clear(); // Always clear the input processors.
		setCatched(false);
		inputLocks.clear();
		
		screen.show();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		super.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen) {
		newScreen = screen;
	}
	
	public boolean isCatched() {
		return isCatched;
	}
	
	public void setCatched(boolean isCatched) {
		Gdx.input.setCursorCatched(isCatched);
		this.isCatched = isCatched;
	}
	
	public void setCursorPos(boolean centor) {
		if (centor) {
			Gdx.input.setCursorPosition(Util.getW()>>1, Util.getH()>>1);
		} else {
			Gdx.input.setCursorPosition(0, 0);
		}
	}
	
	public void addInputLock(String key) {
		inputLocks.add(key);
	}
	
	public void removeInputLock(String key) {
		inputLocks.remove(key);
	}
	
	public boolean isInputLock() {
		return inputLocks.notEmpty();
	}

	@Override
	public void dispose() {
		if (screen != null) {
			screen.dispose();
			screen = null;
		}
			
		if (newScreen != null) {
			newScreen.dispose();
			newScreen = null;
		}
	}
}
