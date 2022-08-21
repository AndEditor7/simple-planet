package com.andedit.planet.util;

@FunctionalInterface
public interface Operator {
	
	Operator ADD = (a, b) -> a + b;
	Operator SUB = (a, b) -> a - b;
	Operator MUL = (a, b) -> a * b;
	Operator DIV = (a, b) -> a / b;
	
	/**
     * Computes two {@code float} values together.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the value of {@code a} and {@code b}
     */
	float compute(float a, float b);
}
