/*
 * Created on 03.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package math.refiner;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Interface for a Method on a stateful Object 
 * that tries to improve a Solution for Minimization or Root Finding, 
 * given only the Function Values. 
 *
 * Design Decisions / Implementation Details:
 * @see math.refiner.IFloatRefiner requires a Function Object, 
 * but could directly use an IFloatImprover! 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
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
