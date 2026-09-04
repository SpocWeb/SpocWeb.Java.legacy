/**
 * File  Name: CachedCategorizeAble.java
 * Created on: 03.11.2002
 */
package function.byref;

/**
 * Title: enclosing_type<p>
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
 * @author mheuer
 * @version	1.0
 *
 */
public class CachedInteger 
extends CachedCountAble 
implements IInteger {

	/** Initializing Constructor */
	public CachedInteger (IInteger inner_) {
		super(inner_);
	}

	/**
	 * @see function.ByRef.ICategorizeAble#setByte(byte)
	 */
	public void setByte(byte val) {
		assertIsDirty(true); 
		((ICategorizeAble) inner).setByte(val); }

	/**
	 * @see function.ByRef.ICategorizeAble#setShort(short)
	 */
	public void setShort(short val) {
		assertIsDirty(true);
		((ICategorizeAble) inner).setShort(val); }

	/**
	 * @see function.ByRef.ICategorizeAble#setInt(int)
	 */
	public void setInt(int val) {
		assertIsDirty(true);
		((ICategorizeAble) inner).setInt(val); }

	/**
	 * @see function.ByRef.ICategorizeAble#setLong(long)
	 */
	public void setLong(long val) {
		assertIsDirty(true);
		((ICategorizeAble) inner).setLong(val); }

}
