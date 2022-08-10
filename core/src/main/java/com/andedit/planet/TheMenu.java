package com.andedit.planet;

import static com.andedit.planet.Main.main;

import com.badlogic.gdx.ScreenAdapter;

public class TheMenu extends ScreenAdapter {
	@Override
	public void show() {
		main.setScreen(new TheGame());
	}
}
