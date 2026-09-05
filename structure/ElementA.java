package structure; //

/**
  * One concrete {@link Element} that dispatches {@link Visitor#visit(ElementA)} on itself.
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
  * mtime: 2026-09-05T11:14:33Z
  * digest: a3e81229be2d6a6e60373f6d65603e81f2e6586cdf6b161b19ba914b9e72fbac
  * stale: false
  * tags: [code/visitor_pattern]
  * concepts: [Concrete Visitable Element A]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class ElementA
implements   Element {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Method that accepts a Visitor and acts based on this Type of Element 	 */
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
