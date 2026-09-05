package streamIO;

/**
  * IStreamOut.java
  * Minimal Interface defined for a Store analogous to an OutputStream
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
  * digest: d36132cd70ea67ea3a73528d894335a413523b20dd93752c85f138e19157d190
  * stale: false
  * tags: [code/output_stream]
  * concepts: [Item Output Stream Interface]
  * facets: {layer: infrastructure, status: legacy, complexity: low}
  * -->
  */
public interface IIStreamOut
//extends InstantiAble
{

	/** to handle the Exception
	 * @param x the Exception to be handled
	 * @throw StopTest when the Exception could not be handled
	 * e.g. when the Exception should stop the whole Test.
	 *
	 * This can be replaced by addItem()
	 * either by throwing a Runtime Exception or
	 * by returning null from this Method to indicate no more handling.
	 */
//	public void handleException(Throwable x) throws StopTest;

	/** Adds this Item to the Store in Place: +=
	  * The Type of Item is not analyzed, i.e. Containers are added as is.
	  * The Position of the Item is undefined either.
	  * When IO Processes are bound to this streamIO, IOException is wrapped into an IOError.
	  * @return this StreamOut or a SubStreamOut to append more Items
	  */
	public IIStreamOut addItem(Object arg); //throws IOException;// { return this; }

	/** Creates a new Output streamIO
	  * modeled according to this one.
	  * This is similar to streamIO.Copy.ICopyAble.newInstance()
	  * which returns a CopyAble (artificially enough, since the ICopyAble is not Standalone)
	  * @return a new StreamOut modeled after this one
	  *
	  * Design Decisions:
	  * this is not necessary anymore, because addItem returns this SubStream
	  * already added to it's internal structure,
	  * which should suffice for nested copying.
	  */
//	public IStreamOut newStreamOut(); //

}
