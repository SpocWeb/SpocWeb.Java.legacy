/*
 * File Name: IConfigFilter.java
 * Created on: 17.06.2003
 *
 */
package streamIO;

/**
 * Title: IConfigFilter<p>
 * Description:
 * A Filter whose Input AND Output Streams can both be replaced during Runtime,
 * combining IPlugAbleFilterIn and IPlugAbleFilterOut into a single reconfigurable Filter.
 * This is necessary for a pluggable Architecture, e.g. to rewire a Filter into a different
 * position of a Filter Chain without recreating it.
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
 * mtime: 2026-09-05T10:41:27Z
 * digest: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 * stale: false
 * tags: [code/output_stream]
 * concepts: [Pluggable Filter Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IPlugAbleFilter 
extends IPlugAbleFilterOut, IPlugAbleFilterIn {

}
