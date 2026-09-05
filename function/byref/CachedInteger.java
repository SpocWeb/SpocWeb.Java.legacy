/**
 * File  Name: CachedCategorizeAble.java
 * Created on: 03.11.2002
 */
package function.byref;

/**
 * Caching decorator over an {@link IInteger}, marking the cache dirty before every write
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
 * mtime: 2026-09-05T20:54:25Z
 * digest: 00b8b58e00e06726b1cc8b05b66570e7204358f5c9f3e5710adc7014ea9a3b7a
 * stale: false
 * tags: [code/caching, code/function_wrapper]
 * concepts: [Caching Decorator]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class CachedInteger
extends CachedCountAble
implements IInteger {

	/** Initializing Constructor */
	public CachedInteger (IInteger inner_) {
		super(inner_);
	}

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setByte(byte)
	 */
	public void setByte(byte val) {
		assertIsDirty(true);
		((ICategorizeAble) inner).setByte(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setShort(short)
	 */
	public void setShort(short val) {
		assertIsDirty(true);
		((ICategorizeAble) inner).setShort(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setInt(int)
	 */
	public void setInt(int val) {
		assertIsDirty(true);
		((ICategorizeAble) inner).setInt(val); }

	/** Marks the cache dirty, then delegates to the inner value.
	 * @see function.ByRef.ICategorizeAble#setLong(long)
	 */
	public void setLong(long val) {
		assertIsDirty(true);
		((ICategorizeAble) inner).setLong(val); }

}
