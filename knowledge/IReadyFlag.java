package knowledge;

/**
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
  */
public interface IReadyFlag {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return true, when this Object has been modified / is not ready, false otherwise */
	public boolean isDirty();
	
}

