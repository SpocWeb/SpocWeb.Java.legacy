package asynch;

import graphs.IValue;

import java.lang.reflect.InvocationTargetException;

import knowledge.IReadyFlag;


/**
  * Title: IFuture<p>
  * Description:
  * Defines the Interface for a Future,
  * i.e. a Value Object shared between Threads to return Results of Computations.
  * It's IValueSetter Method can also be used as a Callback into the original Client
  * to directly perform Work there when the Result is finished!
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: Future
  *
  * Known Uses: <none>
  * 
  * similar Interfaces: IAsyncResult in .NET 
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	08-31-2002, 06:53 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: 240f869d49d83f68f5304aa6d9da05de3a5db8fde67af2794007a688d8001406
  * stale: false
  * tags: [code/deferred_execution]
  * concepts: [Future Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IFuture
extends IValue, IReadyFlag {
	
	/** synchronous Reader Method with Timeout (additionally to the infinite Timeout Getter Method)
	  * Actually multiple Readers could read in parallel Threads
	  * as long as they block the Writer Method and force a Memory Barrier.
	  * @return the Result Value of the Call
	  */
	public Object getVal(final long _TimeOut) throws InterruptedException, InvocationTargetException;
	
	/** propagates an Exception that happened at the Server during Execution: 	 */
	public void setException(final Throwable x);
	
}

