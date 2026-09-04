/**
 * File  Name: CachedDouble.java
 * Created on: 03.11.2002
 */
package function.byref;

/**
 * Title: CachedDouble<p>
 * Description:
 * Purpose:
 *
 * Design Decisions / Implementation Details:
 * 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * @see structure.blackBoard.triangle.Triangle 
 * where CachedDouble could replace the internal Double Array 
 * and automatically trigger the Calculation. 
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class CachedDouble 
extends CachedMeasurAble //ByRefDouble 
implements IFloat
{

	/**
	 * Constructor for CachedDouble.
	 * @param inner_
	 */
	public CachedDouble(IFloat inner_) {
		super(inner_);
	}

	/**
	 * @see function.ByRef.IAdjustAble#setDouble(double)
	 */
	public void setDouble(double val) {
		assertIsDirty(true);
		((IFloat) inner).setDouble(val); }

	/**
	 * @see function.ByRef.IAdjustAble#setFloat(float)
	 */
	public void setFloat(float val) {
		assertIsDirty(true);
		((IFloat) inner).setFloat(val); }

	/**
	 * @see function.ByRef.ICategorizeAble#setByte(byte)
	 */
	public void setByte(byte val) {
		assertIsDirty(true);
		((IFloat) inner).setByte(val); }

	/**
	 * @see function.ByRef.ICategorizeAble#setInt(int)
	 */
	public void setInt(int val) {
		assertIsDirty(true);
		((IFloat) inner).setInt(val); }

	/**
	 * @see function.ByRef.ICategorizeAble#setLong(long)
	 */
	public void setLong(long val) {
		assertIsDirty(true);
		((IFloat) inner).setLong(val); }

	/**
	 * @see function.ByRef.ICategorizeAble#setShort(short)
	 */
	public void setShort(short val) {
		assertIsDirty(true);
		((IFloat) inner).setShort(val); }

}
