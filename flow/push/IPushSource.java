package flow.push;

/**
  * Title: IPushSource<p>
  * Description:
  * Defines the Interface for a Source of Objects.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 09:51 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IPushSource {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Produces a new Item and adds it to the concatenated Stage 	 */
	public IPushSource produce();

}

