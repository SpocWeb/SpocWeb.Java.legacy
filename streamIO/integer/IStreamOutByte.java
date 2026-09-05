package streamIO.integer;

import java.io.IOException;

/**
  * Title: IStreamOutByte<p>
  * Description:
  * This Interface substitutes the Class OutputStream and PrintStream 
  * in all Implementations!
  * The Reason is that the RandomAccessFile Class implements all Methods of
  * both OutputStream and InputStream resp. Writer and Reader, 
  * but Sun chose to define these Methods in Classes rather than Interfaces.
  *
  * Additionally four Methods have been added to:
  * write Character Arrays, Integers and Strings.
  *
  * Known Implementors:
  * @see streamIO.Byte.AStreamOutByte
  * @see streamIO.Byte.FileStreamOutByte
  * @see streamIO.Byte.FileStreamByte
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	1999-01-04, 08;58;28<p>
  * @author 	Matthias Heuer
  * @version	1.0
  *
  * @see IStreamIn_Byte
  * @see java.io.OutputStream
  * @see java.io.InputStream
  * @see java.io.PrintStream 
  * @see java.io.Writer
  * <!-- docstate
  * tags: [code/stream_io, code/stream_input, code/stream_output, code/struct]
  * concepts: [Primitive and Structured Stream I/O Core Abstractions]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public interface IStreamOutByte {
	
	/**
	  * Flushes this output stream and forces any buffered output bytes to be written out.
	  * The general contract of flush is that calling it is an indication that,
	  * if any bytes previously written have been buffered
	  * by the implementation of the output stream,
	  * such bytes should immediately be written to their intended destination.
	  *
	  * The flush method of OutputStream does nothing.
	  *
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract void flush()	throws IOException;

	/**
	  * Closes this output stream and releases any system resources associated with this stream.
	  * The general contract of close is that it closes the output stream.
	  * A closed stream cannot perform output operations and cannot be reopened.
	  *
	  * The close method of OutputStream does nothing.
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract void close() throws IOException;
	
	///////////////////////////////////////////////////////////////////////////
	/// Methods of java.io.OutputStream
	///////////////////////////////////////////////////////////////////////////
	
	/**
	  * Writes the specified byte to this output stream.
	  * The general contract for write is that one byte is written to the output stream.
	  * The byte to be written is the eight low-order bits of the argument b.
	  * The 24 high-order bits of b are ignored.
	  *
	  * Subclasses of OutputStream must provide an implementation for this method.
	  *
	  * @param b - the byte.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException may be thrown if the output stream has been closed.
	  * @see java.io.OutputStream#write(int)
	  */
	public abstract void write(final int b) throws IOException;
	
	/**
	  * Writes b.length bytes from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  * @see java.io.OutputStream#write(byte[])
	  */
	public abstract void write(final byte[] b)	throws IOException;

	/**
	  * Writes len bytes from the specified byte array starting at offset off
	  * to this output stream.
	  * The general contract for write(b, off, len) is
	  * that some of the bytes in the array b are written to the output stream in order;
	  * element b[off] is the first byte written and b[off+len-1] is the last byte
	  * written by this operation.
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
	  * @see java.io.OutputStream#write(byte[], int, int)
	  */
	public abstract void write(final byte[] b, final int off, final int len) throws IOException;

	/**
	  * Writes b.length Characters from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	//public abstract IStreamOutByte addItem(final int[] b);
	
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
	//public abstract IStreamOutByte addItem(final int[] b, final int off, final int len);
	
}
