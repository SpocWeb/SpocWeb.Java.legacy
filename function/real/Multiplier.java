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
  */
public class Multiplier 
	extends StatefulFloatFunction {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////

	/** @return the Scaling Factor taking the Generator's MaxValue into Account	 */
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

	/** @return the next single Precision Number	 */
	public float Map(final float value_) {
		//		++count;
		return (float) (value_ * _Value);
	}

	/** @return the next double Precision Number	 */
	public double Map(final double value_) {
		//		++count;
		return value_ * _Value;
	}

}
