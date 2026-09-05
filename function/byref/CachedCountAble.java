/**
 * File  Name: CachedCountAble.java
 * Created on: 03.11.2002
 */
package function.byref;

import knowledge.CachedValue;
import function.ICountAble;

/**
 * Caching decorator over an {@link ICountAble}, asserting the cache is clean before every read
 * delegates to the wrapped value.
 *
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:54:01Z
 * digest: bd3cb1b008548e5e3718ec24a3d82c518161305e21dacaa26844f13d2b32ba7d
 * stale: false
 * tags: [code/caching, code/function_wrapper]
 * concepts: [Caching Decorator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
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

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.ICountAble#getByte()
	 */
	public byte getByte() {
		assertIsDirty(false);
		return inner.getByte(); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.ICountAble#getShort()
	 */
	public short getShort() {
		assertIsDirty(false);
		return inner.getShort(); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.ICountAble#getInt()
	 */
	public int getInt() {
		assertIsDirty(false);
		return inner.getInt(); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.ICountAble#getLong()
	 */
	public long getLong() {
		assertIsDirty(false);
		return inner.getLong(); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IMeasurAble#getDouble()
	 */
	public double getDouble() {
		assertIsDirty(false);
		return inner.getDouble(); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IMeasurAble#getFloat()
	 */
	public float getFloat() {
		assertIsDirty(false);
		return inner.getFloat(); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IOrderAble#isBetween(Object, Object)
	 */
	public boolean isBetween(Object arg1, Object arg2) {
		assertIsDirty(false);
		return inner.isBetween(arg1, arg2); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IOrderAble#isMoreThan(Object)
	 */
	public boolean isMoreThan(Object arg) {
		assertIsDirty(false);
		return inner.isMoreThan(arg); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IOrderAble#notLessThan(Object)
	 */
	public boolean notLessThan(Object arg) {
		assertIsDirty(false);
		return inner.notLessThan(arg); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IOrderAble#notMoreThan(Object)
	 */
	public boolean notMoreThan(Object arg) {
		assertIsDirty(false);
		return inner.notMoreThan(arg); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IOrderAble#Position(Object)
	 */
	public int Position(Object arg) {
		assertIsDirty(false);
		return inner.Position(arg); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see function.IIOrderAble#isLessThan(Object)
	 */
	public boolean isLessThan(Object arg) {
		assertIsDirty(false);
		return inner.isLessThan(arg); }

	/** Asserts the cache is not dirty, then delegates to the inner value.
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	public int compareTo(Object arg0) {
		assertIsDirty(false);
		return inner.compareTo(arg0); }

}
