package com.andedit.planet;

import static com.andedit.planet.Main.main;

import com.andedit.planet.util.Util;
import com.andedit.planet.world.Planet;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TheGame extends ScreenAdapter {
	
	private boolean disposed;
	private final Vector2 rotation = new Vector2();
	private float angle;
	private final PerspectiveCamera camera;
	private final CameraInputController con;
	private final Planet planet;
	
	public TheGame() {
		planet = new Planet();
		planet.calulate();
		camera = new PerspectiveCamera();
		con = new CameraInputController(camera);
		con.autoUpdate = false;
		
		camera.near = 0.5f;
		camera.fieldOfView = 42;
		camera.viewportWidth = Util.getW();
		camera.viewportHeight = Util.getH();
	}
	
	@Override
	public void show() {
		main.inputs.addProcessor(con);
	}
	
	@Override
	public void render(float delta) {
		angle += delta * 20f;
		if (angle > 360f) {
			angle -= 360f;
		}
		//angle = 0;
		camera.position.set(MathUtils.sinDeg(angle)*4f, 0, MathUtils.cosDeg(angle)*4f);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.update(false);
		
		Util.glClear();
		planet.calulate();
		planet.render(camera);
	}
	
	@Override
	public void hide() {
		dispose();
	}
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
	}
	
	@Override
	public void dispose() {
		if (disposed) return;
		disposed = true;
		planet.dispose();
	}
}
