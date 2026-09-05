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
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: 14f6730f155ea7ef17ee15cd100d2cea599bc19188af17b90d9e7fe45c633771
  * stale: false
  * tags: [code/producer_consumer]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
public interface IPushSource {

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** Produces a new Item and adds it to the concatenated Stage 	 */
	public IPushSource produce();

}

