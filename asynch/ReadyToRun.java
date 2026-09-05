package asynch;

import knowledge.IReadyFlag;

/**
  * Title: ReadyToRun<p>
  * Description:
  * Defines the Interface for a Runnable Object,
  * that checks a Condition before being run().
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 07:34 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Runnable Task Wrapper]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface ReadyToRun
extends Runnable, IReadyFlag {

}

