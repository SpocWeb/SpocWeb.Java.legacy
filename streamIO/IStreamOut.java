package streamIO;

import java.io.IOException;
import java.io.OutputStream;

/**
  * StreamOut.java
  * Interface defined for a Store analogous to an OutputStream
  * Not used 'SemiGroup', because that carries too much Overhead
  * and you have to distinguish between addAt() and addItem() anyway,
  * because addAt() unwraps Containers and Iterators.
  *
  * Any StreamOut can also be used as a DeMultiplexer (on a Get Request Basis)
  * by just connecting several Processes, Threads etc. to it
  *
  * Created on 26. Mai 2001, 21:28
  *
  * @author  Matthias Heuer
  * @version
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:24Z
  * digest: 5546862285d0b85316ed8a5a76a6f1619c45d7b0d811dc47a40a0050e81d0cfc
  * stale: false
  * tags: [code/output_stream]
  * concepts: [Output Stream Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IStreamOut
	extends IIStreamOut {

	/**
	 * Reference to the single empty Outputstream.
	 */
	final static public DevNullOut DevNullOut = new DevNullOut();

	/** Adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but not recursively, but only flattened by one Level (flatDepth == 1).
	  * Named with capital A, to distinguish it from streamIO.Copy.Group.add() 	*/
	public long addItems(Object arg);

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but only recursively, when flattened is true.	  */
	public long addItems(Object arg, int flatDepth);

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
      * Should be called addAt(), but that would result in Ambiguities
      * with the addAt() Method of Group and Container	 */
	public long addItems(Object[] arg); //throws IOException;// { return this; }

	/** adds all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
      * Should be called addAt(), but that would result in Ambiguities
      * with the addAt() Method of Group and Container	 */
	public long addItems(IIStreamIn arg); //throws IOException;// { return this; }

	/** forces the Stream to clear all it's buffers, propagated downstream. 
	 * An InputStream doesn't need this, because reading forces the read Process upstream.	*/
	public void flush() throws IOException; 
	
}


/**
 * Instance of a Null Device that takes any Input and ignores/destroys it
 * Simple Helper Class to avoid complicated Workarounds
 * Always symbolizes an empty Output streamIO.
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:24Z
 * digest: 2e6f8f70a3c9ecf586e2552e59d927b04635822ef5a47f413b95fdcd7b1a8cb8
 * stale: false
 * tags: [code/output_stream]
 * concepts: [Null Output Stream]
 * facets: {layer: infrastructure, status: legacy, complexity: low}
 * -->
 */
class DevNullOut
extends OutputStream
implements IStreamOut {

	/** Singleton Constructor 	 */
	DevNullOut() {};
	
	/** single Method always returning EOI,
	  * signifying an empty Input streamIO
	  */
	public void write(int Value) { }

	/** adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public IIStreamOut addItem(Object arg) { return this; }

	///////////////////////////////////////////////////////////////////////////////////
	/// Optimizations
	///////////////////////////////////////////////////////////////////////////////////
	
	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but not recursively, but only flattened by one Level (flatDepth == 1).	  */
	public long addItems(Object arg) { return 0; }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is analyzed, i.e. Containers Contents is added,
	  * but only recursively, when flattened is true.	  */
	public long addItems(Object arg, int flatDepth) { return 0; }

	/** adds these Items to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.	  */
	public long addItems(Object[] arg) { return 0; }

	/** adds all Items from the Enumerator to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * Returns 'this' if the Object was written
	  * and 'null', if the Record could not be written,
	  * e.g. because the Drive is full or any other Error occurred (e.g. IOException)!
	  * So the Return Value should be tested!
	  * Alternatively this Method can block until new Data is available,
	  * but this should always have a TimeOut to avoid DeadLocks.
	  */
	public long addItems(IIStreamIn arg) { return 0; }

}
