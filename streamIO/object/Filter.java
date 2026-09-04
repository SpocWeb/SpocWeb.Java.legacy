/*
 * Created on 31.08.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.object;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;

/**
 * @author heuerm
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Filter 
extends AFilter {

	/**
	 * @param enum_
	 */
	public Filter(final IIStreamIn enum_) { super(enum_); }
	
	/**
	 * @param out_
	 */
	public Filter(final IIStreamOut out_) { super(out_); }
	
	public Object nextItemInternal() { return in.nextItem(); }
	
	/**
	 * TODO: use a Mapper to control the Filter just like with float Streams! 
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(final Object arg) {
		out.addItem(arg); 
		return this; }
	
}
