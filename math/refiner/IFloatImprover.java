/*
 * Created on 03.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package math.refiner;

/**
 * Defines a stateful improvement step for minimization or root finding that works from
 * function values alone, without needing a {@link function.IFloatFunction} object.
 *
 * Design Decisions / Implementation Details:
 * @see IFloatRefiner requires a Function Object,
 * but could directly use an IFloatImprover!
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:54:34Z
 * digest: 7eb5913917e79f1ab25f12ee5ea7b1907ec0076adfe984d2d792817bd42cc684
 * stale: false
 * tags: [code/fixed_point_iteration]
 * concepts: [Iterative Improver Interface]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IFloatImprover {
	
	/** return a new Value to evaluate the Function at
	 * @param _yValue the Function Value for the last Return Value of this Method
	 * @return a new Value to evaluate the Function at 
	 */
	public double improve(final double _yValue);
	
	/** return true, when the desired Accuracy has been reached, 
	 * since most Algorithms also evaluate this Criterion
	 * @param fnVal the Function Value for the last Return Value of this Method
	 * @return true, when the desired Accuracy has been reached 
	 */
	public boolean finished(final double fnVal); 
}
