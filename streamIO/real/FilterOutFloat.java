/*
 * Created on 14.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.real;

/**
 * Title: FilterOutFloat <p>
 * 
 * Generic Filter for Streams of float or double Numbers. 
 *
 * Design Decisions:
 * 
 * 
 * @author heuerm
 *
 */
public class FilterOutFloat 
implements  IStreamOutFloat {
	
	/** Reference to the Delegate	 */
	final protected IStreamOutFloat outStream;
	
	/**
	 * 
	 */
	public FilterOutFloat(final IStreamOutFloat _out) {
		this.outStream = _out; 
	}
	
	/** @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public IStreamOutFloat addFloat(final float value) {
		if (outStream != null) 
			return outStream.addFloat(value); 
		return this;
	}
	
	/** @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		if (outStream != null) 
			return outStream.addDouble(value); 
		return this;
	}
	
}
