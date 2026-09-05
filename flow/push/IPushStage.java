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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: 0e3992512dbd3c024871ca2ece24c419acf7fcc0865c71a181d9f01f58d2c83b
  * stale: false
  * tags: [code/producer_consumer]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public interface IPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds an Item to this Stage 	 */
	public IPushStage putA(Object item);

}

