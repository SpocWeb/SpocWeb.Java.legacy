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
 *
 * @author  MatthiasHeuer
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

	/** @returns the Contents of the File in the given Encoding */
    final static public String READ_FILE(String fileName, String enc) throws IOException {
		return READ_FILE(new File(fileName), enc);
	}

	/** @returns the Contents of the File in the given Encoding */
    final static public String READ_FILE(File file, String enc) throws IOException {
        StringBuffer ret = new StringBuffer("");
        FileReader reader = new  FileReader(file, enc);
		char[] buffer = new char[BUFFER_SIZE];
		for(int len; 0 < (len = reader.read(buffer));) {
            ret.append(buffer, 0, len); }
        reader.close();
        return ret.toString(); }

	/** @return true when the File existed before the Timeout */
    final static public boolean WAIT_FOR_NEW_FILE(String fileName, long timeout) {
		return WAIT_FOR_NEW_FILE(new File(fileName), timeout); }

	/** @return true when the File existed before the Timeout */
    final static public boolean WAIT_FOR_FILE(String fileName, long timeout) {
		return WAIT_FOR_FILE(new File(fileName), timeout); }

	/** @return true when the File existed before the Timeout */
    final static public boolean WAIT_FOR_NEW_FILE(File file, long timeout) {
		file.delete();
		return WAIT_FOR_FILE(file, timeout); }

	/** @return true when the File existed before the Timeout */
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
	 * @return the File Contents when it appears before the Timeout
	 */
    final static public String SYNCH_READ_FILE(String fileName, long timeout, String encoding)
    throws java.io.IOException {
		return SYNCH_READ_FILE(new File(fileName), timeout, encoding); }

	/**
	 * @return the File Contents when it appears before the Timeout
	 */
    final static public String SYNCH_READ_FILE(File file, long timeout, String encoding)
    throws java.io.IOException {
		if (WAIT_FOR_FILE(file, timeout)) {
	        return READ_FILE(file, encoding); }
		return null; }

	/**
	 * @return the File Contents when it appears before the Timeout
	 * @deprecated, use FileReader.SYNCH_READ_NEW_FILE
	 */
    final static public String SYNCH_READ_NEW_FILE(String fileName, long timeout, String encoding)
    throws java.io.IOException {
		return SYNCH_READ_NEW_FILE(new File(fileName), timeout, encoding); }

	/**
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
