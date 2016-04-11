package com.luigisgro.cqf.function;

/**
 * Derivable scalar function interface
 * @author Luigi Sgro
 *
 */
public interface DerivableFunction extends Function {
	/**
	 * @return A new function object that corresponds to the derivative of the current function
	 */
	Function derivate();
}
