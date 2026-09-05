/*
 * File Name: ConstStreamIn_Float.java
 * Created on: 04.02.2004
 *
 */
package streamIO.real;

import streamIO.IReSetAble;
import streamIO.object.IStreamIn;

/**
 * Implements an infinite stream that always returns the same constant float/double value.
 *
 * <p>Since there is only a single (unfortunately non-atomic) Parameter to this Class,
 * Since there is only a single (unfortunately non-atomic) Parameter to this Class, 
 * this Parameter is made publicly available. 
 *
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
 * mtime: 2026-09-05T11:10:20Z
 * digest: 6c593b28e449aed39220b9f33d60684988ad8eabcd92bfdd5a524c1d57dbd6a2
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Constant Float Stream]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class ConstStreamIn_Float 
extends AStreamIn_Float {

	/** Empty Constructor defaulting the Value to 0 	 */
	public ConstStreamIn_Float() { }

	/** Initializing Constructor, setting the Value 	 */
	public ConstStreamIn_Float(final double value_) { currItem.Value = value_; }

	/** Returns the constant value held by this stream.
	 * @see streamIO.real.IStreamIn_Bound_Float#getMinDouble()	 */
	public double getMinDouble() { return currItem.Value; }

	/** Returns the constant value held by this stream, unchanged.
	 * @see streamIO.real.IStreamIn_Float#nextDouble()	 */
	public double nextDoubleInternal() { return currItem.Value; }

	/** Returns {@link IStreamIn#ORDER_CONST}, since a constant stream is trivially ordered.
	 * @see streamIO.real.IStreamIn_Float#getOrder()	 */
	public byte getOrder() { return IStreamIn.ORDER_CONST; }

	/** Returns 0, since a constant stream has no meaningful position.
	 * @see streamIO.real.AStreamIn_Float#getPosition()	 */
	public long getPosition() { return 0; }

	/** Does nothing, since a constant stream has no state to reset.
	 * @see streamIO.integer.IStreamIn_Int#reSet()	 */
	public IReSetAble reSet() { return this; }

	/** Returns {@link Long#MAX_VALUE}, since a constant stream never runs out of items.
	 * @see streamIO.real.AStreamIn_Float#availAble()	 */
	public long availAble() { return Long.MAX_VALUE; }

	/** Returns {@link Long#MAX_VALUE}, since a constant stream imposes no mark-size limit.
	 * @see streamIO.real.AStreamIn_Float#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }

}
