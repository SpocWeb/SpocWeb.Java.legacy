/*
 * File Name: IConfigFilter.java
 * Created on: 17.06.2003
 *
 */
package streamIO.integer.filter;

/**
 * Title: IConfigFilter<p>
 * Description:
 * Purpose:
 * A Filter whose Input and Output Streams can be replaced during Runtime. 
 * This is necessary for a pluggable Architecture. 
 *
 * Design Decisions / Implementation Details:
 * If similar Classes exist (e.g. Polymorphism),
 * characterize the specific Differences to compare these.
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
public interface IPlugAbleFilterByte 
extends IPlugAbleFilterOutByte, IPlugAbleFilterIn_Byte {

}
