/*
 * File Name: StreamIn_Float.java
 * Created on: 10.02.2004
 *
 */
package streamIO.real;

import streamIO.IMarkAble;
import streamIO.IOrdered;
import streamIO.object.IStreamIn;

/**
 * Combines {@link IStreamIn_Float} with markability, ordering and JDK-style {@code IStreamIn}
 * semantics into one resettable float-stream contract.
 *
 * <p>Design Decisions / Implementation Details:
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Float Stream Base Class]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface StreamIn_Float 
extends IStreamIn, IStreamIn_Float, IMarkAble, IOrdered {
	
}
