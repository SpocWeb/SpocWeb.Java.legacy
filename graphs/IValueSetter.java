package graphs; //

/**
  * Title: IValueSetter<p>
  * Description:
  * Defines a separate Interface for the setVal Method.
  * This is similar to the addItem() Method of IStreamOut,
  * but the Name and Signature conforms to the Bean Pattern.
  *
  * IValueSetter has been separated out as a separate Interface,
  * because it is used in the IFuture Pattern (asynchronous)
  * as well as for a Callback Reference (synchronous).
  * Thus the Reduction to this Interface
  * allows to return an asynchronously calculated Return Value
  * either using a Future:
  * -polling
  * -synchronous waiting (with optional Timeout)
  * or as an asynchronous Callback via this Interface directly back into the Client!
  *
  * Known SubInterfaces: IValue, IFuture, ICPair
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 09:29 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: a1903fea526db7b8ec68adf2d1a8f525aa6a9e486a4fb1a5a214c032ab9da6ca
  * stale: false
  * tags: [code/graph_element]
  * concepts: [Value Setter Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface IValueSetter {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @param sets Value of this Object */
	public void setVal(Object val);
	
}

