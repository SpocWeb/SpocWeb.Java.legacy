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
 * A character-encoding file writer with a static helper for writing a whole String to a
 * file in one call, on top of the instance-level {@link java.io.OutputStreamWriter}
 * behavior its constructors set up.
 *
 * @author  MatthiasHeuer
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:09:21Z
 * digest: a61dc4343a9564fd7ca8bebfa9d7705c2505833c2b20d22bbe2eedb68566f70a
 * stale: false
 * tags: [code/file_io, code/encoding_handling]
 * concepts: [File I/O, Text Encoding]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
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
