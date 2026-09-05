/*
 * File Name: QuaternaryOp.java
 * Created on: 10.01.2004
 *
 */
package math.vector;

/**
 * Defines a binary-to-quaternary arithmetic operation (add, subtract, multiply, divide, or a
 * linear combination) applied to up to four {@code double} operands.
 *
 * <p>Title: QuaternaryOp<p>
 * Description:
 * Interface for testing any Type of Operation
 *
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T12:43:24Z
 * digest: e17bd9d2447f1d6494ac58ef4dda8de77fcf827144462a1dbf3be46dc8bb2206
 * stale: false
 * tags: [code/strategy_pattern, code/functional_interfaces]
 * concepts: [Quaternary Arithmetic Operation]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
interface QuaternaryOp {

	/** The Implementation of QuaternaryOp that adds the first two Operands 	 */
	final static public QuaternaryOp AddOp = new QuaternaryOp() {
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(final double a0, final double a1, final double a2, final double a3) {
			return a0+a1; }

		public int numArgs() { return 2; } 
	};

	/** The Implementation of QuaternaryOp that subtracts the first two Operands 	 */
	final static public QuaternaryOp SubOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(final double a0, final double a1, final double a2, final double a3) {
			return a0-a1; }

		public int numArgs() { return 2; } 
	};

	/** The Implementation of QuaternaryOp that multiplies the first two Operands 	 */
	final static public QuaternaryOp MulOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(final double a0, final double a1, final double a2, final double a3) {
			return a0*a1; }

		public int numArgs() { return 2; } 
	};

	/** The Implementation of QuaternaryOp that multiplies the first two Operands 	 */
	final static public QuaternaryOp MulAtOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(final double a0, final double a1, final double a2, final double a3) {
			return a0*a1; }

		public int numArgs() { return 0; } 
	};

	/** The Implementation of QuaternaryOp that divides the first two Operands 	 */
	final static public QuaternaryOp DivOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(final double a0, final double a1, final double a2, final double a3) {
			if (a0 == 0) { //don't consider NsN = 0/0
				return 0; }
			if (a1 == 0) { //don't check the Sign, projective Model
				return Double.POSITIVE_INFINITY; }
			return a0/a1; }

		public int numArgs() { return 2; } 
	};

	/** The Implementation of QuaternaryOp 
	 * that linearly combines the first three Operands: a0*=a1 + a2
	 */
	final static public QuaternaryOp LinOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(double a0, double a1, double a2, double a3) {
			return a0*a1 + a2; }

		public int numArgs() { return 3; } 
	};

	/** The Implementation of QuaternaryOp 
	 * that linearly combines the first three Operands: a0*=a1 + a2
	 */
	final static public QuaternaryOp LinAtOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(double a0, double a1, double a2, double a3) {
			return a0*a1 + a2; }

		public int numArgs() { return 0; } 
	};

	/** The Implementation of QuaternaryOp 
	 * that combines the first three Operands: x+=a * y 
	 */
	final static public QuaternaryOp AddProdOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(double a0, double a1, double a2, double a3) {
			return a0 + a1*a2; }

		public int numArgs() { return 3; } 
	};

	/** The Implementation of QuaternaryOp 
	 * that combines the first three Operands: x+=a * y 
	 */
	final static public QuaternaryOp BiLinOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(double a0, double a1, double a2, double a3) {
			return a0*a1+a2*a3; }

		public int numArgs() { return 4; } 
	};

	/** The Implementation of QuaternaryOp 
	 * that combines the first three Operands: x+=a * y 
	 */
	final static public QuaternaryOp BiLinAtOp = new QuaternaryOp() { 
		/** @see math.QuaternaryOp#op(double, double, double, double)		 */
		public double op(double a0, double a1, double a2, double a3) {
			return a0*a1+a2*a3; }

		public int numArgs() { return 0; } 
	};

	/**
	 * Returns how many of the four operands this implementation actually uses.
	 */
	public int numArgs();

	/** any Operation with up to 4 arguments 	*/
	public double op(double a0, double a1, double a2, double a3);

}
