package structure; //

/**
  * A second concrete {@link Visitor}, identical in shape to {@link Visitor1}, that also
  * delegates each visit back to the Element's own {@link Element#invite}.
  *
  * Defines the Interface for a Visitor for the concrete Classes
  * @see ElementA
  * @see ElementB
  *
  * This Interface has to be extended with a Method for each new possible Element Class.
  * Thus it should only be used when the Number of Element Classes is stable,
  * but the Operations (Visitors) have to be varied and open in Number.
  *
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-23-2002, 04:19 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:18:42Z
  * digest: 28c4c02f74c16fd2226ee5ab72fb050c3ba60b707f2b65292b1814c779d9cc26
  * stale: false
  * tags: [code/visitor_pattern]
  * concepts: [Concrete Visitor 2]
  * facets: {layer: utility, status: broken, complexity: low}
  * -->
  */
public class Visitor2
implements   Visitor {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Method called only by ElementA Instances
	  * This Implementation varies with the Visitor AND the concrete Element
	  * Either implement the Behavior here (adding new Visitors)
	  * or delegate it back to the appropriate Element Class's invite() Method.
	  */
	public void visit(ElementA elA) {
		//implement the Behavior here; delegating back to elA.invite(this) would recurse forever,
		//because ElementA.invite(Visitor) calls v.visit(this) right back.
		System.out.println("Visitor2 visits ElementA " + elA);
	}

	/** Method called only by ElementB Instances
	  * This Implementation varies with the Visitor AND the concrete Element
	  * Either implement the Behavior here (adding new Visitors)
	  * or delegate it back to the appropriate Element Class's invite() Method.
	  */
	public void visit(ElementB elB) {
		//implement the Behavior here; delegating back to elB.invite(this) would recurse forever.
		System.out.println("Visitor2 visits ElementB " + elB);
	}

}
