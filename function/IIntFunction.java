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
 */
public interface IIntFunction {
	
	/**
	 * @param value the Value to map. 
	 * @return a Mapping from long to long. 
	 */
	public long Map(final long value);
	
	/**
	 * @param value the Value to map. 
	 * @return a Mapping from int to int. 
	 */
	public int Map(final int value);

}
