package graphs;

import streamIO.object.enumer.ListItem;
import tester.IEquivalence;

/**
  * Title: ILinkAble.java<p>
  * Description:
  * Implementors of this Interface are used for linked Lists and upward navigable Trees.
  * Defines the Interface for building up explicitly recursively upward navigable Structures
  * like (singly) linked Lists or Hierarchies.
  * Typically used to define disjoint Sets resp. an Equivalence Relation
  * @see IEquivalence
  *
  * Known SubInterfaces:
  * @see IPair
  *
  * Known Implementors:
  * @see ListItem
  *
  *
  * Algorithms to use these Structures are defined in
  * @see streamIO.Object.Enumerator.ListItem
  *
  * Known SubInterfaces: (none)
  *
  * Known Implementors:
  * @see streamIO.Object.Enumerator.ListItem
  *
  * similar Classes:
  * @see streamIO.Object.IPair which handles two References
  * @see streamIO.Object.Pair  whereas this Interface only deals with one!
  * @see streamIO.Copy.IMonoid.Pair
  * @see streamIO.Copy.IMonoid.Association
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-07-20, 03;55;09<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: 02f8a32a2515220d0985bf0096e93989a761924ec16a4a4c6c64ddd328d9b6ff
  * stale: false
  * tags: [code/graph_edge]
  * concepts: [Linkable Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface ILinkAble //
extends ILinked //, IPair //although setVal() takes only an untyped Object!
{  //Pair has both Item and Parent!

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	// static Methods for handling Disjoint Sets are defined in class ListItem
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method:
	  * @param sets the Parent of this ILinkAble */
	void setPrnt(ILinked parent);
	
	////////////////////////////////////////////////////////////////////////////////
	//  public Methods
	////////////////////////////////////////////////////////////////////////////////
	
}
