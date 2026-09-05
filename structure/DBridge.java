package structure; //

/**
  * Bridges the minimum {@link IInterface} like {@link Bridge}, but inherits from
  * {@link DAbstraction} instead of holding the Implementor Reference itself.
  *
  * The Bridge Pattern allows to separate Interface and Implementation
  * for both Inheritance Hierarchies AND at Runtime.
  * It delegates ALL Operations of an Interface to an Implementor
  * thus allowing to switch it at Runtime
  * and to mix in Instances of any Hierarchy.
  *
  * This poses only little Effort on Classes that cannot inherit from 'Abstraction',
  * (usually because they have to inherit from a different Class)
  * because they have to implement only the smaller 'IInterface'.
  * The Drawback is that the DBridge cannot be used as a full blown 'Interface'
  * AND Optimizations of the .
  * To alleviate this,
  *
  *
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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T11:13:48Z
  * digest: e28bc098f2d6d1d15d7d66b33b0659b9ad4f485d85b21abb8478289c068fe0a7
  * stale: false
  * tags: [code/bridge]
  * concepts: [Delegate-Based Bridge Implementor]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class DBridge
extends DAbstraction
implements IInterface //unnecessary to mention
{

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Constructor taking the actual Implementor of this Interface.
	  * @param the actual Implementor of this Interface. 	 */
	public DBridge(IInterface _Implementor) {
		super(_Implementor); }

////////////////////////////////////////////////////////////////////////////////
//  Interface 'IInterface': final Implementation of ALL Methods by Delegation!!
//  This is the Nature of a DBridge!
////////////////////////////////////////////////////////////////////////////////

	/** basic simple ('primitive') Operation for later Overloading
	  * Complex Operations in Interface delegate to it,
	  * allowing for Compile Time Variation of Behavior.
	  *
	  * By making the Implementation final
	  * and later referring to the DBridge Class explicitly,
	  * any Comiler, JIT Compiler or Runtime can inline this Delegation
	  * and thus Delegation can take place without Performance Penalty!
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	final public Object simpleOp(Object param) {
		return IImplementor.simpleOp(param); }

}
