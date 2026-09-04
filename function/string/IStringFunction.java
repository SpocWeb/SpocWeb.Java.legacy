/*
 * Created on 02.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package function.string;

import function.IFunction;

/**
  * Title: IStringFunction<p>
  * Description:
  * Defines the Interface for an Object mapping Strings to Strings. 
  * This is used as an Alternative to IFunction which maps Objects to Objects. 
  * 
  * Any stateless String Function can be defined as IStringFunction 
  * 
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Similar Interfaces and Classes: 
  * @see function.IFunction
  * 
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-21-2002, 04:51 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
 */
public interface IStringFunction 
extends IFunction {
	/**
	 * type-safe Refinement of the generic Mapping Function
	 * @see IFunction#Map(Object)
	 * @param arg the String to process
	 * @return a new String constructed from the given one. 
	 */
	public String Map(final String arg); 
	
}
