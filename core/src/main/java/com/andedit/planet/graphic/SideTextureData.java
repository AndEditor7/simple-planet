package com.andedit.planet.graphic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;

public class SideTextureData extends PixmapTextureData {
	
	private final Object lock;

	public SideTextureData(Pixmap pixmap, Object lock) {
		super(pixmap, null, false, false);
		this.lock = lock;
	}
	
	@Override
	public TextureDataType getType () {
		return TextureDataType.Custom;
	}
	
	@Override
	public void consumeCustomData(int target) {
		synchronized (lock) {
			Pixmap pixmap = consumePixmap();
			Gdx.gl.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, 1);
			Gdx.gl.glTexImage2D(target, 0, pixmap.getGLInternalFormat(), pixmap.getWidth(),
				pixmap.getHeight(), 0, pixmap.getGLFormat(), pixmap.getGLType(), pixmap.getPixels());
		}
	}
}
