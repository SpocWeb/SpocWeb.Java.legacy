package streamIO.object;

import java.io.IOException;
import java.io.StreamTokenizer;

/**This is a thin Wrapper around StreamTokenizer (StreamParser),
 * so it can be used like an Enumerator.
 * It returns Double, String, EOI or null (on IOException).
 * It ignores single and multiline Java Style Comments
 * and the treatment of EOL as Separator can be determined by the ST.
 *
 * This is similar but not as powerful as the StreamParser or Scanner,
 * but it can directly parse numeric Objects, is faster and can stream.
 *
 * The Constructor takes a StreamTokenizer as Parameter,
 * which again takes a Reader or an InputStream as Parameter.
 * The InputStream has been deprecated, but can still be used!!!
 * Additionally the InputStreamReader could be used to bridge from
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T16:44:11Z
 * digest: 44a54f0d17f1bee0a8a91accbdb7d491b13370448981592f43d45c03361d378b
 * stale: false
 * tags: [code/stream_processing, code/iterator]
 * concepts: [Object Stream Pipeline]
 * facets: {layer: utility, status: legacy, complexity: medium}
 * -->
 * an InputStream to a Reader.	 */
public class StreamIterator
extends AStreamIn {
	
	/**Reference to the StreamTokenizer	 */
	protected StreamTokenizer ST;
	
	/**Reference to the current Item	 */
	protected Object currItem;
	
	/** Initializing Constructor   */
	public StreamIterator(StreamTokenizer ST) { this.ST = ST; }
	
	/**Feedback, whether more Items are available,
	 * since the Length of Tokens is undefined, it can return only 0 or 1.	 */
  	public long availAble() { return (ST.ttype != StreamTokenizer.TT_EOF) ? 1 : 0; }
  	
	/** Reports that marking is not supported by the wrapped {@link StreamTokenizer}.
	 * @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return -1; }

	/** Always returns 0, since the wrapped {@link StreamTokenizer} has no position to report.
	 * @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }
	
	/**Restarts the Enumerator	 */
//	public iresetable reset()	{ //ST.reader.reset(); }  //reader is an inner Component and cannot be reset!
	//The Stream has to be reopened, no handle on the Reader to call it's reset() Method
	
	/**Returns the current Item and indicates whether there are more...
	 * Possible Return Classes are Double, String, EOI or null (on IOExceptions)
	 */
	public Object currItem() { return currItem; }
	
	/**Returns the next Item and indicates whether there are more...
	 * Possible Return Classes are Double, String, EOI or null (on IOExceptions)
	 */
	public Object nextItem() {
		try{
			switch (ST.nextToken()) {
				case StreamTokenizer.TT_NUMBER:	return currItem = new Double(ST.nval);
				case StreamTokenizer.TT_WORD:	return currItem = ST.sval;
				case StreamTokenizer.TT_EOF:	return currItem = EOI;
			}
		} catch (final IOException x) {}
		return null; }
	
}
