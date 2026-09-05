package knowledge;

/**
  * Adds a setter to {@link IReadyFlag}, so the dirty state becomes something a caller
  * asserts rather than something the object concludes.
  *
  * <p>An implementor is then trusting its callers: nothing here re-derives the flag, so a
  * caller that mutates the object and forgets to set it leaves a dirty object claiming to
  * be ready.
  *
  * Title: IDirtyFlag.java<p>
  * Description:
  * Defines the Interface for an Object with two States: one dirty, one ready
  * The State can be set externally.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-01-2002, 12:08 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * @see IReadyFlag the read-only half this extends
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T08:08:12Z
  * digest: 07a767715e8234a811703fa44c6fe1783af77296bfe14dbca74aa7a9a78d7e05
  * stale: false
  * -->
  */
public interface IDirtyFlag
extends IReadyFlag {

////////////////////////////////////////////////////////////////////////////////
/// #region : Accessor Methods (getXXX/isXXX/setXXX)
////////////////////////////////////////////////////////////////////////////////

	/** (re-)sets the Dirty Flag 	 */
	void setDirty(boolean dirty);

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

}

