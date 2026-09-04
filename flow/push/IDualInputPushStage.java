package flow.push;

/**
  * Title: IDualPushStage<p>
  * Description:
  * Defines the Interface for a Processing Stage with two Inputs.
  * To differentiate the Input you can either use a Parameter
  * (but that changes the Interface)
  * or two different Methods: putA and putB which makes it more expressive.
  * The Dual Stage can be extended to an n Stage by hierarchical Concatenation.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 09:58 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IDualInputPushStage
extends IPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds a second Item to this Stage 	 */
	public IDualInputPushStage putB(Object item);

}

