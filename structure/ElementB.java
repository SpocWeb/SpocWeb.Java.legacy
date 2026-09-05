package structure; //

/**
  * The other concrete {@link Element} that dispatches {@link Visitor#visit(ElementB)} on
  * itself.
  *
  * Known SubInterfaces:
  *
  * Known Implementors:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-23-2002, 04:22 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:14:35Z
  * digest: 6caba42c3d775598f95e0154955f1b69baf4e7641de4b670fbe9d226b94d62ab
  * stale: false
  * tags: [code/visitor_pattern]
  * concepts: [Concrete Visitable Element B]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class ElementB
implements   Element {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Method that accepts a Visitor and acts based on the Type of Element 	 */
	public void invite(Visitor v) {
		v.visit(this); }

	/** Method called by Visitor1
	  * This Method dependends on both Visitor Type AND this Element Type
	  * It could be enforced via the Interface 'Element'.
	  */
//	void invite(Visitor1 v) { }

	/** Method called by Visitor2
	  * This Method dependends on both Visitor Type AND this Element Type
	  * It could be enforced via the Interface 'Element'.
	  */
//	void invite(Visitor2 v) { }

}
