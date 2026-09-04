package function.real;

/**
  * Title: FilterOutSum<p>
  * Description:
  * Sums up the Elements of the streamIO, but hands them on unchanged,
  * so also other Operations can take place on them. 
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Product 
	extends StatefulFloatFunction {

	/** Initializing Constructor
	  * defaulting the Value to 0.0
	  * @param Generator the actual IStreamInNumber 	*/
//	public Product() { super(); }

	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Start Value returned by this Filter */
	public Product(final double value_) { super(value_); }

	/** @return the next single Precision Number	 */
	public float Map(final float value_) { //
		++_Count; _Value *= value_; return value_; }

	/** @return the next double Precision Number	 */
	public double Map(final double value_) { //
		++_Count; _Value *= value_; return value_; }

	/** @return the HMV, Harmonic Mean Value
	  * of the Elements passed through this Node
	  * @throws NAN when no Element has passed this Node yet.  */
	public double getHMV() {
		return Math.pow(_Value, 1.0 / _Count);
	}

}
