/*
 * Created on 03.03.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package math.refiner;


/**
 * Holds the current x/y iteration state (last step size and last point) shared by every
 * {@link AFloatRefiner} subclass, independent of any particular improvement algorithm.
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:54:49Z
 * digest: c390a61249c87b88d499ad006c6804965d4b9fb7dce85816d29c4266dfe1dd53
 * stale: false
 * tags: [code/fixed_point_iteration]
 * concepts: [Iterative Improver Base Class]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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
