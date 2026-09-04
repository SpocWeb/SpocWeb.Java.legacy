package structure; //

/**
  * Title: ElementB<p>
  * Description:
  * Defines the Interface for an Element being visited by a Visitor.
  * This is a generic Interface, except for the Fact
  *  that the Visitor Interface used is specific
  *  to the Number and Types of Elements visited.
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
