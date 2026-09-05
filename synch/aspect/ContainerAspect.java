package synch.aspect;

import synch.InvalidException;

/**  ContainerAspect
 * A ContainerAspect contains Fields of other Aspect Types
 * and knows their Names by Reflection!
 * Since the Container knows its Name,
 * it can detect whether a given Name,Value Pair is relevant!
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: f6e02744f2eaf091e2740752c3061a073e8d6c4abd07d5ec9824049450320610
 * stale: false
 * tags: [code/attached_property]
 * concepts: [Composite Value Object Container]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
 */
public class ContainerAspect
extends Aspect {

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public ContainerAspect(String Name, Aspect Parent) {
		super(Name, Parent); }

////////////////////////////////////////////////////////////////////////////
/// #region : Interface ICPair, IPair: abstract Methods
////////////////////////////////////////////////////////////////////////////

	/** Accessor Method
	  * @param sets Value of this Aspect as an Object */
	public void setValue(Object val) { copyAt(val); }

	/** Accessor Method
	  * For composite Objects there is no Default Value Property!
	  * @return the Value of this Aspect as an Object */
	public Object getVal() { return this; }

	/**This Method is responsible for copying the given Value
	 * into the local Value of this Property.
	 * This is used e.g. on receiving an Update from a Publisher.
	 * All the Rest of the Publication Mechanism is handled automatically!
	 */
	protected void copyFieldsAt(Object Value) { }

	/** Local Validation Routine to validate multifield Checks */
	protected void myValidate(Object Source, Object Value, Object oldVal)
		throws InvalidException { }

}
