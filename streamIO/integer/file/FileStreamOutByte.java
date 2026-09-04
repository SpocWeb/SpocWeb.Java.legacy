package streamIO.integer.file;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import streamIO.integer.AStreamOutByte;
import streamIO.integer.IStreamIn_Byte;
import streamIO.integer.IStreamOutByte;

/**
  * Title: FileStreamOutByte<p>
  * Description:
  * This Class substitutes the Class FileOutputStream in all Implementations
  * The Reason is that the RandomAccessFile Class implements all Methods of
  * both OutputStream and InputStream but sun chose to define these Methods
  * in classes rather than Interfaces, so it cannot be subclassed directly.
  *
  * Design Decisions: 
  * Defines a new Protocol Interpretation: -1 closes the File / streamIO 
  * and triggers the Creation of a new one! 
  * 
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	1999-01-04, 08;58;28<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see IStreamIn_Byte for a Byte  Input streamIO
  * @see IStreamOutByte for a Byte Output streamIO
  * @see StreamByte for a random Access Byte streamIO
  * @see java.io.OutputStream
  * @see java.io.InputStream
  * @see java.io.FileOutputStream
  * @see java.io.FileInputStream
  * @see java.io.RandomAccessFile
  * @see java.io.DataInput
  * @see java.io.DataOutput
  *
  */
