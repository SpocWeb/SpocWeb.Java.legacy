package function.real;

/**
  * Title: Minimum<p>
  * Description:
  * Evaluates the Maximum of the Elements of the streamIO, but hands them on unchanged,
  * so also other Operations can take place on them.
  * Can filter in both Directions (as Input and Output streamIO!)
  * The current Minimum and Number of Elements can be queried anytime!
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Minimum
	extends StatefulFloatFunction {

	/** Initializing Constructor
	  * defaulting the Start Value to +Infinity
	  */
	public Minimum() { super(Double.POSITIVE_INFINITY); }

	/** Initializing Constructor
	  * @param startValue_ Maximum Value returned by this Filter */
	public Minimum(double startValue_) { super(startValue_); }

	/** @return the next single Precision Number	 */
	public float Map(final float value_) { 
		++_Count; if (_Value > value_) _Value = value_; return value_; }

	/** @return the next double Precision Number	 */
	public double Map(final double value_) { 
		++_Count; if (_Value > value_) _Value = value_; return value_; }

}
