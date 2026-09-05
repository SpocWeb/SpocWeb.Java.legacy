package graphs;

import streamIO.object.enumer.ListItem;
import tester.IEquivalence;

/**
  * Title: ILinked.java<p>
  * Description:
  * These are used for linked Lists and upward navigable Trees.
  * Defines the Interface for explicitly recursively upward navigable Structures
  * like (singly) linked Lists or Hierarchies.
  * Typically used to define disjoint Sets resp. an Equivalence Relation
  * @see IEquivalence
  * @see graphs.EquivalenceByParent which uses this Interface 
  * to rapidly determine the Equivalence of Objects in undirected Connected Components
  *
  * Known SubInterfaces:
  * @see ILinkAble
  *
  * Known Implementors:
  * @see ListItem
  *
  * Similar Interfaces:
  * This is less modular and standalone than using the
  * @see IPair which also allows to link two Objects.
  * On the other hand, unlike in an ILinked structure
  * none of the paired Processors know anything about their Use in a linked structure, .
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2000-08-13, 04;06;42<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:18Z
  * digest: 866d059657e42585b77618772023c2dd3e668f289e0a60224684696e3e27bcbd
  * stale: false
  * tags: [code/graph_edge]
  * concepts: [Linked Interface]
  * facets: {layer: domain, status: legacy, complexity: low}
  * -->
  */
public interface ILinked
//extends ICPair //Pair has both Item and Parent!
{

	////////////////////////////////////////////////////////////////////////////////
	//  Accessor Methods (getXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** Accessor Method:
	  * @return the final Parent == Root
	  * getRoot().getKey() is equivalent to StreamIn.lastItem()
	  * and is used to handle disjoint Sets
	  * It can be implemented using iterated getParent() Methods,
	  * but the Reason to make this Method virtual is 
	  * that there are different possible Implementations
	  * depending on the Strategy to minimize the Distance to the Root Elemet
	  * by dynamically updating the Parent Reference.  */
	ILinked getRoot();
	
	/** Accessor Method:
	  * @return the Parent of this ILinked */
	ILinked getPrnt();
	
}
