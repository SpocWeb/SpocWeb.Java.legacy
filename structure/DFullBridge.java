package structure; //

/**
  * Title: DFullBridge<p>
  * Description:
  * Defines a Bridge for the full 'Interface'.
  * The Bridge Pattern allows to separate Interface and Implementation
  * for both Inheritance Hierarchies AND at Runtime.
  * This poses a larger Effort on Classes that cannot inherit from 'Abstraction',
  * (usually because they have to inherit from a different Class),
  * because they have to implement the full 'Interface'.
  * To alleviate this,
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-16-2002, 08:30 PM<p>
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
  */
public class DFullBridge
extends DBridge
implements Interface {

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/**
	 * either reuse the Parent Reference and cast before each Call
	 * or define a new Reference with the SubType
	 * and thus improve Runtime Performance by sacrificing Memory.
	 * This can and should be decided later (only after Profiling)!
	 */
	protected Interface Implementor;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor taking the actual Implementor of this Interface.
	  * @param the actual Implementor of this Interface. 	 */
	public DFullBridge(Interface _Implementor) {
		super (_Implementor);
		this.Implementor = _Implementor;
	}

////////////////////////////////////////////////////////////////////////////////
//  Interface 'Interface': final Implementation of ALL Methods by Delegation!!
//  This is the Nature of a Bridge!
////////////////////////////////////////////////////////////////////////////////

	/** complex Operation,
	  * usually relying on the simple Operations defined in IInterface.
	  *
	  * This is THE Mechanism to separate out the stable Parts of an Algorithm
	  * from the variable Parts, which comes, e.g. with different concrete Classes.
	  *
	  * Here EVERY Operation is just delegated to the Implementor.
	  *
	  * By making the Implementation final
	  * and later referring to the Bridge Class explicitly,
	  * any Comiler, JIT Compiler or Runtime can inline this Delegation
	  * and thus Delegation can take place without Performance Penalty!
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	final public Object complexOp(Object param) {
//		return ((Interface) IImplementor).complexOp(param);
		return               Implementor .complexOp(param);
	}


}
