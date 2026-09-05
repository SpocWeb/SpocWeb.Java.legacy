/**
 * File  Name: FilterSeparator.java
 * Created on: 26.12.2002
 */
package streamIO.object.filterInOut;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.object.AFilter;

/**
 * Filter that inserts a configured separator object between the items passing through.
 * <p>
 * Title: FilterSeparator<p>
 * Description:
 *
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
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T20:48:43Z
 * digest: 5a522e76626beff77ff99e5905119ab9297f1b353aa99b9e0a4a583e0e9cc38f
 * stale: false
 * tags: [code/stream_filter, code/decorator_pattern]
 * concepts: [Stream Filter (Input)]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
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
	 * Adds {@code arg} to the wrapped output, followed by the configured separator.
	 *
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(Object arg) {
		out.addItem(arg);
		out.addItem(separator);
		return this; }

	/**
	 * Alternates between the separator and the wrapped input's own next item.
	 *
	 * @see streamIO.IFactory#nextItem()
	 */
	protected Object nextItemInternal() {
		if (currItem != separator)
			return separator; //rely on the Fact that the Separator does not appear in the Stream
		return in.nextItem(); }

	/**
	 * Unused entry point.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) {}
	
}
