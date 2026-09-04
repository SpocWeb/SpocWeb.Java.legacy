package streamIO.object;

import streamIO.IIStreamIn;

/**
 * FilterStreamIn.java
 * NOP-Stream, hands the Elements through unchanged. 
 * Delegates to any IStreamIn Implementor and thus allows the Use of StreamIn
 * without these Implementors having to implement the whole StreamIn Interface.
 * 
 * Created on 3. März 2001, 22:32
 * Known SubClasses: AEnumerator
 *
 * @author  Matthias Heuer
 * @version
 */
public class FilterIn
extends AFilterIn {
	
	/** Creates new FilterStreamIn delegating to the given Stream */
	public FilterIn (final IIStreamIn enum_) { super(enum_); }
	
	/** Creates new FilterStreamIn for late Initialization */
	//private FilterIn () { }
	
	/** this is the abstract Template Method 	*/
	protected Object nextItemInternal() { return in.nextItem(); }
	
}
