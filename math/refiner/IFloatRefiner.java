/*
 * File Name: IFloatRefiner.java
 * Created on: 29.01.2004
 *
 */
package math.refiner;

/**
 * Title: IFloatRefiner<p>
 * Description:
 * Interface for performing a single Step 
 * to find a special Point (Zero, Fixpoint or Maximum) in Function f 
 * 
 * Known SubClasses: <none>
 * 
 * Known Uses: <none>
 *
 * similar Classes: 
 * @see streamIO.copy.group.ring.IRefiner
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public interface IFloatRefiner {

	/**Performs one Step to refine the previous Result
	 * @return the new Estimate for the Ordinate of the desired Solution. 
	 * Differencing to the last Estimate gives the Step taken. 
	 */
	double refine();

}
