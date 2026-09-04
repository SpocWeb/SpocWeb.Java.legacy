package structure; //

/**
  * Title: Bridge<p>
  * Description:
  * The Bridge Pattern allows to separate Interface and Implementation
  * for both Inheritance Hierarchies AND at Runtime.
  * It delegates ALL Operations of an Interface to an Implementor
  * thus allowing to switch it at Runtime
  * and to mix in Instances of any Hierarchy.
  *
  * A Bridge Object is unfortunately necessary to realize the full Functionality
  * but fortunately the Creation of the Constellation Bridge->Delegate
  * is well apted for the Prototype Pattern.
  *
  * This poses only little Effort on Classes that cannot inherit from 'Abstraction',
  * (usually because they have to inherit from a different Class)
  * because they have to implement only the smaller 'IInterface'.
  * The Drawback is that the Bridge cannot be used as a full blown 'Interface'
  * AND Optimizations of the .
  * To alleviate this,
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-16-2002, 08:21 PM<p>
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
public class Bridge
//extends other Bridges to implement the full Interface.
implements IInterface {
//doesn't need to implement the Interface it delegates to!
//otherwise it becomes a Decorator.

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** The actual Implementor	 */
	protected IInterface Implementor;

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor taking the actual Implementor of this Interface.
	  * @param the actual Implementor of this Interface. 	 */
	public Bridge(IInterface _Implementor) {
		this.Implementor = _Implementor; }

////////////////////////////////////////////////////////////////////////////////
//  Interface 'IInterface': final Implementation of ALL Methods by Delegation!!
//  This is the Nature of a Bridge!
////////////////////////////////////////////////////////////////////////////////

	/** basic simple ('primitive') Operation for later Overloading
	  * Complex Operations in Interface delegate to it,
	  * allowing for Compile Time Variation of Behavior.
	  *
	  * By making the Implementation final
	  * and later referring to the Bridge Class explicitly,
	  * any Comiler, JIT Compiler or Runtime can inline this Delegation
	  * and thus Delegation can take place without Performance Penalty!
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	final public Object simpleOp(Object param) {
		return Implementor.simpleOp(param); }

}
