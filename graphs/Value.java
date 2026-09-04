package graphs;

/**
  * Title: Value<p>
  * Description:
  * Purpose:
  * implements the IValue Interface
  * Purpose / Responsibilities of this Class
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: 
  * @see asynch.Future cannot derive, because Accessors are made final for Performance Reasons
  * @see graphs.KeyValuePair
  * Pair
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 10:09 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Value
extends CValue
implements IValue {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface IValue: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @param sets Value of the Pair */
	final public void setVal(final Object Value) { this.val = Value; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public Value() { }

	/** Initializing Constructor,
	  * here not calling the Base Constructor to save the Call!	 */
	public Value(final Object Value) { val = Value; }
	
}

