package com.andedit.planet;

import static com.andedit.planet.Main.main;

import com.andedit.planet.gen.material.PrideGen;
import com.andedit.planet.gen.shape.CustomShapeGen;
import com.andedit.planet.gen.shape.SimpleShapeGen;
import com.andedit.planet.input.control.Control;
import com.andedit.planet.input.control.DesktopControl;
import com.andedit.planet.util.Camera;
import com.andedit.planet.util.Inputs;
import com.andedit.planet.util.Util;
import com.andedit.planet.world.Planet;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class TheGame extends ScreenAdapter {
	
	private boolean disposed;
	private float angle;
	private final Camera camera;
	private final Control control;
	private final Planet planet;
	
	public TheGame() {
		planet = new Planet();
		planet.setShapeGen(new CustomShapeGen());
		planet.setMaterialGen(new PrideGen());
		planet.calulate();
		camera = new Camera();
		control = new DesktopControl();
		
		camera.near = 0.05f;
		camera.far = 10f;
		camera.fieldOfView = 40;
		//camera.fieldOfView = 60;
		camera.viewportWidth = Util.getW();
		camera.viewportHeight = Util.getH();
	}
	
	@Override
	public void show() {
		main.inputs.addProcessor(control.getInput());
		main.setCatched(true);
	}
	
	private boolean calc = false;
	
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
		Vector2 look = control.getLook();
		camera.yaw += look.x;
		camera.pitch += look.y;
		float scl = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) ? 2 : 1;
		scl *= delta * 0.4f;
		Vector2 move = control.getMove().rotateDeg(-camera.yaw).scl(2f);
		camera.translate(move.x * scl, control.getMoveY()  * scl, move.y * scl);
		camera.updateRotation();
		control.clear();
		camera.fieldOfView = 60;
	}
	
	@Override
	public void render(float delta) {
		mov(delta);
		
		if (Inputs.isKeyJustPressed(Keys.F1)) {
			calc = !calc;
		}
		
		if (Inputs.isKeyJustPressed(Keys.F4)) {
			planet.setShapeGen(new CustomShapeGen());
			planet.setMaterialGen(new PrideGen());
			planet.calulate();
		}
		
		Gdx.gl.glLineWidth(1);
		Util.glClear();
		if (calc)
		planet.calulate();
		camera.update(false);
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
