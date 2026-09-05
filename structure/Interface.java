package structure; //

/**
  * Extends {@link IInterface} with the full {@link #complexOp} Operation, separating out an
  * Interface so complex-Operation Optimizations remain possible without committing to a
  * concrete Class.
  *
  * Defines the full Interface for a Class.
  * Separating out the (full) Interface instead of using a concrete abstract Class
  * allows to introduce Optimizations on the complex Operations later
  * and still not to commit on a concrete Class yet, but to an Interface,
  * allowing Optimizations like:
  * -not relying on any Implementation.
  * -making Implementation Classes final and still mix with their Interface.
  * -...
  *
  * Known SubInterfaces: There may be many...
  *
  * Known Implementors:
  * Abstraction defines the basic Algorithm of complexOp(),
  * but leaves the concrete simpleOp() open.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-16-2002, 07:59 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see  IInterface , the minimum Interface simpleOp()
  * @see   Interface , the full Interface complexOp()
  * @see  Abstraction, the abstract Implementation of complexOp() using simpleOp()
  * @see  Delegator  , inheriting from Abstraction and delegating simpleOp()
  * @see      Bridge , bridging only simpleOp(), no Abstraction, no Base Class!
  * @see  FullBridge , bridging the full 'Interface' Methods, no Abstraction!
  * @see DAbstraction, the abstract Implementation of complexOp using Delegation to simpleOp
  * @see     DBridge , bridging the minimum Interface Methods AND inheriting from DAbstraction
  * @see DFullBridge , bridging the full 'Interface' Methods, overwriting complexOp
  *
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:16:35Z
  * digest: bfe0792ea77b77a67b7ce9adf1110eb7f5b1dac58f2a34bdd0dd1e910d67f419
  * stale: false
  * tags: [code/bridge]
  * concepts: [Bridge Interface Implementation]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface Interface
extends IInterface {

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** complex Operation,
	  * usually relying on the simple Operations defined in IInterface.
	  *
	  * This is THE Mechanism to separate out the stable Parts of an Algorithm
	  * from the variable Parts, which comes, e.g. with different concrete Classes.
	  * The Strategy Pattern varies the Algorithm on the same concrete Classes.
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	Object complexOp(Object param);

}
