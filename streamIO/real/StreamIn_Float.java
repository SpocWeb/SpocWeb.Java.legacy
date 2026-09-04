/*
 * File Name: StreamIn_Float.java
 * Created on: 10.02.2004
 *
 */
package streamIO.real;

import streamIO.IMarkAble;
import streamIO.IOrdered;
import streamIO.object.IStreamIn;

/**
 * Title: StreamIn_Float<p>
 * Description:
 * Interface describing resettable Streams of Float Numbers 
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
public interface StreamIn_Float 
extends IStreamIn, IStreamIn_Float, IMarkAble, IOrdered {
	
}
