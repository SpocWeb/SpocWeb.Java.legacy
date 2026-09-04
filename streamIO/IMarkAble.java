/*
 * Created on 13.09.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO;

/**
 * Title: <p>
 * Description:
 * Purpose:
 * Marks a Stream as mark()able, 
 * i.e. the Origin for reSet() Operations can be relocated. 
 * Positioning and the getPosition() Result become relative to the last mark()ed Position. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author heuerm
 * @version	1.0
 */
public interface IMarkAble 
extends IReSetAble {
    
	/**
	  * Tests if this input stream supports the mark (and reset) methods.
	  * @see #mark(long)
	  * @see #mark()
	  * @see java.io.InputStream#markSupported() returns false.
	  * @return the maximum allowed readLimit for the mark() Method. 
	  * -1 if this Object does not support the mark and reset methods
	  */
	public long getMaxMarkSize();
	
	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * at the last marked position.
	  * The readlimit arguments tells this input stream to allow that many Items
	  * to be read before the mark position gets invalidated.
	  * This is to limit the Blocking of System Ressources. 
	  * @return this Stream, if mark()ing was successful, null otherwise,
	  * so an Exception can be raised by using the Return Value. 
	  */
    public IMarkAble mark(); 
    
	/** Marks the current position in this Iterator.
	  * A subsequent call to the reset method repositions this Iterator
	  * at the last marked position.
	  * The readlimit arguments tells this input stream to allow that many Items
	  * to be read before the mark position gets invalidated.
	  * This is to limit the Blocking of System Ressources	 
	  * @return this Stream, if mark()ing was successful, null otherwise,
	  * so an Exception can be raised by using the Return Value. 
	  */
    public IMarkAble mark(long readLimit); 
    
}
