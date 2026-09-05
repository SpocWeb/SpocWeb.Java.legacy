package graphs;

import streamIO.copy.monoid.Association;

/**
  * Title: ICValue<p>
  * Description:
  * Defines the Interface for a stateful Constant Objet
  * that can return (a Copy or Read Only Version of) its Value.
  * This Interface is related to IStreamIn nextItem(),
  * which returns a different Item each Time it is called.
  *
  * Related Interfaces:
  * @see streamIO.IFactory which should always return a different Object using nextItem()
  *
  * Known SubInterfaces:
  * @see graphs.IValue which adds a set() Method
  *
  * Known Implementors:
  * @see Pair
  * @see Association
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 10:03 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:14Z
  * digest: a1903fea526db7b8ec68adf2d1a8f525aa6a9e486a4fb1a5a214c032ab9da6ca
  * stale: false
  * tags: [code/graph_element]
  * concepts: [Comparable Value Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface ICValue {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * This is related to the nextItem() Method of IStreamIn,
	  * but always returns semantically the same Item!
	  * @return the Value or State of this Object */
	public Object getVal();
	
}

