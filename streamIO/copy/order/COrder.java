package streamIO.copy.order;


/**Implements Constants for all Types of OrderAble Classes.
 * This Class inhibits the Use of ...At() Routines
 * but still supports all other Methods of the OrderAble Class
 * by delegeting to an inner Instance of Order.
 *
 * Design Decisions:
 * All Constant Classes are derived from this one.
 * They cannot inherit the Implementations from the A... Classes,
 * but it is more performant to delegate everything right away!
 * This Class is unnecessary, because the Default Implementation of ACopyAble
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:25Z
 * digest: 6c40af6350593748ac7e2079e4a5f20add36fc1ac1e67ce9aca3d30dda9aa94e
 * stale: false
 * tags: [code/abstract_base, code/delegation, code/immutable_wrapper]
 * concepts: [Order Relation, Constant/Immutable Wrapper]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * already delegates to copyAt() which throws an Error.  	 */
public class COrder
extends AOrder {

	//////////////////////
	//	Constructor		//
	//////////////////////

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Initializing Constructor,
	 *  Creates a DeepCopy of cnst, to be really sure that no Object has a Reference
	 *  @param cnst Object to be represented and copied by Value.
	 *   */
	public COrder(IOrder cnst) { super((IOrder) cnst.copy()); }

}
