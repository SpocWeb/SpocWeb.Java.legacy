package function.real;

/**
  * Title: Multiplier<p>
  * Description: AKA 'Scaler'
  * Filter for Number Streams.
  * Implements an Optimization by precalculating the Scaling Factor
  * taking the Generator's MaxValue into Account
  * thus saving one float Point Multiplication
  *
  * Known SubClasses:
  * @see FilterInFloatLin
  *
  * See also: 
  * @see function.real.Adder
  * @see function.real.Multiplier
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T16:44:16Z
  * digest: 637f145847b8b2d5d7a6a6d6294a851ac0db242905c42052a93b6fffe05bff58
  * stale: false
  * tags: [code/running_aggregates, code/mathematical_function]
  * concepts: [Streaming Numeric Aggregator]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Multiplier 
	extends StatefulFloatFunction {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Returns the configured scaling factor.
	 * @return the Scaling Factor taking the Generator's MaxValue into Account	 */
	public double getScale() { return _Value; }

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor
	  * defaulting the Scale to 0.0
	  * @param Generator the actual IStreamInNumber 	*/
	//	public Multiplier() { super(); }

	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Maximum Value returned by this Filter */
	public Multiplier(final double Scale) {
		super(Scale);
	}

	/** Scales {@code value_} by the configured factor.
	 * @return the next single Precision Number	 */
	public float Map(final float value_) {
		//		++count;
		return (float) (value_ * _Value);
	}

	/** Scales {@code value_} by the configured factor.
	 * @return the next double Precision Number	 */
	public double Map(final double value_) {
		//		++count;
		return value_ * _Value;
	}

}
