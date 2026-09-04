package structure; //

/**
  * Title: Visitor2<p>
  * Description:
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
		elA.invite(this); //delegate it back...
	}

	/** Method called only by ElementB Instances
	  * This Implementation varies with the Visitor AND the concrete Element
	  * Either implement the Behavior here (adding new Visitors)
	  * or delegate it back to the appropriate Element Class's invite() Method.
	  */
	public void visit(ElementB elB) {
		elB.invite(this); //delegate it back...
	}

}
