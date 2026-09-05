package knowledge;

/**
  * Exposes a read-only dirty/ready state that the implementor derives rather than stores.
  *
  * <p>The split from {@link IDirtyFlag} is the whole point: here the state is a conclusion
  * the object draws about itself, so there is deliberately no setter for a caller to
  * contradict it with.
  *
  * Title: IReadyFlag<p>
  * Description:
  * Defines the Interface for an Object with two States: one dirty, one ready.
  * The State cannot be set externally but is calculated.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 07:30 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see IDirtyFlag the sub-interface whose state a caller may set
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T08:08:01Z
  * digest: f4ed74a8c450ddafff2c4eec19c6a985af0886e33aa9ae05b415ca86a8dd1b2e
  * stale: false
  * -->
  */
public interface IReadyFlag {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Reports whether this object has been modified since it was last made ready.
	 *
	 * @return true, when this Object has been modified / is not ready, false otherwise
	 */
	public boolean isDirty();
	
}

