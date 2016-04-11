package com.luigisgro.cqf.function;

/**
 * Definition of a class of integrable functions
 * @author Luigi Sgro
 *
 */
public interface IntegrableFunction extends Function {
	/**
	 * @return A new function object that corresponds to the anti-derivative of the current function
	 */
	Function integrate();
}
