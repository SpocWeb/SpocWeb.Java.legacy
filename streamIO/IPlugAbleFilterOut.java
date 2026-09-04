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
 */
public interface IPlugAbleFilterOut 
extends IIStreamOut {
	
	/**
	 * seta the Output streamIO to filter
	 * @param stream the new Output Sream
	 */
	public void setStreamOut(final IIStreamOut stream);

}
