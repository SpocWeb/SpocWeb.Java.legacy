/**
 * File  Name: FilterSeparator.java
 * Created on: 26.12.2002
 */
package streamIO.object.filterInOut;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.object.AFilter;

/**
 * Title: FilterSeparator<p>
 * Description:
 * Purpose:
 * InOut-Filter; Inserts a Separator Object between any two Objects.
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
public class FilterSeparator 
extends AFilter {

	/** The Separator Object inserted between any two Objects from the Source streamIO */
	protected Object separator;

	/**
	 * Constructor for FilterSeparator.
	 * @param out_
	 */
	public FilterSeparator(final IIStreamOut out_, final Object _separator) {
		super(out_);
		this.currItem =
		this.separator = _separator;
	}

	/**
	 * Constructor for FilterSeparator.
	 * @param Enum
	 */
	public FilterSeparator(final IIStreamIn Enum, final Object _separator) {
		super(Enum);
		this.currItem =
		this.separator = _separator;
	}

	/**
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(Object arg) {
		out.addItem(arg);
		out.addItem(separator);
		return this; }

	/**
	 * @see streamIO.IFactory#nextItem()
	 */
	protected Object nextItemInternal() {
		if (currItem != separator) 
			return separator; //rely on the Fact that the Separator does not appear in the Stream
		return in.nextItem(); }
	
	public static void main(String[] args) {}
	
}
