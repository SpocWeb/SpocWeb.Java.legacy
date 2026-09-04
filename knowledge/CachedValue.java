/**
 * File  Name: CachedValue.java
 * Created on: 03.11.2002
 */
package knowledge;

import function.byref.CachedCountAble;
import function.byref.CachedMeasurAble;

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

	/** asserts that this Value is dirty! 
	 * @throws IllegalArgumentException otherwise
	 */
	final public void assertIsDirty(boolean dirty_) 
	throws IllegalArgumentException {
		if (dirty == dirty_) { // isDirty()) {
			return; }
		if (calculator == null) {
			calculator.run(); //call a Callback to set the Value
		} else {
			throw new IllegalArgumentException("Value is " + (dirty? "already set!":"not set yet!")); 
		}			
	}
		
}
