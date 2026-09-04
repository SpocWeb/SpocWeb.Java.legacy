package flow.push;

/**
  * Title: IPushStage<p>
  * Description:
  * Defines the Interface for a single Processing Stage
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 09:53 PM<p>
  * @author     Matthias Heuer
  * @version    1.0
  */
public interface IPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds an Item to this Stage 	 */
	public IPushStage putA(Object item);

}

