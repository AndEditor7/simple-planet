package com.andedit.planet;

import static com.andedit.planet.Main.main;

import com.andedit.planet.util.AssetManager;
import com.andedit.planet.util.Inputs;
import com.badlogic.gdx.ScreenAdapter;

public class Loading extends ScreenAdapter {
	
private final AssetManager asset;
	
	public Loading(AssetManager asset) {
		this.asset = asset;
		Assets.load(asset);
	}
	
	@Override
	public void render(float delta) {
		if (asset.update(10)) {
			Assets.get(asset);
			Inputs.clear();
			Statics.init();
			main.setScreen(new TheMenu());
		}
	}
}