public class FileStreamOutByte
extends FileOutputStream
implements IStreamOutByte {

	/**
	  * Creates an output file stream to write to the file with the specified name.
	  * A new FileDescriptor object is created to represent this file connection.
	  * First, if there is a security manager, its checkWrite method is called
	  * with name as its argument.
	  *
	  * If the file exists but is a directory rather than a regular file,
	  * does not exist but cannot be created, or cannot be opened for any other reason
	  * then a FileNotFoundException is thrown.
	  *
	  * @param name - the system-dependent filename
	  * @throws FileNotFoundException - if the file exists but is a directory rather than a regular file,
	  * 	does not exist but cannot be created, or cannot be opened for any other reason
	  * @throws SecurityException - if a security manager exists and its checkWrite method denies write access to the file.
	  * @see SecurityManager.checkWrite(java.lang.String)
	  */
	public FileStreamOutByte(final String name) throws FileNotFoundException {
		super(name); }

	/**
	  * Creates an output file stream to write to the file with the specified name.
	  * If the second argument is true, then bytes will be written to the end of the file
	  * rather than the beginning.
	  * A new FileDescriptor object is created to represent this file connection.
	  * First, if there is a security manager, its checkWrite method is called
	  * with name as its argument.
	  *
	  * If the file exists but is a directory rather than a regular file,
	  * does not exist but cannot be created, or cannot be opened for any other reason
	  * then a FileNotFoundException is thrown.
	  *
	  * @param name - the system-dependent file name
	  * @param append - if true, then bytes will be written to the end of the file rather than the beginning
	  * @throws FileNotFoundException - if the file exists but is a directory rather than a regular file,
	  * 	does not exist but cannot be created, or cannot be opened for any other reason.
	  * @throws SecurityException - if a security manager exists and its checkWrite method denies write access to the file.
	  * @since JDK1.1
	  * @see SecurityManager.checkWrite(java.lang.String)
	  */
	public FileStreamOutByte(String name, boolean append) throws FileNotFoundException {
		super(name, append); }

	/**
	  * Creates a file output stream to write to the file represented by the specified File object.
	  * A new FileDescriptor object is created to represent this file connection.
	  * First, if there is a security manager, its checkWrite method is called
	  * with the path represented by the file argument as its argument.
	  *
	  * If the file exists but is a directory rather than a regular file,
	  * does not exist but cannot be created, or cannot be opened for any other reason
	  * then a FileNotFoundException is thrown.
	  *
	  * @param file - the file to be opened for writing.
	  * @throws FileNotFoundException - if the file exists but is a directory rather than a regular file,
	  * 	does not exist but cannot be created, or cannot be opened for any other reason
	  * @throws SecurityException - if a security manager exists and its checkWrite method denies write access to the file.
	  * @see File.getPath()
	  * @see SecurityException
	  * @see SecurityManager.checkWrite(java.lang.String)
	  */
	public FileStreamOutByte(File file) throws FileNotFoundException {
		super(file); }

	/**
	  * Creates an output file stream to write to the specified file descriptor,
	  * which represents an existing connection to an actual file in the file system.
	  * First, if there is a security manager, its checkWrite method is called
	  * with the file descriptor fdObj argument as its argument.
	  *
	  * @param fdObj - the file descriptor to be opened for writing.
	  * @throws SecurityException - if a security manager exists
	  * 	and its checkWrite method denies write access to the file descriptor.
	  * @see SecurityManager.checkWrite(java.io.FileDescriptor)
	  */
	public FileStreamOutByte(FileDescriptor fdObj) {
		super(fdObj); }

////////////////////////////////////////////////////////////////////////////////
//  Interface StreamOutByte: Methods handling Characters and Strings
////////////////////////////////////////////////////////////////////////////////

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @see write(byte[], int, int)
	  */
	public void write(char[] b) { write(b, 0, b.length); }

	/**
	  * Writes len Characters from the specified byte array starting at offset off
	  * to this output stream.
	  * The general contract for write(b, off, len) is
	  * that some of the Characters in the array b are written to the output stream in order;
	  * element b[off      ] is the first Character written
	  * and     b[off+len-1] is the  last Character written by this operation.
	  * The write method of OutputStream calls the write method of one argument
	  * on each of the bytes to be written out.
	  * Subclasses are encouraged to override this method
	  * and provide a more efficient implementation.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If off is negative, or len is negative,
	  * or off+len is greater than the length of the array b,
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param off - the start offset in the data.
	  * @param len - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	public void write(char[] b, int off, int len) {
		AStreamOutByte.WRITE_SAFE(this, b, off, len); }

	/**
	  * Writes b.length Characters from the specified String to this Output streamIO.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public void write(String b)	throws IOException { write(b, 0, b.length()); }

	/**
	  * Writes len Characters from the specified String starting at Offset off
	  * to this Output streamIO.
	  * The general contract for write(b, off, len) is
	  * that some of the Characters in the array b are written to the output stream in order;
	  * element b[off      ] is the first Character written
	  * and     b[off+len-1] is the  last Character written by this operation.
	  * The write method of OutputStream calls the write method of one argument
	  * on each of the bytes to be written out.
	  * Subclasses are encouraged to override this method
	  * and provide a more efficient implementation.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If off is negative, or len is negative,
	  * or off+len is greater than the length of the array b,
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param off - the start offset in the data.
	  * @param len - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	public void write(final String b, final int off, final int len) throws IOException {
		AStreamOutByte.WRITE(this, b, off, len); }

	/**
	  * Writes b.length Characters from the specified String to this Output streamIO.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public IStreamOutByte addBuffer(final StringBuffer b)	{ 
		return addBuffer(b, b.length()); }
	
	/**
	  * Writes len Characters from the specified String starting at Offset off
	  * to this Output streamIO.
	  * The general contract for write(b, off, len) is
	  * that some of the Characters in the array b are written to the output stream in order;
	  * element b[off      ] is the first Character written
	  * and     b[off+len-1] is the  last Character written by this operation.
	  * The write method of OutputStream calls the write method of one argument
	  * on each of the bytes to be written out.
	  * Subclasses are encouraged to override this method
	  * and provide a more efficient implementation.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If off is negative, or len is negative,
	  * or off+len is greater than the length of the array b,
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param off - the start offset in the data.
	  * @param len - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop) {
		return addBuffer(b, stop, 0); }
	
	/**
	  * Writes len Characters from the specified String starting at Offset off
	  * to this Output streamIO.
	  * The general contract for write(b, off, len) is
	  * that some of the Characters in the array b are written to the output stream in order;
	  * element b[off      ] is the first Character written
	  * and     b[off+len-1] is the  last Character written by this operation.
	  * The write method of OutputStream calls the write method of one argument
	  * on each of the bytes to be written out.
	  * Subclasses are encouraged to override this method
	  * and provide a more efficient implementation.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If off is negative, or len is negative,
	  * or off+len is greater than the length of the array b,
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param off - the start offset in the data.
	  * @param len - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	public IStreamOutByte addBuffer(final StringBuffer b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String)	 */
	public IStreamOutByte addString(final String b) { return addString(b, b.length()); } 

	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int)	 */
	public IStreamOutByte addString(final String b, final int stop) {
		return addString(b, stop, 0); } 

	/** @see streamIO.integer.IStreamOutByte#addString(java.lang.String, int, int)	 */
	public IStreamOutByte addString(final String b, final int stop, final int start) {
		AStreamOutByte.WRITE_SAFE(this, b, stop, start); 
		return this; }
	
	/**
	  * Writes b.length bytes from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public void write(final byte[] b)	throws IOException { 
		write(b, 0, b.length); }

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	public void addItem(final int[] b) { addItem(b, 0, b.length); }
	  */

	/**
	  * Writes len Characters from the specified byte array starting at offset off
	  * to this output stream.
	  * The general contract for write(b, off, len) is
	  * that some of the Characters in the array b are written to the output stream in order;
	  * element b[off      ] is the first Character written
	  * and     b[off+len-1] is the  last Character written by this operation.
	  * The write method of OutputStream calls the write method of one argument
	  * on each of the bytes to be written out.
	  * Subclasses are encouraged to override this method
	  * and provide a more efficient implementation.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If off is negative, or len is negative,
	  * or off+len is greater than the length of the array b,
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param off - the start offset in the data.
	  * @param len - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	public void addItem(final int[] b, final int off, final int len) {
		AStreamOutByte.WRITE_SAFE(this, b, off, len); }
	  */

	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutByte escapeString(final String str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	public IStreamOutByte escapeString(final StringBuffer str, final String separators) {
		AStreamOutByte.ESCAPE_SAFE(this, str, separators);
		return this; 
	}
	
}
