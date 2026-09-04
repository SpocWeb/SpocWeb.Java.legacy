package streamIO.integer; //TODO: always define a Package

import java.io.IOException;

/**
  * Title: IStreamByteRandom<p>
  * Description:
  * Defines the Interface for ...TODO: Describes the Purpose / Responsibilities
  * of this Interface, not it's Implementation.
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  * All interface Operations are implicitly public and abstract.
  * All interface Attributes are implicitly public, final and static.
  *
  * Known SubInterfaces: <none>
  *
  * Known Implementors: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	12-22-2002, 03:52 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public interface IStreamByteRandom 
extends IStreamByte {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * returns the current physical Position in this File. 
	 * @see java.io.RandomAccessFile#getFilePointer()
	 * @return the current physical Position in this File. 
	 */
	public long getFilePointer()   throws IOException;   // in updateRow(),
	
	/**
	 * @see java.io.RandomAccessFile#length()
	 * @return the Length of the File 
	 * @throws IOException
	 */
	public long length()           throws IOException; // in updateRow(), moveToInsertRow()

	/**
	 * used to trim the File.
	 * @see java.io.RandomAccessFile#setLength(long)
	 * @throws IOException
	 */
	public void setLength(long newLength) throws IOException; // 

	/**
	 * if the Position is beyond the File Length, a Write Operation will extend the File after this Position. 
	 * @see java.io.RandomAccessFile#seek(long)
	 * @param Pointer the Position to locate the Pointer to
	 * @throws IOException
	 */
	public void seek(final long Pointer) throws IOException; // in updateRow(), beforeFirst(), compress()
	
	/**
	 * @see IStreamIn_Byte#skip(long) already defines a similar Method 
	 * not needed, can be implemented using seek(long) and length(): <br/>
	 * seek(n+getPosition) 
	 * @see java.io.RandomAccessFile#skipBytes(int)
	 * @param n must not be negative! 
	 * @return the Number of Bytes actually skipped
	 * @throws IOException
	 */
	//public int skipBytes(final int n) throws IOException;
	
}
