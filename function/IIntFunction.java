/*
 * File Name: ILongFunction.java
 * Created on: 18.04.2003
 */
package function;

/**
 * Title: IIntFunction<p>
 * Description:
 * Purpose:
 * Defines the Interface for a Mapping of Integers to Integers. 
 *
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
 * mtime: 2026-09-05T16:39:28Z
 * digest: 3732b6b17b89b0487a3497b4657f54ae191c842a4b39449bcdbd5af8694458e6
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IIntFunction {
	
	/** Maps a {@code long} value to another {@code long} value.
	 * @param value the Value to map.
	 * @return a Mapping from long to long.
	 */
	public long Map(final long value);

	/** Maps an {@code int} value to another {@code int} value.
	 * @param value the Value to map.
	 * @return a Mapping from int to int.
	 */
	public int Map(final int value);

}
