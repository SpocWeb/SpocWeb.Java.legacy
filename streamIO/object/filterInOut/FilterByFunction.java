package streamIO.object.filterInOut;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.object.AFilter;
import function.FunctionByHash;
import function.IFunction;

/**
  * Projective filter that maps every item through a configured {@link IFunction} on its way
  * through, on either the input or output side.
  * <p>
  * Title: FilterByFunction.java<p>
  * Description:
  * Maps the Items in the Input streamIO by the given Function and hands them over.
  * Prototype of a Projective Filter. 
  * @see streamIO.object.filterInOut.FilterByTester for a Filter to remove Items. 
  * Filters to add Items like FilterSeparator are not frequent enough to be generalized. 
  *
  * Design Decisions / Implementation Details:
  * Made it final, because that speeds up Operation
  * when declared explicitly and not polymorphic.
  *
  * Known SubClasses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 06;44;48<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T20:48:04Z
  * digest: 78197b9ec72369bd43f1b6463e9feae0f8feb155d6d62d18acf4a487079d8b92
  * stale: false
  * tags: [code/stream_filter, code/decorator_pattern]
  * concepts: [Stream Filter (Input)]
  * facets: {layer: utility, status: legacy, complexity: medium}
  * -->
  */
public class FilterByFunction
extends AFilter {

	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////

	/** performs the actual Mapping in this Filter	 */
	protected final IFunction mapper;

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor	 */
	public FilterByFunction(final IIStreamIn enm, final IFunction mapper_) {
		super(enm); 
		this.mapper = mapper_; }

	/**
	 * Constructor for FilterByFunction.
	 * @param out_
	 */
	public FilterByFunction(final IIStreamOut out_, final IFunction mapper_) {
		super(out_);
		this.mapper = mapper_; }

	/**
	 * Creates a filter that maps items via a hash built from the given key/value column pairs.
	 *
	 * @return a FilterByFunction that maps the key Fields to the Value Fields */
	public FilterByFunction (final IIStreamIn Enum, final Object[][] KeyValPairs, final int keyIndex, final int valIndex) {
		this(Enum, new FunctionByHash(KeyValPairs, keyIndex, valIndex)); }

	/**
	 * Creates a filter that maps items via a hash built from column 0 (key) and column 1
	 * (value) of the given rows.
	 *
	 * @return a FilterByFunction that maps the key Fields to the Value Fields */
	public FilterByFunction (final IIStreamIn Enum, final Object[][] KeyValPairs) {
		this(Enum, new FunctionByHash(KeyValPairs, 0, 1)); }

	/**
	 * Creates a filter that maps items via a hash built from parallel key and value arrays.
	 *
	 * @return a FilterByFunction that maps the key Fields to the Value Fields */
	public FilterByFunction (final IIStreamIn Enum, final Object[] keys, final Object[] values) {
		this(Enum, new FunctionByHash(keys, values)); }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamOut:
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Maps {@code arg} through this filter's function, when one is set, before adding it to
	 * the wrapped output.
	 *
	 * @see streamIO.IIStreamOut#addItem(Object)
	 */
	public IIStreamOut addItem(Object arg) {
		if (mapper != null) 
			arg = mapper.Map(arg); 
		out.addItem(arg); 
		return this; }

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn:
	////////////////////////////////////////////////////////////////////////////////

	/**Returns the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		if (mapper == null)
			return in.nextItem();
		if (EOI ==(currItem = in.nextItem())) 
			return currItem; 
			return mapper.Map(currItem); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + FilterByFunction.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}
