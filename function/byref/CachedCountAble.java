/**
 * File  Name: CachedCountAble.java
 * Created on: 03.11.2002
 */
package function.byref;

import knowledge.CachedValue;
import function.ICountAble;

/**
 * Title: enclosing_type<p>
 * Description:
 * Purpose:
 *
 *
 * Design Decisions / Implementation Details:
 * This is a Filter / Decorator Class 
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
public class CachedCountAble 
extends CachedValue 
implements ICountAble {// ICategorizeAble {

	/** Reference to the inner Value to protect */
	protected ICountAble inner;

	/** Initializing Constructor */
	public CachedCountAble(ICountAble inner_) {
		this.inner = inner_;
	}

	/**
	 * @see function.ICountAble#getByte()
	 */
	public byte getByte() {
		assertIsDirty(false);
		return inner.getByte(); }

	/**
	 * @see function.ICountAble#getShort()
	 */
	public short getShort() {
		assertIsDirty(false);
		return inner.getShort(); }

	/**
	 * @see function.ICountAble#getInt()
	 */
	public int getInt() {
		assertIsDirty(false);
		return inner.getInt(); }

	/**
	 * @see function.ICountAble#getLong()
	 */
	public long getLong() {
		assertIsDirty(false);
		return inner.getLong(); }

	/**
	 * @see function.IMeasurAble#getDouble()
	 */
	public double getDouble() {
		assertIsDirty(false);
		return inner.getDouble(); }

	/**
	 * @see function.IMeasurAble#getFloat()
	 */
	public float getFloat() {
		assertIsDirty(false);
		return inner.getFloat(); }

	/**
	 * @see function.IOrderAble#isBetween(Object, Object)
	 */
	public boolean isBetween(Object arg1, Object arg2) {
		assertIsDirty(false);
		return inner.isBetween(arg1, arg2); }

	/**
	 * @see function.IOrderAble#isMoreThan(Object)
	 */
	public boolean isMoreThan(Object arg) {
		assertIsDirty(false);
		return inner.isMoreThan(arg); }

	/**
	 * @see function.IOrderAble#notLessThan(Object)
	 */
	public boolean notLessThan(Object arg) {
		assertIsDirty(false);
		return inner.notLessThan(arg); }

	/**
	 * @see function.IOrderAble#notMoreThan(Object)
	 */
	public boolean notMoreThan(Object arg) {
		assertIsDirty(false);
		return inner.notMoreThan(arg); }

	/**
	 * @see function.IOrderAble#Position(Object)
	 */
	public int Position(Object arg) {
		assertIsDirty(false);
		return inner.Position(arg); }

	/**
	 * @see function.IIOrderAble#isLessThan(Object)
	 */
	public boolean isLessThan(Object arg) {
		assertIsDirty(false);
		return inner.isLessThan(arg); }

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object arg0) {
		assertIsDirty(false);
		return inner.compareTo(arg0); }

}
