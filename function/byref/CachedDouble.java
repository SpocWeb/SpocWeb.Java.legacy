/**
 * File  Name: CachedDouble.java
 * Created on: 03.11.2002
 */
package function.byref;

/**
 * Caching decorator over an {@link IFloat}, marking the cache dirty before every write
 * delegates to the wrapped value.
 *
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:54:49Z
 * digest: f1a3426d63b22ebee07b722c4e307e72572fd0896e0400b772b306fab52dbf4c
 * stale: false
 * tags: [code/caching, code/function_wrapper]
 * concepts: [Caching Decorator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.IAdjustAble#setDouble(double)
	 */
	public void setDouble(double val) {
		assertIsDirty(true);
		((IFloat) inner).setDouble(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.IAdjustAble#setFloat(float)
	 */
	public void setFloat(float val) {
		assertIsDirty(true);
		((IFloat) inner).setFloat(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setByte(byte)
	 */
	public void setByte(byte val) {
		assertIsDirty(true);
		((IFloat) inner).setByte(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setInt(int)
	 */
	public void setInt(int val) {
		assertIsDirty(true);
		((IFloat) inner).setInt(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setLong(long)
	 */
	public void setLong(long val) {
		assertIsDirty(true);
		((IFloat) inner).setLong(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setShort(short)
	 */
	public void setShort(short val) {
		assertIsDirty(true);
		((IFloat) inner).setShort(val); }

}
