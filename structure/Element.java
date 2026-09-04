package structure; //

/**
  * Title: Element<p>
  * Description:
  * Defines the Interface for an Element being visited by a Visitor.
  * This is a generic Interface, except for the Fact
  *  that the Visitor Interface used is specific
  *  to the Number and Types of Elements visited.
  * If the specific Behavior is to be implemented in the Element Classes,
  * it can be delegated back to specific visit() Methods,
  * but that triple Dispatch increases the Interface Coupling between Element and Visitor.
  * It is better to reverse Control then and start Dispatching from the Visitor Class.
  *
  * @see Element the Base Class for Elements to be visited
  * @see Visitor the Base Class for Operations on Elements
  * @see VisitorA
  * @see VisitorB
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors:
  * @see ElementA
  * @see ElementB
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-23-2002, 04:22 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface Element {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Method that accepts a Visitor and acts based on the Type of Element 	 */
	void invite(Visitor v);

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
