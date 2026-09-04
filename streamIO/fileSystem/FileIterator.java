package streamIO.fileSystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import streamIO.Log;
import streamIO.object.AStreamIn;

/**
 * FileIterator
 *
 * streamIO of new Input- or Output- Streams with File Names just being counted up.
 * @see FileBackupIterator
 *
 * used by:
 * @see streamIO.Object.Byte.LimitedSizeOutputStream
 *
 * Created on 31. März 2001, 22:51
 *
 * @author  Matthias Heuer
 * @version
 * @stereotype enumeration
 */
public class FileIterator
extends AStreamIn
{
	
	private static final Log L = new Log(FileIterator.class); 
	
	/** Counter for the Files opened and their Number  */
	protected long counter;
	
	/** First Part of the File Name: Path and File Name */
	protected String leftName;
	
	/** Last Part of the File Name: Suffix */
	protected String rightName;
	
	/** Reference to the next Item of this streamIO.
	  * Necessary because State has to be stored between available()
	  * and nextItem() when using the Iterator Interface
	  * which cannot deal with 'null'. */
	//protected Object nextItem;
	
	/** The current File Name
	  * Made transient, because it needn't be serialized. */
	protected transient StringBuffer name = new StringBuffer();
	
	/** Determines whether an InPut streamIO is created from the Files or an OutPut streamIO.  */
	protected boolean input;
	
	/** Determines whether the Files are opened new or for appending.  */
	protected boolean append;
	
	/** Stores whether there is a new File available.  */
	protected boolean available = true;
	
	/** Returns whether there is a new File available.  */
	public long availAble(){
		if (available) 
			return Long.MAX_VALUE;
		return 0; }
	
	/** @see streamIO.IIStreamIn#isValid()	 */
	public boolean isValid() { return !available; }
	
	/** Creates new FileEnumeration  */
	public FileIterator(final String _leftName, final String _rightName, 
			final boolean _input, final boolean _append) {
		this.rightName = _rightName;
		this.leftName = _leftName;
		this.append = _append;
		this.input = _input;
	}
	
	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return counter; }

	/** @see streamIO.object.AStreamIn#currItem()	 */
	public Object currItem() { return currItem; }
	
	protected transient Object currItem; 
	
	/** Returns the next Element of this Enumeration.
	 *  This Enumeration has unlimited Output Elements,
	 *  but a limited Number of Input Elements.
	 *  According to this, hasMoreElements() increases the Counter,
	 *  whenever it is called, to allow for skipping missing Files.  */
	public Object nextItem() {
		if (!available) 
			return null;
		name.setLength(0); //indicate that the next Element has been fetched.
		for (;;) {
			try {
				name.append (leftName).append (++counter).append (rightName);
				if (input) return new  FileInputStream(name.toString());
				else       return new FileOutputStream(name.toString(), append);
			} catch (final FileNotFoundException e) {
				if (input) {
					available = false;	//
					return EOI; } //new StringBufferInputStream(""); }// Stream.EmptyInputStream();
				L.n("Cannot find '").l(name).l("'"); //Block and try to recover or change the Media
				if ("n".equals(L.readString("Continue? (Y/N)").toLowerCase())) 
					return EOI; //returning 'null' can lead to Errors when using ...
			}
		}
	}
}
