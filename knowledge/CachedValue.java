/**
 * File  Name: CachedValue.java
 * Created on: 03.11.2002
 */
package knowledge;

import function.byref.CachedCountAble;
import function.byref.CachedMeasurAble;

/**
 * A {@link DirtyFlag} whose clean state is restored on demand by a caller-supplied
 * {@link Runnable} rather than by whoever mutated it.
 *
 * <p>The inherited {@code dirty} field means "the inner value is not present"; the
 * {@link #calculator} is the callback expected to put it there. Subclasses own the value
 * itself - this class holds only the flag and the callback, which is why the value's type
 * is not fixed here.
 *
 * <p><b>Invariant:</b> {@link #calculator} must be set before a caller can rely on the
 * value being recomputed; it is a public field with no null guard, so an unset calculator
 * is not detected until it is needed.
 *
 * Known SubClasses:
 * @see CachedCountAble
 * @see CachedMeasurAble
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
 * mtime: 2026-09-05T08:10:13Z
 * digest: 327a7738f6b4f8450bcdd6bc19927b6d3e7fd0cfe659814458fd248eaae31460
 * stale: false
 * -->
 */
public class CachedValue extends DirtyFlag {

	////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////

	/** Reference to the Calculator if the inner Value is not present! 
	 * This is a Callback that should set the inner Value
	 * This could also be a streamIO that is used to set the Value
	 * possibly a Mapper would be better suited!
	 */
	public Runnable calculator;

	/**
	 * Returns silently when the dirty flag already equals the expected state, and otherwise
	 * tries to reach that state by running {@link #calculator}.
	 *
	 * @param dirty_ the dirty state this value is asserted to be in
	 * @throws IllegalArgumentException when the state differs and no calculator can fix it
	 */
	final public void assertIsDirty(boolean dirty_)
	throws IllegalArgumentException {
		if (dirty == dirty_) { // isDirty()) {
			return; }
		if (calculator != null) {
			calculator.run(); //call a Callback to set the Value
		} else {
			throw new IllegalArgumentException("Value is " + (dirty? "already set!":"not set yet!"));
		}
	}
		
}
