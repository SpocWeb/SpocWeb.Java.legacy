package streamIO; //TODO: always define a Package

/**
  * Title: IAvailAble<p>
  * Description:
  * Defines the Interface for an Input streamIO with additional Information
  * about the (minimum) Number of (currently) available Elements.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-28-2002, 10:37 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IAvailAble {
	
	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @return the (minimum) Number of Items left (in the Buffer),
	  * i.e. the minimum Number of times to call nextItem().
	  * The actual Number may be higher, so available() should be called again
	  * at the End of this Number of calls to nextItem().
	  * This can be used to dimension Buffers for Processing.
	  * Since Streams are not required to return this Number accurately,
	  * it was moved into the StreamIn Interface.
	  * 
	  * For Random Number Generators this returns the Period 
	  * where the Numbers repeat themselves. 
	  * 
	  * Nearly equivalent is currItem != null
	  * (when the Container does not contain null Entries, like e.g. HashTables)
	  * 
	  * To make the Test easier for Clients, the isEmpty() Method has been added,
	  * which is equivalent to available() &lt; 0
	  */
	public long availAble();

    /**
     * This Value can be used for mark()ing. 
     * @see java.io.RandomAccessFile#getFilePointer()
     * @return the current Position in the Stream, 
     * counting from 0 after mark()ing (relative) 
     * or absolute (if mark() is not supported).
     */
    public long getPosition(); 
    
}
