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

