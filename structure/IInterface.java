package structure; //

/**
  * Declares the single primitive {@link #simpleOp} Operation a Class Hierarchy must
  * implement, minimizing the Effort of mixing in new Implementations.
  *
  * Defines the minimum Interface for a Class Hierarchy.
  * This is often used to separate out the basic Operations for Clarity
  * and to make Mixing of Implementations easier,
  * by minimizing the Number of Operations to implement.
  *
  * The Drawback of using only this Interface is
  * that Optimizations in the derived Operations cannot be realized,
  * because only the Default Implementations are used
  * by the abstract Adaptors.
  *
  * Known SubInterfaces:
  * Interface
  *
  * Known Implementors:
  * Classes that want to minimize the Effort
  * and implement only the simple Operations.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-16-2002, 07:53 PM<p>
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
  * mtime: 2026-09-05T11:16:30Z
  * digest: e9afd23044fad9d1b683b9e1697e36a7b8d3d9a0d095da868d4d3d965c8162ce
  * stale: false
  * tags: [code/bridge]
  * concepts: [Bridge Interface]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public interface IInterface
{

////////////////////////////////////////////////////////////////////////////////
//  public Methods
////////////////////////////////////////////////////////////////////////////////

	/** basic simple ('primitive') Operation for later Overloading
	  * Complex Operations in Interface delegate to it,
	  * allowing for Compile Time Variation of Behavior.
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	Object simpleOp(Object param);

}
