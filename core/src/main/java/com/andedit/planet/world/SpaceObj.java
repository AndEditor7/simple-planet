package com.andedit.planet.world;

import com.andedit.planet.interfaces.Atmosphere;
import com.andedit.planet.trans.Trans;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Null;

public interface SpaceObj {
	Matrix4 MAT = new Matrix4();
	
	void setTrans(@Null Trans trans);
	
	void update(float delta);
	
	void render(Camera camera);
	
	boolean canRender();
	
	@Null
	Atmosphere getAtmo();
	
	Matrix4 getTrans();
	
	default Vector3 getLightDir() {
		return new Vector3();
	}
	
	default Matrix4 getAtmoMat() {
		var trans = getTrans();
		var val = trans.val;
		return MAT.setToTranslationAndScaling(
			val[Matrix4.M03], val[Matrix4.M13], val[Matrix4.M23],
			trans.getScaleX(), trans.getScaleY(), trans.getScaleZ()
		);
	}
}
