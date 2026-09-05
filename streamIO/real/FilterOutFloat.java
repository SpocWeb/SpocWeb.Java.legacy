/*
 * Created on 14.11.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package streamIO.real;

/**
 * Generic pass-through filter for streams of float or double numbers, forwarding to an
 * optional delegate.
 *
 * <p>Design Decisions:
 *
 * @author heuerm
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T11:19:15Z
 * digest: 468f5e95a6c26ad259c21fb57c5801c781ffded68b4ea6883efa31faebd6a710
 * stale: false
 * tags: [code/stream_filter]
 * concepts: [Float Output Filter]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
public class FilterOutFloat
implements  IStreamOutFloat {

	/** Reference to the Delegate	 */
	final protected IStreamOutFloat outStream;

	/** Creates a filter forwarding to the given delegate, or acting as a sink when {@code _out} is null.
	 * @param _out the destination stream to forward to, or null
	 */
	public FilterOutFloat(final IStreamOutFloat _out) {
		this.outStream = _out;
	}

	/** Forwards a float value to the delegate, if one is set.
	 * @see streamIO.real.IStreamOutFloat#addFloat(float)	 */
	public IStreamOutFloat addFloat(final float value) {
		if (outStream != null)
			return outStream.addFloat(value);
		return this;
	}

	/** Forwards a double value to the delegate, if one is set.
	 * @see streamIO.real.IStreamOutFloat#addDouble(double)	 */
	public IStreamOutFloat addDouble(final double value) {
		if (outStream != null)
			return outStream.addDouble(value);
		return this;
	}

}
