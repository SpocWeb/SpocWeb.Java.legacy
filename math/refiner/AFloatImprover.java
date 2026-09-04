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
 *
 * Purpose / Responsibilities of this Class
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
public class AFloatImprover 
//implements IFloatImprover
{
	
	////////////////////////////////////////////////////////////////////////////
	
	/**Contains the last Step Size	 */
	public double dx;
	
	/**The last / left x Value 	 */
	public double xl;
	
	/**The last / left y Value	 */
	public double yl;
	
	/**Initializing the Iteration
	 * by giving the Function and a Starting Point.	 */
	public void init(final double _x, final double _y) { 
		xl = _x; 
		yl = _y; 
	}
	
	/**The (a priori) Multiplicity of the Zero.
	 * Set by NewtonRefiner2, required for Correction by Regula Falsi. 
	 * Also acts as an (Over-)Relaxation Factor to speed up Searches.  
	 */
	//public double multiplicity = 1;
	
	////////////////////////////////////////////////////////////////////////////
	
	/**Empty Constructor.	 */
	public AFloatImprover()	{}

	/**Initializing Constructor for Iteration
	 * by giving the Function and a Starting Point.	 */
	public AFloatImprover(final double _x, final double _y) {
		init(_x, _y); }
	
	///////////////////////////////////////////////////////////////////////////
	
}
