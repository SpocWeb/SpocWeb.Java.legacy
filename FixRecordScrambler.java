import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
  * Title: FilterFind<p>
  *
  * Purpose:
  * Filters a streamIO and ends it 
  * as soon as a certain String is found 
  * more often than the specified Number. 
  * 
  * Design Decisions / Implementation Details:
  * Counter is integrated into this Class to save coupling Streams at different Levels.
  * Since Files are not only separated AFTER a certain String, 
  * but also possibly BEFORE or WITHIN the streamIO, thus it must read ahead. 
  * Searching is done primitive, using an O(N*M) Algorithm.
  * Can be improved later to use one of the more sophisticated Algorithms like Boyer-Moore. 
  *
  *
  * Known SubClasses: <none>
  *
  * otherwise related Classes: <none>
  *
  * Known Uses: 
  * for splitting structured Text Files. 
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	02-12-2003, 11:29 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class FixRecordScrambler {

	/**
	 * Writes the given Number of Bytes from is to os (bytewise). 
	 *
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
	 */
	final static public void OFFSET_FIELD(String filePath, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		OFFSET_FIELD(new File(filePath), recordSize, startOffset, fieldOffset, field, numRecs); }
		
	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 */
	final static public void OFFSET_FIELD(File file, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		File tmp  = new File(file.getAbsolutePath()+".new");
		OFFSET_FIELD(file, tmp, recordSize, startOffset, fieldOffset, field, numRecs);
		tmp.renameTo(file);
	}

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
	 */
	final static public void OFFSET_FIELD(String in_FilePath, String outFilePath, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		OFFSET_FIELD(
			new File(in_FilePath),
			new File(outFilePath), recordSize, startOffset, fieldOffset, field, numRecs); }

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
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
	 */
	final static public void OFFSET_FIELD(InputStream is, OutputStream os, int recordSize, int startOffset, int fieldOffset, String field, int numRecs) throws IOException {
		OFFSET_FIELD(is, os, recordSize, startOffset, fieldOffset, field.getBytes(), numRecs); }

	/**
	 * Scrambles a streamIO by ofsetting a certain Portion [fieldOffset, fieldOffset+fieldLenght]
	 * by a single recordSize per Record.
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
	 */
	final static public void OFFSET_FIELD(InputStream is, OutputStream os, int recordSize, int startOffset, int fieldOffset, int fieldLength, byte value, int numRecs) throws IOException {
		byte[] field = new byte[fieldLength];
		Arrays.fill(field, value);
		OFFSET_FIELD(is, os, recordSize, startOffset, fieldOffset, field, numRecs);
	}

////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods (not in Interfaces)
////////////////////////////////////////////////////////////////////////////

/** Tests all Methods of this Class	 */
public static void testIt(String[] args) { //throws java.io.IOException {
	System.out.println("Testing " + FixRecordScrambler.class.getName());
}

/**The main entry point for the application.
 *
 * @param args Array of parameters passed to the application
 * via the command line.	 */
public static void main (String[] args) throws java.io.IOException {
	if (args.length != 6) {
		System.out.println("Syntax: "+FixRecordScrambler.class.getName()+" inFilePath outFilePath recordSize startOffset fieldOffset fieldValue numRecords"); }
	OFFSET_FIELD(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5], Integer.parseInt(args[6])); }

}
