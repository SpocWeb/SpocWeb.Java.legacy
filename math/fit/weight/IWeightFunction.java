/*
 * File Name: IWeightFunction.java
 * Created on: 22.02.2004
 *
 */
package math.fit.weight;

/**
 * Title: IWeightFunction<p>
 * Description:
 * Defines the Interface for a Weighting Function 
 * for a Distribution of random or measured Values. 
 * 
 * The Weighting Function is defined by the shape of the Distribution
 * and relative weight should first increase with the Deviation d, 
 * but as soon as |d| becomes larger than 1, it should decrease, 
 * so Outliers don't get too much Weight in the Estimation. 
 *
 * Design Decisions / Implementation Details:
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
 */
public interface IWeightFunction {

	/** 
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the Probability(-density) for this Deviation 
	 */
	double prob(double d); 

	/** 
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the integrated Probability for this Deviation 
	 */
	double probCum(double d); 

	/** 
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the Weight for this Deviation 
	 */
	double weight(double d); 

	/** 
	 * @param d the normed Deviation: (y-yAvg)/stdDev
	 * @return the integrated Weight for this Deviation 
	 */
	double weightCum(double d); 

}
