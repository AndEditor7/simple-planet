package com.andedit.planet.input.control;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Null;

/** Game's controller interface. */
public interface Control  {
	
	float getMoveY();
	
	Vector2 getMove();
	
	Vector2 getLook();
	
	@Null
	default InputProcessor getInput() {
		return null;
	};
	
	boolean isUse();
	
	/** Resets everything. */
	void reset();
	
	/** Resets just pressed input. */
	void clear();
}
