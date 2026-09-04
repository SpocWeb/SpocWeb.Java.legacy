package streamIO.real.random;

import streamIO.integer.random.IStreamIn_Bound_Int;
import streamIO.integer.random.RandomQuick;
import streamIO.real.FilterInLin;
import streamIO.real.IStreamIn_Float;

/**Returns random Numbers distributed in a Pareto fashion  */
public class RandomPareto
extends ARandomFloat //FilterIn_FloatByFunction 
//implements IReSetAble 
{
    /** The Standard Distribution with k=1     */
	final static public RandomPareto RANDOM = new RandomPareto(1); 
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public double NEXT_DOUBLE() { return RANDOM.nextDouble(); }
	
	/**Random double Precision Number from the static Random Number Generator	 */
	final static public float NEXT_FLOAT() { return RANDOM.nextFloat(); }
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** the reciprocal and negated Coefficient */
	public final double kRcpNeg;
	
	/** Constructor that takes a Random Number Generator
	  * new Generator generates Values in [-1,+1) 	 */
	public RandomPareto(double k) { 
		this(k, (IStreamIn_Bound_Int) new RandomQuick()); }

	/** Constructor that takes a Random Number Generator
	  * new Generator generates Values in [-1,+1) 	 */
	public RandomPareto(double k, final IStreamIn_Bound_Int ran)	{ 
		super(new FilterInLin(ran, 0, 1));
		kRcpNeg = -1 / k;
	}

	/** Constructor that takes a Random Number Generator
	  * new Generator generates Values in [-1,+1) 	 */
	public RandomPareto(double k, final IStreamIn_Float ran) { 
		super(ran); 
		kRcpNeg = -1 / k;
	}

	/** @see streamIO.real.random.ARandomFloat#getMinDouble()	 */
	public double getMinDouble() { return 1; }
	
	/**Random double Precision Number using 1/x Transformation	 */
	protected double nextDoubleInternal() {
		return Math.pow(ran.nextDouble(), kRcpNeg);	//und eine für sofort
	}
	
}
