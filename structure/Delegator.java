package structure; //

/**
  * Bridges the minimum {@link IInterface} while also inheriting {@link Abstraction}'s
  * {@link Interface#complexOp} implementation, at the cost of a non-final delegation.
  *
  * The Delegator is an intermediate Class between Abstraction, Bridge and FullBridge, used to...
  * * alleviate the Effort to have to implement the full Interface of 'Interface'
  *   when being delegated to.
  * * increase Consistency of Implementations by inheriting from Abstraction.
  *
  * Thus a full Implementation of 'Interface' is composed of
  * * an Object implementing IInterface' and
  * * the Default Implementation of 'Interface' in Abstraction,
  *   which can also still be overridden if necessary / useful!
  *
  * The Choice between inheriting the Implementor Variable from Bridge
  * or the abstract Implementation from Abstraction only seems to be hard!
  * Generally Implementations involve MUCH MORE CODE than Variable Declarations
  * and thus inheriting from Abstraction is used!
  * On the other Hand, by not inheriting from Bridge this Delegator cannot be used
  * to replace Instances of 'Bridge' and there is no Performance Improvement
  * of the Delegation.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	03-16-2002, 08:48 PM<p>
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
  * mtime: 2026-09-05T11:14:30Z
  * digest: abfe0fd009b68b099341f71316a3e34ccf10dae1673f2d277ec0b4355a812825
  * stale: false
  * tags: [code/delegate_pattern, code/bridge]
  * concepts: [Delegator]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
public class Delegator
extends Abstraction //Bridge //see above!
implements Interface {

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
	public Delegator(IInterface _Implementor) {
		this.Implementor = _Implementor; }

////////////////////////////////////////////////////////////////////////////////
//  Interface 'IInterface': final Implementation of ALL Methods by Delegation!!
//  This is the Nature of a Bridge!
////////////////////////////////////////////////////////////////////////////////

	/** basic simple ('primitive') Operation for later Overloading
	  * Complex Operations in Interface delegate to it,
	  * allowing for Compile Time Variation of Behavior.
	  *
	  * NO Performance Improvement can be realized by making the Implementation final,
	  * because the Use of this Delegator is usually hidden due to it's Proxy Function.
	  * Thus the Implementation is not made final!
	  * @param The Parameter can be anything
	  * @return the Result, can also be anything	 */
	public Object simpleOp(Object param) {
		return Implementor.simpleOp(param); }

}
