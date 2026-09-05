/*
 * File Name: IConfigFilterOut.java
 * Created on: 17.06.2003
 *
 */
package streamIO;

/**
 * Title: IConfigFilterOut<p>
 * Description:
 * Purpose:
 * A Filter whose Output Stream can be replaced during Runtime. 
 * This is necessary for a pluggable Architecture. 
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
 * mtime: 2026-09-05T10:13:24Z
 * digest: 79d4ccd4a8369fea6e0e9c0c44755922aee80ba72e15337a49d881a465c1578e
 * stale: false
 * tags: [code/output_stream]
 * concepts: [Pluggable Filter Output Interface]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public interface IPlugAbleFilterOut 
extends IIStreamOut {
	
	/**
	 * seta the Output streamIO to filter
	 * @param stream the new Output Sream
	 */
	public void setStreamOut(final IIStreamOut stream);

}
