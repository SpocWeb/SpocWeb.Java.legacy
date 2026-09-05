/**
 * File  Name: IAdjustAble.java
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
 * digest: 2fe9fa190260535dab752dbf8e20f5b4d780ab4a4e51361e73c056e10bb7ae05
 * stale: false
 * tags: [code/function_wrapper, code/mathematical_constants]
 * concepts: [By-Reference Primitive Wrapper]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public interface IAdjustAble extends ICategorizeAble {
	
	/** adjusts this Object to the given Value 
	 *  @param val the Value to adjust this Class to
	 */
	public void setDouble(double val);
	
	/** adjusts this Object to the given Value 
	 *  @param val the Value to adjust this Class to
	 */
	public void setFloat(float val);

}

