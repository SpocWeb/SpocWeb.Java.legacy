/*
 * FileWriter.java
 *
 * Created on 25. November 2002, 14:22
 */

package streamIO.character;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author  MatthiasHeuer
 */
public class FileWriter
extends java.io.OutputStreamWriter
//implements IStreamOutByte
{

	/** Constant for UTF-8 Encoding */
	final static public String UTF_8 = "UTF-8";

	/** Constant for ISO-8859-1 Encoding */
	final static public String ISO_8859_1 = "ISO-8859-1";

	/** writes the String into the File using the given Encoding */
    final static public void WRITE_FILE(String fileName, String enc, String value) throws IOException {
		WRITE_FILE(new File(fileName), enc, value);
	}

	/** writes the String into the File using the given Encoding */
    final static public void WRITE_FILE(File file, String enc, String value) throws IOException {
        FileWriter writer = new FileWriter(file, enc);
		writer.write(value);
        writer.close(); }

	/** Creates a new instance of FileWriter */
	public FileWriter(File file) throws FileNotFoundException {
		super(new FileOutputStream(file));
	}

	/** Creates a new instance of FileWriter */
	public FileWriter(String fileName) throws FileNotFoundException {
		super(new FileOutputStream(fileName));
	}

	/** Creates a new instance of FileWriter */
	public FileWriter(String fileName, boolean append) throws FileNotFoundException {
		super(new FileOutputStream(fileName, append));
	}

	/** Creates a new instance of FileWriter */
	public FileWriter(FileDescriptor fdescr) {
		super(new FileOutputStream(fdescr));
	}

	/** Creates a new instance of FileWriter */
	public FileWriter(File file, String enc) throws FileNotFoundException, UnsupportedEncodingException {
		super(new FileOutputStream(file), enc);
	}

	/** Creates a new instance of FileWriter */
	public FileWriter(String fileName, String enc) throws FileNotFoundException, UnsupportedEncodingException {
		super(new FileOutputStream(fileName), enc);
	}

	/** Creates a new instance of FileWriter */
	public FileWriter(String fileName, boolean append, String enc) throws FileNotFoundException, UnsupportedEncodingException {
		super(new FileOutputStream(fileName, append), enc);
	}

	/** Creates a new instance of FileWriter */
	public FileWriter(FileDescriptor fdescr, String enc) throws UnsupportedEncodingException {
		super(new FileOutputStream(fdescr), enc);
	}

}
