package graphs;

/**
  * Title: CValue<p>
  * Description:
  * Purpose:
  * implements the ICValue Interface
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: Value, Future
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 10:10 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class CValue
implements ICValue {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** The Value of this Pair
	  * Due to the Fact that most Algorithms use the Interface,
	  * and the actual Class is not relied upon,
	  * the public Properties should not be tampered with
	  * and are only for Performance Boosting in critical Situations.
	  */
	public Object val;
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface ICValue: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @return Value */
	final public Object getVal() { return val; }
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Empty Constructor	 */
	public CValue() { }

	/** Empty Constructor	 */
	public CValue(final Object Value) { val = Value; }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Object Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see java.lang.Object#toString()	 */
	public String toString() { return String.valueOf(val); }
	
}

