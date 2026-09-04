package function.real;

/**
  * Title: FilterSqlSum<p>
  * Description:
  * Sums up the Elements of the streamIO, but hands them on unchanged,
  * so also other Operations can take place on them. 
  * 
  * Summing up Squares is numerically instable, 
  * especially when not normalized by the Mean 
  * or at least the first Element of the Sequence. 
  * 
  * @see streamIO.Float.Sum which calculates the simple Sum. 
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-11-26, 01;13;44<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class SumSquares 
	extends StatefulFloatFunction {

	/** Initializing Constructor
	  * defaulting the Value to 0.0
	  * @param Generator the actual IStreamInNumber 	*/
//	public SumSquares() { super(); }

	/** Initializing Constructor
	  * @param Generator the actual IStreamInNumber
	  * @param Scale     Maximum Value returned by this Filter */
	public SumSquares(final double StartValue) {
		super(StartValue); }

	/** @return the next single Precision Number	 */
	public float Map(final float value_) { //return (float) (Sum += Generator.nextDouble()); }
		++_Count; _Value += value_*value_; return value_; }

	/** @return the next double Precision Number	 */
	public double Map(final double value_) { //return Sum += Generator.nextDouble(); }
		++_Count; _Value += value_*value_; return value_; }

	/** @return the SMV, Square Mean Value
	  * of the Elements passed through this Node
	  * @throws NAN when no Element has passed this Node yet.  */
	public double getSMV() {
		return _Value / _Count; }

}
