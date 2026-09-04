package streamIO.object.filterInOut;

import java.io.FileNotFoundException;
import java.io.IOException;

import streamIO.IIStreamIn;
import streamIO.IIStreamOut;
import streamIO.IPlugAbleFilterIn;
import streamIO.object.APlugAbleFilter;
import tester.EquivalenceIdentity;
import tester.ITester;
import tester.TesterEquivalence;
import tester.stateful.TestSequence;

/**
  * Title: FilterByTester.java<p>

  * Description:
  * Filters Objects by handing them over to a ITester Function. 
  * If the ITester returns false, the Item is returned, 
  * otherwise Items are retrieved until one does not fulfill the Test. 
  *
  * This is used to e.g. filter Rows from a Container
  * To filter Columns from a Row in a Container use
  * @see streamIO.object.filterIn.FilterInByBoolean
  * @see streamIO.object.filterIn.FilterInByBitMask
  *
  * Design Decisions / Implementation Details:
  * Made it final, because that speeds up Operation
  * when declared explicitly and not polymorphic.
  *
  * Known SubClasses:
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-06-03, 06;44;48<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
final public class FilterByTester
extends APlugAbleFilter {
    
	///////////////////////////////////////////////////////////////////////////
	
	/** 
	 * 
	 * @param inFile
	 * @param outFile
	 * @param sep The String of Separators starting with the Escape Character
	 */
	public static long FILTER_DUPLICATES(final String in_File, final String sep, final String outFile) 
	throws FileNotFoundException, IOException {
		return FILTER(in_File, sep, 
		        (IPlugAbleFilterIn) new FilterByTester(new TestSequence()), outFile); 
	}
	
	/** Since it is usually not interesting to return only Items equivalent to another Item, 
	 * only Filtering on non Equivalence is supported here. 
	 * 
	 * This can be used to filter out Separator Objects 
	 * that were e.g. introduced by @see FilterSeparator. 
	 */
	final static public FilterByTester FILTER_IDENTITY(
	        final IIStreamIn enm, final Object itemToFilter, final boolean equivalence) {
		return 
			new FilterByTester(enm, 
			new TesterEquivalence(itemToFilter, 
			equivalence ? 
			EquivalenceIdentity.Equality : 
			EquivalenceIdentity.Identity)); }
		
	////////////////////////////////////////////////////////////////////////////////
	//  Variables
	////////////////////////////////////////////////////////////////////////////////
	
	/** Reference to the ITester of this Filter	 */
	protected ITester tester;
	
	/** Determines whether Nulls will be filled in, when the Test fails	 */
	protected boolean mFillNulls;
	
	////////////////////////////////////////////////////////////////////////////////
	//  Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////
	
	/** Initializing Constructor	 */
	public FilterByTester(final IIStreamIn _enum, final ITester _tester, final boolean fillNulls) {
		super(_enum);
		this.tester = _tester;
		this.mFillNulls = fillNulls; }
	
	/** Initializing Constructor	 */
	public FilterByTester(final IIStreamIn _enum, final ITester _tester) {
		this(_enum, _tester, false); }
	
	/** Initializing Constructor.
	  * An empty ITester is assumed to return true always...	 */
	public FilterByTester(final IIStreamIn _enum) {
		this(_enum, null, false); }
	
	/** Initializing Constructor	 */
	public FilterByTester(final IIStreamOut _store, final ITester _tester, final boolean fillNulls) {
		super(_store);
		this.mFillNulls = fillNulls;
		this.tester = _tester; }
	
	/** Initializing Constructor	 */
	public FilterByTester(final IIStreamOut _store, final ITester _tester) {
		this(_store, _tester, false); }
	
	/** Initializing Constructor	 */
	public FilterByTester(final IIStreamOut _store) {
		this(_store, null, false); }
	
	/** Initializing Constructor	 */
	public FilterByTester(final ITester _tester) { 
	    super(); 
		this.tester = _tester; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOut:
	////////////////////////////////////////////////////////////////////////////////

	/**Adds this Item to the Store/streamIO as is.	 */
	public IIStreamOut addItem(final Object arg) {
		if (tester == null)
			out.addItem(arg);
		else if (tester.test(arg))
			out.addItem(arg);
		else if (mFillNulls)
			out.addItem(IIStreamIn.EOI);
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn:
	////////////////////////////////////////////////////////////////////////////////

	/**@return the next (Parent) Object of this one.
	 * No Exception is thrown at the End, instead EOI is returned.
	 * This is less explicit, but much faster for a regular Operation
	 * because Exception Handling can be extremely slow.
	 */
	protected Object nextItemInternal() {
		if (tester == null) {
			return in.nextItem(); }
		do {
			if (!tester.test(currItem = in.nextItem())) {
				return currItem; }
		} while (!mFillNulls && ((currItem != EOI) || in.isValid()));
		return IIStreamIn.EOI; }

	////////////////////////////////////////////////////////////////////////////////
	//  static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** Tests all Methods of this Class	 */
	public static void testIt() { //throws java.io.IOException {
		System.out.println("Testing " + FilterByTester.class.getName());
	}
	
	/** 
	 * 
	 * @param args a List of File Names to read and trim. 
	 */
	public static void main(final String[] args) throws Exception {
		System.out.println("Syntax: inputFile [[outputFile] SeparatorChars]"); 
		if (args.length == 0) {
			testIt(); return; }
		final String in_File = args[0]; 
		final String outFile = (args.length > 1) ? args[1] : args[0]+".noDuplicates"; 
		final String seps    = (args.length > 2) ? args[2] : "\\\n"; 
		FILTER_DUPLICATES(in_File, seps, outFile); 
	}
	
}
