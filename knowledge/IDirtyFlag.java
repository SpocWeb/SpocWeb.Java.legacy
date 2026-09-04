package knowledge;

/**
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

