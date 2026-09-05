package flow.push;

/**
  * Title: DevNull<p>
  * Description:
  * A no-op Sink that discards every Item pushed into it.
  * It is a Singleton, because it can be reused anywhere.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	09-11-2002, 11:41 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:23:57Z
  * digest: d6e4d7159d6c01ddfd502528ad293e5945f734a5e51d20ddc6866c2a65d84c38
  * stale: false
  * tags: [code/null_object]
  * concepts: [Dataflow, Pipeline]
  * facets: {layer: domain, status: stable, complexity: low}
  * -->
  */
final public class DevNull
implements IPushStage {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants
////////////////////////////////////////////////////////////////////////////////

	/** Shared singleton instance of this discard-everything Sink.	 */
	final static public DevNull DEV_NULL = new DevNull();

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods
////////////////////////////////////////////////////////////////////////////////

	/** adds an Item to this Stage 	 */
	public IPushStage putA(Object item) { return this; }

}

