package graphs;

/**
  * Title: ICPair<p>
  * Description:
  * Defines the Interface for a Constant (read only) Object that links two Objects.
  * This is more modular and standalone than using the
  * @see ILinked which also allows to link Objects, but only with itself.
  * On the other hand, unlike in an ILinked structure
  * none of the paired Processors know anything about their Use in a linked structure, .
  *
  * This is a standalone Construction that can be used in several Contexts:
  * directly for ordered Pairs in Set Products
  * as Base Class for binary Operators like Add, Subt, Mul, Div, Cat
  * ListItem
  *
  * Known SubInterfaces:
  * @see IPair which also allows to change the inner Objects
  *
  * Known Implementors:
  * @see streamIO.Object.Pair
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  * @see streamIO.Object.Enumerator.ListItem
  *
  * Similar Interfaces:
  * @see ILinkAble
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-07-20, 03;28;30<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:42:13Z
  * digest: a1903fea526db7b8ec68adf2d1a8f525aa6a9e486a4fb1a5a214c032ab9da6ca
  * stale: false
  * tags: [code/pair_data_structure]
  * concepts: [Comparable Pair Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface ICPair
extends ICValue {

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method
	  * @return the key of the Pair */
	public Object getKey();
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////////
	
}
