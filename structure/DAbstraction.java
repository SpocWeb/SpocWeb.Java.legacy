package structure; //

/**
  * Implements the common {@link Interface#complexOp} Algorithm like {@link Abstraction}, but
  * delegates the primitive Operation to a held {@link IInterface} Implementor rather than to
  * itself, so an unrelated Base Class can still be extended.
  *
  * This is an Economization for multiple Implementors of 'Interface'
  * sharing the same Algorithm.
  * They can use this Abstraction as a Base Class
  * (if possible and no other Base Class is more convenient) and...
  * * ...get Implementations for free
  * * the Implementations are automatically consistent
  * * only the relevant Differences are visible
  * * Optimizations are not realized automatically,
  *   because the Implementations here are only generic, but
  * * Optimizations are still possible in concrete Subclasses
  *   by overriding the Methods!
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-16-2002, 08:05 PM<p>
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
  * mtime: 2026-09-05T11:13:46Z
  * digest: bc8ca4029cac04647afdef77e76402e1aa446518ea5fff2d0094260789b26e3d
  * stale: false
  * tags: [code/bridge]
  * concepts: [Delegate-Based Bridge Abstraction]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public abstract class DAbstraction
//extends ...can possibly extend other DAbstractions, thus reducing Code Duplication.
implements Interface {

// by delegating to a Delegator (which is this Instance by Default)
// the Bridge can already be prepared in this Class,
// and the FullBridge Class can directly inherit from Abstraction
// thus allowing to redefine the Bridge and to save the Delegator Class
// and receive the full Performance Boost of the Bridge even when using the Delegator.
// The Cost for this is the Introduction of a local Reference to the Delegate (4 Byte)
// This Technique is necessary anyway if you want to exploit the Template Method
// together with Mixing Class Hierarchies like e.g. with Group and GroupM merged into Ring,
// where a Reference to 'this' is handed back to AGroupM
// to be used in all abstract Implementations,

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** The actual Implementor	 */
	protected IInterface IImplementor;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor
	  * using 'this' Object as the actual Implementor of this Interface.
	  */
	public DAbstraction() {
		this.IImplementor = this; }

	/** Constructor taking the actual Implementor of this Interface.
	  * @param the actual Implementor of this Interface. 	 */
	public DAbstraction(IInterface _Implementor) {
		this.IImplementor = _Implementor; }

////////////////////////////////////////////////////////////////////////////////
//  Interface IInterface: abstract Methods
//  of course it is not necessary to list the abstract Methods
//  because they are already defined in IInterface
//  and can be determined by trying to compile concrete Classes,
//  but listing them here again makes it clearer
//  and it is very probable that even some simple ('primitive') Operations
//  are already implemented, e.g. because they are the same in both Interfaces.
////////////////////////////////////////////////////////////////////////////////

	/** basic simple ('primitive') Operation for later Overloading
	  * Complex Operations in Interface delegate to it,
	  * allowing for Compile Time Variation of Behavior.
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	public abstract Object simpleOp(Object param);

////////////////////////////////////////////////////////////////////////////////
//  Interface  Interface: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** complex Operation,
	  * usually relying on the simple Operations defined in IInterface.
	  *
	  * This is THE Mechanism to separate out the stable Parts of an Algorithm
	  * from the variable Parts, which comes, e.g. with different concrete Classes.
	  * The Strategy Pattern varies the Algorithm on the same concrete Classes.
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	public Object complexOp(Object param) {
		//of course you can do much more here!!!
		return IImplementor.simpleOp(param); }

}
