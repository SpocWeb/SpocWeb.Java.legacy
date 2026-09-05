package structure; //

/**
  * Declares one {@link #visit} Overload per fixed {@link Element} Type, letting an open Set
  * of Visitors vary the Operation performed on a closed Set of Elements.
  *
  * Defines the Interface for a Visitor for the concrete Classes
  * @see ElementA
  * @see ElementB
  *
  * This Interface has to be extended with a Method for each new possible Element Class.
  * Thus it should only be used when the Number of Element Classes is stable,
  * but the Operations (Visitors) have to be varied and open in Number.
  *
  * @see Element the Base Class for Elements to be visited
  * @see ElementA
  * @see ElementB
  * @see Visitor the Base Class for Operations on Elements
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors:
  * @see VisitorA
  * @see VisitorB
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-23-2002, 04:19 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:18:31Z
  * digest: 28c4c02f74c16fd2226ee5ab72fb050c3ba60b707f2b65292b1814c779d9cc26
  * stale: false
  * tags: [code/visitor_pattern]
  * concepts: [Visitor Interface]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface Visitor {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Method called only by ElementA Instances
	  * This Implementation varies with the Visitor AND the concrete Element
	  * Either implement the Behavior here (adding new Visitors)
	  * or delegate it back to the appropriate Element Class's invite() Method.
	  */
	void visit(ElementA elA);

	/** Method called only by ElementB Instances
	  * This Implementation varies with the Visitor AND the concrete Element
	  * Either implement the Behavior here (adding new Visitors)
	  * or delegate it back to the appropriate Element Class's invite() Method.
	  */
	void visit(ElementB elB);

}
