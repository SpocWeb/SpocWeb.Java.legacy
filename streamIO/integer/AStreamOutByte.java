package streamIO.integer;

import java.io.IOException;
import java.io.InputStream;

import streamIO.AStreamOut;
import streamIO.IIStreamOut;
import streamIO.integer.encoding.FilterLookup;
import streamIO.integer.filter.FilterOutByte;
import streamIO.integer.pipe.ByteStreamerThread;
import tools.IOError;

/**
  * Title: AStreamOutByte<p>
  * Description:
  * This Adapter Class delegates all Methods of the Interface IStreamOutByte
  * to an inner Instance of Class IStreamOutByte or OutputStream.
  *
  * Known Subclasses:
  * @see FilterOutByte
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
  */
public abstract class AStreamOutByte
extends AStreamOut
implements IStreamOutByte {
	
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	final static public void ESCAPE_SAFE (final IStreamOutByte ths, final String str, final String separators) {
		try { ESCAPE_UNSAFE (ths, str, separators); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}
		
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  *
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	final static public void ESCAPE_SAFE (final IStreamOutByte ths, final StringBuffer str, final String separators) {
		try { ESCAPE_UNSAFE (ths, str, separators); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}
		
	/**
	  * Writes the String b to the Stream, but escapes any Character from the Separator String. 
	  * Slightly ineffective, since you have to search the forbidden Chars in the separators String. 
	  * 
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	final static public void ESCAPE_UNSAFE (final IStreamOutByte ths, final StringBuffer str, final String separators
			) throws IOException {
		final char escapeChr = separators.charAt(0); 
		for(int len = str.length(), i = -1; ++i < len; ) {
			char chr = str.charAt(i); 
			if (separators.indexOf(chr) >= 0) {  //the actual Position doesn't matter
				ths.write(escapeChr);
				chr = FilterLookup.ASCII2ESCAPE(chr); //use Standard Escape Sequences like \t\r\n\\
			} 
			ths.write(chr); 
		}
	}
	
	/**
	  * Writes the String b to the Stream, 
	  * but escapes any Character from the Separator String. 
	  * Slightly ineffective, 
	  * since you have to search the forbidden Chars in the separators String.
	  * Could be more effective by sorting or even better: looking it up!  
	  * 
	  * @param b - the data.
	  * @param separators - the separator Characters with the Escape Character at the first Position
	  * @throws IOException - if an I/O error occurs.
	  */
	final static public void ESCAPE_UNSAFE (final IStreamOutByte ths, final String str, final String separators
			) throws IOException {
		final char escapeChr = separators.charAt(0); 
		for (int len = str.length(), i = -1; ++i < len; ) {
			char chr = str.charAt(i); 
			if (separators.indexOf(chr) >= 0) {  //the actual Position doesn't matter
				ths.write(escapeChr);
				chr = FilterLookup.ASCII2ESCAPE(chr); //use Standard Escape Sequences like \t\r\n\\
			} 
			ths.write(chr); 
		}
	}
	
	/**
	  * Writes len Characters from the specified String starting at offset off
	  * to this Output streamIO.
	  * The general Contract for write(b, off, len) is
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
	final static public void WRITE(final IStreamOutByte stream, final String b) throws IOException {
		WRITE(stream, b, 0, b.length()); 
	}

	/**
	  * Writes len Characters from the specified String starting at offset off
	  * to this Output streamIO.
	  * The general Contract for write(b, off, len) is
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
	final static public void WRITE(final IStreamOutByte stream, 
			final String b, final int off, int len) throws IOException {
		len  += off; 
		for (int i = off-1; ++i < len;) {
			stream.write(b.charAt(i)); }
	}
	
	/**
	  * Writes len Characters from the specified StringBuffer starting at offset off
	  * to this Output streamIO without taking Measures to encode it.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	final static public void WRITE_SAFE(final IStreamOutByte stream, 
			final StringBuffer b, final int stop, int start) {
		try { WRITE_UNSAFE(stream, b, stop, start); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}
	
	/**
	  * Writes len Characters from the specified StringBuffer starting at offset off
	  * to this Output streamIO.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	final static public void WRITE_UNSAFE(final IStreamOutByte stream, 
			final StringBuffer b, final int stop, int start) throws IOException {
		for (int i = start-1; ++i < stop;) 
			stream.write(b.charAt(i)); 
	}
	
	/**
	  * Writes len Characters from the specified StringBuffer starting at offset off
	  * to this Output streamIO.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	final static public void WRITE_SAFE(final IStreamOutByte stream, final String b) {
		WRITE_SAFE(stream, b, b.length(), 0); }
	
	/**
	  * Writes len Characters from the specified StringBuffer starting at offset off
	  * to this Output streamIO.
	  *
	  * If b is null, a NullPointerException is thrown.
	  *
	  * If start or stop is negative, 
	  * then an IndexOutOfBoundsException is thrown.
	  *
	  * @param b - the data.
	  * @param start - the start offset in the data.
	  * @param stop  - the last byte to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	final static public void WRITE_SAFE(final IStreamOutByte stream, 
			final String b, final int stop, int start) {
		try { WRITE_UNSAFE(stream, b, stop, start); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}

	/** Writes the Characters from start (inclusive) to stop (exclusive) 
	 * to the specified stream.
	 *
	 * @param b - the data.
	 * @param start - the first Character to write.
	 * @param stop  - the  last Character to write.
	 * @throws IOException - if an I/O error occurs.
	 * 	In particular, an IOException is thrown if the output stream is closed.
	 */
	final static public void WRITE_UNSAFE(final IStreamOutByte stream, final String b) throws IOException {
		WRITE_UNSAFE(stream, b, b.length(), 0); }

	/** Writes the Characters from start (inclusive) to stop (exclusive) 
	 * to the specified stream.
	 *
	 * @param b - the data.
	 * @param start - the first Character to write.
	 * @param stop  - the  last Character to write.
	 * @throws IOException - if an I/O error occurs.
	 * 	In particular, an IOException is thrown if the output stream is closed.
	 */
	final static public void WRITE_UNSAFE(final IStreamOutByte stream, 
			final String b, final int stop, int start) throws IOException {
		for (int i = start-1; ++i < stop;) 
			stream.write(b.charAt(i)); 
	}
	
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
	final static public void WRITE_SAFE(final IStreamOutByte stream, 
			final char[] b, final int off, int len) {
		try { WRITE(stream, b, off, len); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}

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
	final static public void WRITE(final IStreamOutByte stream, 
			final char[] b, final int off, int len
			) throws IOException {
		len  += off;
		for (int i = off-1; ++i < len;) {
			stream.write(b[i]); }
	}

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
	  */
	final static public void WRITE(final IStreamOutByte stream
			, final byte[] b, final int off, int len) throws IOException {
		len  += off;
		for (int i = off-1; ++i < len;) {
			stream.write(b[i]); }
	}

	/**
	  * Writes len Integers from the specified Integer array starting at offset off
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
	  * @param stop - the start offset in the data.
	  * @param start - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	final static public void WRITE_SAFE(final IStreamOutByte stream
			, final int[] b, final int stop, final int start) {
		try { WRITE_UNSAFE(stream, b, stop, start); 
		} catch (final IOException x) {
			throw new IOError(x); 
		}
	}

	/**
	  * Writes len Integers from the specified Integer array starting at offset off
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
	  * @param stop - the start offset in the data.
	  * @param start - the number of bytes to write.
	  * @throws IOException - if an I/O error occurs.
	  * 	In particular, an IOException is thrown if the output stream is closed.
	  */
	final static public void WRITE_UNSAFE(final IStreamOutByte stream
			, final int[] b, final int stop, int start) throws IOException {
		for (int i = start-1; ++i < stop;) 
			stream.write(b[i]); 
	}

	////////////////////////////////////////////////////////////////////////////////
	//  public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////
	
	////////////////////////////////////////////////////////////////////////////////
	//  IStreamOut Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/** @see streamIO.IIStreamOut#addItem(java.lang.Object)	 */
	public IIStreamOut addItem(final Object arg) {
		try { WRITE(this, String.valueOf(arg));
		} catch (final IOException x) {
			throw new IOError(x);
		}
		return this; }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Interface StreamOutByte: abstract Methods
	////////////////////////////////////////////////////////////////////////////////
	
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
	  */
	public abstract void write(final int b) throws IOException;
	
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
	  * @see AStreamOut#flush()
	  * @see java.io.OutputStream#flush()
	  */
	public abstract void flush() throws IOException;

	/**
	  * Closes this output stream and releases any system resources associated with this stream.
	  * The general contract of close is that it closes the output stream.
	  * A closed stream cannot perform output operations and cannot be reopened.
	  *
	  * The close method of OutputStream does nothing.
	  * @throws IOException - if an I/O error occurs.
	  */
	public abstract void close() throws IOException;

	////////////////////////////////////////////////////////////////////////////////
	//  Interface IStreamIn_Byte: Implementation
	////////////////////////////////////////////////////////////////////////////////
	
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
	  */
	public void write(final byte[] b, final int off, final int len) throws IOException {
		WRITE(this, b, off, len); }
	
	/**
	  * Writes b.length bytes from the specified byte array to this output stream.
	  * The general contract for write(b) is
	  * that it should have exactly the same effect as the call write(b, 0, b.length).
	  *
	  * @param b - the data.
	  * @throws IOException - if an I/O error occurs.
	  * @see write(byte[], int, int)
	  */
	public void write(final byte[] b)	throws IOException { write(b, 0, b.length); }
	
	////////////////////////////////////////////////////////////////////////////////
	//  Stream Methods
	////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called readFrom()
	 * @param in the Stream to stream from
	 * @return the number of Values streamed 
	 */
	public long stream(final IStreamIn_Byte in) throws IOException {
		return ByteStreamerThread.STREAM(in, this);
	}
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called readFrom()
	 * @param in the Stream to stream from
	 * @return the number of Bytes streamed 
	 */
	public long stream(final InputStream in) throws IOException {
		return ByteStreamerThread.STREAM(in, this); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called readFrom()
	 * @param in the Stream to stream from
	 * @param chunkSize the Size to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final IStreamIn_Byte in, final int chunkSize) throws IOException {
		return ByteStreamerThread.STREAM(in, this, chunkSize); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called readFrom()
	 * @param in the Stream to stream from
	 * @param chunkSize the Size to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final InputStream in, final int chunkSize) throws IOException {
		return ByteStreamerThread.STREAM(in, this, chunkSize); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called readFrom()
	 * @param in the Stream to stream from
	 * @param buffer the Buffer to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final IStreamIn_Byte in, final byte[] buffer) throws IOException {
		return ByteStreamerThread.STREAM(in, this, buffer); }
	
	/**
	 * streams the current Stream into the given Output Stream
	 * could also be called writeTo()
	 * @param in the Stream to stream from
	 * @param buffer the Buffer to use for chunk-wise reading 
	 * @return the number of Values streamed 
	 * @throws IOException when an Error happens
	 */
	public long stream(final InputStream in, final byte[] buffer) throws IOException {
		return ByteStreamerThread.STREAM(in, this, buffer); }
	
}
