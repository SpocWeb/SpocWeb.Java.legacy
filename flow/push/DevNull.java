package flow.push;

/**
  * Title: DevNull<p>
  * Description:
  * Defines the Interface for the Null Device.
  * It is a Singleton, because it can be reused anywhere.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 11:41 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class DevNull
implements IPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants
////////////////////////////////////////////////////////////////////////////////

	/** TODO: 	 */
	final static public DevNull DEV_NULL = new DevNull();

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds an Item to this Stage 	 */
	public IPushStage putA(Object item) { return this; }

}

