package com.andedit.planet.gen.noise;

import com.andedit.planet.util.Operator;
import com.andedit.planet.util.Pair;
import com.badlogic.gdx.utils.Array;

/** A multi noise evaluation, Uses NoiseFilter interface. */
public class MultiNoise implements NoiseFilter {
	/** An array list of NoiseFilter and Operator pair. */
	private final Array<Pair<NoiseFilter, Operator>> noises;
	/** The noise amplitude. */
	public float amb = 1;
	
	public MultiNoise() {
		noises = new Array<>(32);
	}
	
	/** Removes the item at the specified index.  */
	public void remove(int idx) {
		noises.removeIndex(idx);
	}
	
	/** Removes the instance of the specified value in the array. */
	public void remove(Pair<NoiseFilter, Operator> pair) {
		noises.removeValue(pair, true);
	}
	
	public Pair<NoiseFilter, Operator> add(NoiseFilter filter, Operator operator) {
		var pair = new Pair<>(filter, operator);
		noises.add(pair);
		return pair;
	}
	
	public Pair<NoiseFilter, Operator> set(int idx, NoiseFilter filter, Operator operator) {
		var pair = new Pair<>(filter, operator);
		noises.set(idx, pair);
		return pair;
	}
	
	/** Swaps the elements at the specified positions. */
	public void swap(int first, int second) {
		noises.swap(first, second);
	}
	
	
	/** Clear and reset the MultiNoise. */
	public void reset() {
		noises.clear();
		amb = 1;
	}

	@Override
	public float evaluate(float x, float y, float z) {
		float value = 0;
		for (int i = 0; i < noises.size; i++) {
			var pair = noises.get(i);
			var filter = pair.left;
			var operator = pair.right;
			value = operator.compute(value, filter.evaluate(x, y, z));
		}
		return value * amb;
	}
}
