package com.andedit.planet;

import static com.andedit.planet.Main.main;

import com.andedit.planet.gen.MarsGen;
import com.andedit.planet.gen.MercuryGen;
import com.andedit.planet.input.control.Control;
import com.andedit.planet.input.control.DesktopControl;
import com.andedit.planet.trans.RotationTrans;
import com.andedit.planet.util.Camera;
import com.andedit.planet.util.Inputs;
import com.andedit.planet.util.Util;
import com.andedit.planet.world.Planet;
import com.andedit.planet.world.Renderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class TheGame extends ScreenAdapter {
	
	private boolean disposed;
	private float angle;
	private final Camera camera;
	private final Control control;
	private final Planet planet;
	private final Renderer render;
	
	private boolean firstStart = true;
	
	public TheGame() {
		planet = new Planet();
		planet.setTrans(new RotationTrans());
		refreah();
		camera = new Camera();
		control = new DesktopControl();
		render = new Renderer(new Array<>());
		render.add(planet);
		
		camera.near = 0.1f;
		camera.far = 50f;
		camera.fieldOfView = 40;
		camera.viewportWidth = Util.getW();
		camera.viewportHeight = Util.getH();
		camera.position.set(0, 0, 3.5f);
		camera.direction.set(1,0,0);
		camera.up.set(0,1,0);
		camera.yaw = 180;
	}
	
	@Override
	public void show() {
		main.inputs.addProcessor(control.getInput());
		main.setCatched(true);
	}
	
	private void rot(float delta) {
		angle += delta * 15f;
		if (angle > 360f) {
			angle -= 360f;
		}
		angle = 0;
		camera.position.set(MathUtils.sinDeg(angle)*3.8f, 0, MathUtils.cosDeg(angle)*3.8f);
		camera.up.set(0, 1, 0);
		camera.lookAt(0, 0, 0);
		camera.fieldOfView = 40;
	}
	
	private void mov(float delta) {
		camera.fieldOfView = 60;
		Vector2 look = control.getLook();
		camera.yaw += look.x;
		camera.pitch += look.y;
		float scl = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? 2 : 1;
		scl *= delta * 0.4f;
		Vector2 move = control.getMove().rotateDeg(-camera.yaw).scl(2f);
		camera.translate(move.x * scl, control.getMoveY()  * scl, move.y * scl);
		camera.updateRotation();
		control.clear();
	}
	
	private void refreah() {
		var gen = new MarsGen();
		planet.setShapeGen(gen);
		planet.setMaterialGen(gen);
		planet.setAtmosphere(gen);
		//planet.setMaterialGen(new PrideMaterialGen());
		planet.calulate(() -> {
			if (firstStart) {
				firstStart = false;
				camera.yaw = 180;
				camera.pitch = 0;
				control.clear();
			}
		});
	}
	
	@Override
	public void render(float delta) {
		mov(delta);
		
		if (Inputs.isKeyJustPressed(Keys.F4) || Inputs.isKeyJustPressed(Keys.ENTER)) {
			refreah();
		}
		if (Inputs.isKeyJustPressed(Keys.F5)) {
			//planet.amb = 0.2f;
		}
		
		Util.glClear();
		camera.update(false);
		planet.update(delta);
		render.render(camera);
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
