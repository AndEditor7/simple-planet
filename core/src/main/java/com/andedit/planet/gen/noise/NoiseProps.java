package com.andedit.planet.gen.noise;

import com.andedit.planet.util.Operator;

public class NoiseProps implements Operator {
	public Operator operator = Operator.ADD;
	
	public NoiseProps() {
		
	}
	
	public NoiseProps(Operator operator) {
		this.operator = operator;
	}
	
	@Override
	public float compute(float a, float b) {
		return operator.compute(a, b);
	}
}
