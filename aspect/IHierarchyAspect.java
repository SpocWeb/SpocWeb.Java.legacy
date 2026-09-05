package aspect;

//import Stream.Copy.ICopyAble;
import synch.ISubscriber;
import synch.IValidator;
import synch.InvalidException;

/**
  * Title: IHierarchyAspect<p>
  * Description:
  * Defines the Interface for an Aspect Hierarchy
  * * it can have 'Parent' Aspects and inherits the Prefix from them
  * * validate and propagate Changes   upward to the Parents  (Multi Field Plausis).
  * * validate and propagate Changes downward to the Children (One   Field Plausis).
  *
  * The Reason for having different Methods for upward and downward Communication
  * is to suppress Event Chaining, i.e. Events triggering Events etc.
  *
  * Regular Checking and Publication is added only in SubClasses!
  *
  * All interface Operations are implicitly public and abstract.
  * All interface Attributes are implicitly public, final and static.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	07-22-2002, 07:30 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:24:25Z
  * digest: c11a7ddf63f14ece97525b7fdc1528b834f52cc28f98c41ee97008d1da7dc96b
  * stale: false
  * tags: [code/domain_model, code/hierarchy]
  * concepts: [Aspect Framework]
  * facets: {layer: domain, status: stable, complexity: medium}
  * -->
  */
public interface IHierarchyAspect
extends IAspect, IValidator, ISubscriber
{

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** Returns this Aspect's Parent, if any.
	  * @return the Parent Aspect this Aspect is nested under, or null if this Aspect is a root  */
	public IHierarchyAspect getParent();

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Callback used to update all Subscribers
	 * @param Source the Object whose Value is changed
	 * @param Value  the new Value
	 * @param oldVal the old Value, optional can be null
	 */
	public void updateParent(Object Source, Object Value, Object oldVal);

	/** Local Validation Routine to validate multifield Checks */
	public void validateParent(Object Source, Object Value, Object oldVal)
		throws InvalidException;

	/** recursively Bulk update all Subscribers on the current new Values	 */
	public void update();

	/** recursively Bulk validate all Validators on the current new Values	 */
	public void validate() throws InvalidException;

}

