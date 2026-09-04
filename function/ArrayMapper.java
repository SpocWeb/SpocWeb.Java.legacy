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
 */
public class ArrayMapper 
extends AInvertAble
implements IInvertAble {

	/** 
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

	public ArrayMapper (final String[][] mappings_, final int keyCol_, final int valCol_) {
		this.mappings = mappings_;  Assert.NOT_NULL(mappings);
		this.keyCol = keyCol_; Assert.IS_TRUE(keyCol >= 0);
		this.valCol = valCol_; Assert.IS_TRUE(valCol >= 0); 
	}

	/** 
	 * @return the Value Column for the given Key Column
	 */
	public Object Map(final Object arg) {
		return MAP(mappings, arg, keyCol, valCol); }


	/** 
	 * @return the Key Column for the given Value Column
	 */
	public Object UnMap(final Object arg) {
		return MAP(mappings, arg, valCol, keyCol); }

}
