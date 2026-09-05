/**
 * File  Name: CachedMeasurAble.java
 * Created on: 03.11.2002
 */
package function.byref;

import knowledge.CachedValue;
import function.IMeasurAble;

/**
 * Caching decorator over an {@link IMeasurAble}, asserting the cache is clean before every read
 * delegates to the wrapped value.
 *
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:54:43Z
 * digest: f3abb9edf8101c454b63d32a89c0a019cbe7f0cd8714447d628547eaa465a671
 * stale: false
 * tags: [code/caching, code/function_wrapper]
 * concepts: [Caching Decorator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class CachedMeasurAble
extends CachedValue
implements IMeasurAble {

	/** Reference to the inner Value to protect */
	protected IMeasurAble inner;

	/** Initializing Constructor */
	public CachedMeasurAble(IMeasurAble inner_) {
		this.inner = inner_;
	}

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
