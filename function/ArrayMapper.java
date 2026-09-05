package function;

import streamIO.Assert;

/**
 * private Helper Class for a simulated Mapping based on an Array 
 * Quite inefficient, but good enough for Arrays < 20 Elements.
 *  
 * Erstellt am 26.01.2004, 15:06:33 von mheuer
 * <hr>
 * @version $Revision: 1.2 $
 * @author  $Author: mheuer $ <pre>
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:34:46Z
 * digest: a2e31c80c82ec87f1c9b9fb6f3bda881322c7127db465623779bdcbc0cbfb2c0
 * stale: false
 * tags: [code/function_contract, code/function_composition]
 * concepts: [Function/Relation Contract]
 * facets: {layer: utility, status: legacy, complexity: low}
 * -->
 */
public class ArrayMapper 
extends AInvertAble
implements IInvertAble {

	/** Looks up {@code arg} in {@code mappings}' key column and returns the matching value-column entry.
	 * @return the EKP-Number for the given RiseID
	 */
	final static public Object MAP(final Object[][] mappings, final Object arg
	, final int keyCol, final int valCol) {
		for (int i = mappings.length; --i >= 0;) {
			final Object[] assoc = mappings[i]; 
			if ((assoc[keyCol] == arg) || ((assoc[keyCol] != null) && assoc[keyCol].equals(arg))) {
				return assoc[valCol]; }
		}
		return null;
	}; 
	
	/////////////////////////////////////////////////////////////////////////////////////

	/** The Array used for actually Mapping	 */
	final String[][] mappings; 

	final int keyCol; 

	final int valCol; 

	/** Creates a mapper over {@code mappings_}, keyed by {@code keyCol_} and valued by {@code valCol_}. */
	public ArrayMapper (final String[][] mappings_, final int keyCol_, final int valCol_) {
		this.mappings = mappings_;  Assert.NOT_NULL(mappings);
		this.keyCol = keyCol_; Assert.IS_TRUE(keyCol >= 0);
		this.valCol = valCol_; Assert.IS_TRUE(valCol >= 0); 
	}

	/** Looks {@code arg} up in the key column and returns the corresponding value-column entry.
	 * @return the Value Column for the given Key Column
	 */
	public Object Map(final Object arg) {
		return MAP(mappings, arg, keyCol, valCol); }


	/** Looks {@code arg} up in the value column and returns the corresponding key-column entry.
	 * @return the Key Column for the given Value Column
	 */
	public Object UnMap(final Object arg) {
		return MAP(mappings, arg, valCol, keyCol); }

}
