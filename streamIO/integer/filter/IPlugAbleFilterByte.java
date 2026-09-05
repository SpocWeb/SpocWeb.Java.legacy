/*
 * File Name: IConfigFilter.java
 * Created on: 17.06.2003
 *
 */
package streamIO.integer.filter;

/**
 * A Filter whose Input and Output Streams can be replaced during Runtime.
 * This is necessary for a pluggable Architecture. 
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
 * mtime: 2026-09-05T21:42:26Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Pluggable Byte-Stream Filter Infrastructure and java.io Adapters]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 */
public interface IPlugAbleFilterByte 
extends IPlugAbleFilterOutByte, IPlugAbleFilterIn_Byte {

}
