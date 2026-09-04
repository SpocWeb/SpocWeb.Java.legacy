package streamIO.fileSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import streamIO.object.AStreamIn;

/**
 * FileBackupIterator
 * Returns a new File and in parallel renames the former File
 * to a Backup Location.
 * Any previous Backup Files are overwritten. 
 * 
 * @see FileIterator
 * 
 * Created on 1. April 2001, 00:58
 * 
 * @author  Matthias Heuer
 * @version
 * @stereotype enumeration
 */
public class FileBackupIterator
extends AStreamIn {

	/** Output File */
	protected File file;

	/** Backup File */
	protected File backup;

	/** Stores whether there is a new File available.  */
	protected boolean available = true;

	/** Returns whether there is a new File available.  */
	public long availAble(){
		if (available) 
			return Long.MAX_VALUE; 
		return -1; }

	/** @see streamIO.object.AStreamIn#getMaxMarkSize()	 */
	public long getMaxMarkSize() { return Long.MAX_VALUE; }
	
	/** @see streamIO.object.AStreamIn#getPosition()	 */
	public long getPosition() { return 0; }

	/** @see streamIO.IIStreamIn#isValid()	 */
	public boolean isValid() { return available; }
	
	/** Creates new FileBackupEnumeration */
	public FileBackupIterator (final File _file, final File _backup) {
		this.backup = _backup;
		this.file = _file; }
	
	/** Creates new FileBackupEnumeration */
	public FileBackupIterator (final String _fileName, final String _backupName) {
		this(new File(_fileName), new File(_backupName)); }
	
	/**
	 * Renames the Output File to a Backup File
	 * and creates a new Output streamIO.
	 * @returns the next element of this enumeration
	 * in this Case a FileOutputStream for the Base File.
	 */
	public Object nextItem() {
		backup.delete(); //delete existing Backup Copy
		//Base.close(); //close and flush the Stream
		file.renameTo(backup); //rename the existing File
		try { return filter = new FileOutputStream(file); //appending makes no sense here.
		} catch (final IOException e) {
			available = false;
			return null; 
		} 
	}
	
	/** @see streamIO.object.AStreamIn#currItem()	 */
	public Object currItem() { return filter; }
	
}
