import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
  * Title: FixRecordScrambler<p>
  *
  * Purpose:
  * Rewrites a fixed-size binary Record Stream, offsetting one Field within each Record
  * by a single Record Size per Record - i.e. Field N of Record I is moved to Record I+1 -
  * so a later Pass can align that Field across the whole File.
  *
  * Design Decisions / Implementation Details:
  * Works on flat, fixed-length binary Records rather than delimited Text, unlike its sibling
  * {@link FilterFind}.
  *
  * Known SubClasses: <none>
  *
  * otherwise related Classes:
  * @see FilterFind for splitting delimited Text Files instead of fixed-size binary Records
  *
  * Known Uses:
  * for splitting structured Text Files.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	02-12-2003, 11:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T09:55:23Z
  * digest: 1aa7ab87186cc7dfbd3dc4f8a271b92628d9754019e7df633156268b8f7a05ab
  * stale: false
  * tags: [code/cli_tool, code/fixed_width_reader]
  * concepts: [File I/O]
  * facets: {layer: utility, status: broken, complexity: medium}
  * -->
  */
public class FixRecordScrambler {

	/**
	 * Writes the given Number of Bytes from is to os (bytewise). 
	 *
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public void STREAM(InputStream is, OutputStream os, long length) throws IOException {
		for(long i = length; --i >= 0;) { //also blockwise or with buffered In and Out Streams
			int val; 
			if((val = is.read()) < 0) {
				break; }
			os.write(val);
		}
	}

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public void OFFSET_FIELD(String filePath, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		OFFSET_FIELD(new File(filePath), recordSize, startOffset, fieldOffset, field, numRecs); }
		
	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public void OFFSET_FIELD(File file, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		File tmp  = new File(file.getAbsolutePath()+".new");
		OFFSET_FIELD(file, tmp, recordSize, startOffset, fieldOffset, field, numRecs);
		tmp.renameTo(file);
	}

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public void OFFSET_FIELD(String in_FilePath, String outFilePath, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		OFFSET_FIELD(
			new File(in_FilePath),
			new File(outFilePath), recordSize, startOffset, fieldOffset, field, numRecs); }

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public void OFFSET_FIELD(File in_File, File outFile, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		FileInputStream  in_Stream = new  FileInputStream(in_File);
		FileOutputStream outStream = new FileOutputStream(outFile);
		OFFSET_FIELD(in_Stream, outStream, recordSize, startOffset, fieldOffset, field.getBytes(), numRecs); 
		STREAM(in_Stream, outStream, Long.MAX_VALUE);
		in_Stream.close();
		outStream.close();
	}

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public void OFFSET_FIELD(InputStream is, OutputStream os, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		OFFSET_FIELD(is, os, recordSize, startOffset, fieldOffset, field.getBytes(), numRecs); }

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: broken, complexity: medium}
	 * -->
	 */
	final static public void OFFSET_FIELD(InputStream is, OutputStream os, int recordSize, int startOffset, int fieldOffset, byte[] field, int numRecs) throws IOException {
		STREAM(is, os, startOffset+fieldOffset);
		byte[] record = new byte[recordSize-field.length];
		int recordLength;
		int  fieldLength = field.length;
		for(int i = numRecs; --i >= 0;) {
			os.write(field , 0,  fieldLength); //order reversed: first write, then read
			fieldLength  = is.read (field);
			recordLength = is.read(record); //normal order: first read, then write
			os.write(record, 0, recordLength);
			if (fieldLength < field .length) {
				break; }
			if(recordLength < record.length) {
				break; }
		}
	}

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 */
//	final static public void OFFSET_FIELD(InputStream is, OutputStream os, int recordSize, int startOffset, int fieldOffset, int fieldLength, byte value) throws IOException {

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 * <!-- docstate
	 * tags: [code/fixed_width_reader]
	 * concepts: [File I/O]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public void OFFSET_FIELD(InputStream is, OutputStream os, int recordSize, int startOffset, int fieldOffset, int fieldLength, byte value, int numRecs) throws IOException {
		byte[] field = new byte[fieldLength];
		Arrays.fill(field, value);
		OFFSET_FIELD(is, os, recordSize, startOffset, fieldOffset, field, numRecs);
	}

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class
 *
 * <!-- docstate
 * tags: [code/test_harness]
 * concepts: [Testing]
 * facets: {layer: utility, status: stable, complexity: low}
 * -->
 */
public static void testIt(String[] args) { //throws java.io.IOException {
	System.out.println("Testing " + FixRecordScrambler.class.getName());
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * <!-- docstate
 * tags: [code/cli_tool]
 * concepts: [File I/O]
 * facets: {layer: utility, status: broken, complexity: low}
 * -->
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	if (args.length != 7) {
		System.out.println("Syntax: "+FixRecordScrambler.class.getName()+" inFilePath outFilePath recordSize startOffset fieldOffset fieldValue numRecords");
		return; }
	OFFSET_FIELD(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6])); }

}
