package com.andedit.planet.util;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.graphics.Cubemap.CubemapSide;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FacedCubemapData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.BufferUtils;

public final class Util {
	public static final ByteBuffer BUFFER = BufferUtils.newByteBuffer(1048576);

	public static float lerp(float fromValue, float toValue, float progress, float clamp) {
		final float delta = (toValue - fromValue) * progress;
		return fromValue + MathUtils.clamp(delta, -clamp, clamp);
	}

	public static boolean isGL30() {
		return Gdx.gl30 != null;
	}
	
	public static boolean isMobile() {
		return Gdx.app.getType() != ApplicationType.Desktop;
	}
	
	public static boolean isDesktop() {
		return Gdx.app.getType() == ApplicationType.Desktop;
	}
	
	public static int getMb() {
		long java = Gdx.app.getJavaHeap();
		if (isDesktop()) {
			return (int)(java / 1024L / 1024L);
		}
		return (int)((java + Gdx.app.getNativeHeap()) / 1024L / 1024L);
	}

	/** Returns the width of the client area in logical pixels. */
	public static int getW() {
		return Gdx.graphics.getWidth();
	}

	/** Returns the height of the client area in logical pixels. */
	public static int getH() {
		return Gdx.graphics.getHeight();
	}
	
	public static boolean isBlank(String string) {
		final int len = string.length();
		if (len == 0) {
			return true;
		}
		for (int i = 0; i < len; i++) {
			if (!Character.isWhitespace(string.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static float modAngle(float angle) {
		float mod = angle % 360f;
		return MathUtils.clamp(mod < 0f ? mod + 360f : mod, 0f, 360f);
	}
	
	/** Linearly interpolates between fromValue to toValue on progress position. */
	public static double lerp (double fromValue, double toValue, double progress) {
		return fromValue + (toValue - fromValue) * progress;
	}
	
	public static float packNorm(float x, float y, float z) {
		final float a = 0.5f;
		return Color.toFloatBits((x*a)+a, (y*a)+a, (z*a)+a, 1f);
	}

	public static void glClear() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	}
	
	public static ShaderProgram newShader(String path) {
		return new ShaderProgram(Gdx.files.internal(path + ".vert"), Gdx.files.internal(path + ".frag"));
	}
	
	public static float getShade(Color color, float scl) {
		return Color.toFloatBits(color.r * scl, color.g * scl, color.b * scl, 1);
	}
	
	public static void scale(Actor actor, float scl) {
		actor.setSize(actor.getWidth()*scl, actor.getHeight()*scl);
	}
	
	public static TextureRegion flip(TextureRegion region, boolean x, boolean y) {
		TextureRegion var = new TextureRegion(region);
		var.flip(x, y);
		return var;
	}
	
	public static Pixmap getPixmap(Cubemap map, CubemapSide side) {
		return getPixmap(getTexData(map, side));
	}
	
	public static Pixmap getPixmap(TextureData data) {
		if (!data.isPrepared()) data.prepare();
		return data.consumePixmap();
	}
	
	public static TextureData getTexData(Cubemap map, CubemapSide side) {
		return ((FacedCubemapData)map.getCubemapData()).getTextureData(side);
	}

	/** Create a new change listener using java 8 lambda. */
	public static EventListener newListener(Consumer<Event> listener) {
		return new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				listener.accept(event);
			}
		};
	}

	/** Create a new change listener without Event. */
	public static EventListener newListener(Runnable runnable) {
		return new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				runnable.run();
			}
		};
	}
}
