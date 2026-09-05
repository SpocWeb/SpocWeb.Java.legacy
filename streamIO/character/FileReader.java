/*
 * FileReader.java
 *
 * Created on 6. Dezember 2002, 15:47
 */

package streamIO.character;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A character-decoding file reader with static helpers for whole-file copy, whole-file
 * read and polling for a file's appearance, on top of the instance-level
 * {@link java.io.InputStreamReader} behavior its constructors set up.
 *
 * @author  MatthiasHeuer
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:09:11Z
 * digest: 2c440a6aa16fb2a7c2d06bbf6927dd2db8cb62dc32990d1925c855ee7178c810
 * stale: false
 * tags: [code/file_io, code/encoding_handling, code/file_polling]
 * concepts: [File I/O, Text Encoding]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
 */
public class FileReader
extends java.io.InputStreamReader
//implements IStreamIn_Byte
{

	/** Size used for reading */
	final static public char BUFFER_SIZE = 4096;

    /** copy a file
     * @param src name of the input file
     * @param dest name of the output file
     * @throws FileNotFoundException
     * @throws IOException
	 * @deprecated, use FileReader.COPY_FILE, because that one is faster
     */
    final static public void COPY_FILE(String from, String to) throws FileNotFoundException, IOException {
		COPY_FILE(new File(from), new File(to)); }

    /** copy a file
     * @param src name of the input file
     * @param dest name of the output file
     * @throws FileNotFoundException
     * @throws IOException
	 * @deprecated, use FileReader.COPY_FILE, because that one is faster
     */
    final static public void COPY_FILE(File from, File to) throws FileNotFoundException, IOException {
        FileInputStream  reader = new  FileInputStream(from);
        FileOutputStream writer = new  FileOutputStream(to);
		byte[] buffer = new byte[BUFFER_SIZE];
		for(int len; 0 < (len = reader.read(buffer));) {
			writer.write(buffer, 0, len); }
        reader.close();
        writer.close();
	}

	/** Reads the whole file into a String.
	  * @returns the Contents of the File in the given Encoding */
    final static public String READ_FILE(String fileName, String enc) throws IOException {
		return READ_FILE(new File(fileName), enc);
	}

	/** Reads the whole file into a String.
	  * @returns the Contents of the File in the given Encoding */
    final static public String READ_FILE(File file, String enc) throws IOException {
        StringBuffer ret = new StringBuffer("");
        FileReader reader = new  FileReader(file, enc);
		char[] buffer = new char[BUFFER_SIZE];
		for(int len; 0 < (len = reader.read(buffer));) {
            ret.append(buffer, 0, len); }
        reader.close();
        return ret.toString(); }

	/** Deletes the file if present, then polls until a new file appears at the same path.
	  * @return true when the File existed before the Timeout */
    final static public boolean WAIT_FOR_NEW_FILE(String fileName, long timeout) {
		return WAIT_FOR_NEW_FILE(new File(fileName), timeout); }

	/** Polls once a second until the file exists or the timeout elapses.
	  * @return true when the File existed before the Timeout */
    final static public boolean WAIT_FOR_FILE(String fileName, long timeout) {
		return WAIT_FOR_FILE(new File(fileName), timeout); }

	/** Deletes the file if present, then polls until a new file appears at the same path.
	  * @return true when the File existed before the Timeout */
    final static public boolean WAIT_FOR_NEW_FILE(File file, long timeout) {
		file.delete();
		return WAIT_FOR_FILE(file, timeout); }

	/** Polls once a second until the file exists or the timeout elapses.
	  * @return true when the File existed before the Timeout */
    final static public boolean WAIT_FOR_FILE(File file, long timeout) {
		timeout += System.currentTimeMillis();
		while (!file.exists()) {
			try { Thread.sleep(1000);
			} catch (InterruptedException x) {
				return false; }
			if (timeout < System.currentTimeMillis()) {
				return false; }
		} return true; }

	/**
	 * Waits for the file to appear, then reads it whole.
	 * @return the File Contents when it appears before the Timeout
	 */
    final static public String SYNCH_READ_FILE(String fileName, long timeout, String encoding)
    throws java.io.IOException {
		return SYNCH_READ_FILE(new File(fileName), timeout, encoding); }

	/**
	 * Waits for the file to appear, then reads it whole.
	 * @return the File Contents when it appears before the Timeout
	 */
    final static public String SYNCH_READ_FILE(File file, long timeout, String encoding)
    throws java.io.IOException {
		if (WAIT_FOR_FILE(file, timeout)) {
	        return READ_FILE(file, encoding); }
		return null; }

	/**
	 * Deletes the file if present, then waits for a new file to appear and reads it whole.
	 * @return the File Contents when it appears before the Timeout
	 * @deprecated, use FileReader.SYNCH_READ_NEW_FILE
	 */
    final static public String SYNCH_READ_NEW_FILE(String fileName, long timeout, String encoding)
    throws java.io.IOException {
		return SYNCH_READ_NEW_FILE(new File(fileName), timeout, encoding); }

	/**
	 * Deletes the file if present, then waits for a new file to appear and reads it whole.
	 * @return the File Contents when it appears before the Timeout
	 * @deprecated, use FileReader.SYNCH_READ_NEW_FILE
	 */
    final static public String SYNCH_READ_NEW_FILE(File file, long timeout, String encoding)
    throws java.io.IOException {
		file.delete();
		if (WAIT_FOR_NEW_FILE(file, timeout)) {
	        return READ_FILE(file, encoding); }
		return null; }

	/** Creates a new instance of FileReader */
	public FileReader(File file) throws FileNotFoundException {
		super(new FileInputStream(file));
	}

	/** Creates a new instance of FileReader */
	public FileReader(String fileName) throws FileNotFoundException {
		super(new FileInputStream(fileName));
	}

	/** Creates a new instance of FileReader */
	public FileReader(FileDescriptor fdescr) {
		super(new FileInputStream(fdescr));
	}

	/** Creates a new instance of FileReader */
	public FileReader(File file, String enc) throws FileNotFoundException, UnsupportedEncodingException {
		super(new FileInputStream(file), enc);
	}

	/** Creates a new instance of FileReader */
	public FileReader(String fileName, String enc) throws FileNotFoundException, UnsupportedEncodingException {
		super(new FileInputStream(fileName), enc);
	}

	/** Creates a new instance of FileReader */
	public FileReader(FileDescriptor fdescr, String enc) throws UnsupportedEncodingException {
		super(new FileInputStream(fdescr), enc);
	}

}
