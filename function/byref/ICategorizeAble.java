/**
 * File  Name: ICategorizeAble.java
 * Created on: 27.10.2002
 */
package function.byref;

/**
 * Title: enclosing_type<p>
 * Description:
 * Purpose:
 * Defines an Interface for Classes 
 * whose Value can be set on a continuous Scale
 *
 * Design Decisions / Implementation Details:
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
 * mtime: 2026-09-05T10:12:24Z
 * digest: f3890bdfee3a0cff5ef74da268fae1dcbce5fdc7e04b81f1fbfbea56d244ecbb
 * stale: false
 * tags: [code/function_wrapper, code/mathematical_constants]
 * concepts: [By-Reference Primitive Wrapper]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface ICategorizeAble {
	
	/** gives this Object the given Category 
	 *  @param val the Category to set this Object to
	 */
	public void setByte (byte  val);

	/** gives this Object the given Category 
	 *  @param val the Category to set this Object to
	 */
	public void setShort(short val);

	/** gives this Object the given Category 
	 *  @param val the Category to set this Object to
	 */
	public void setInt  (int   val);

	/** gives this Object the given Category 
	 *  @param val the Category to set this Object to
	 */
	public void setLong (long  val);

}
