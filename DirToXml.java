import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * File Name: DirToXML.java
 * Created on: 10.06.2003
 *
 */

/**
 * Title: DirToXml<p>
 * Description:
 * Purpose:
 * Creates an XML File with the Files nested correctly in Directories. 
 * To convert a File System into a nested Tab-Separated File Format 
 * you can use the DOS Tree Command and later replace the Graphics by Tabs. 
 *
 * Known SubClasses: <none>
 *
 * Known Uses: 
 * storing CD Information into Files for easier Searching 
 * @see technology.xml.XmlToDirHandler
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class DirToXml {

	/** String prepended to nested Output	 */
	public static String indent = "\t";
	
	/**
	 * Recursively applies the same Command to every File in all Subdirectories
	 * @param cmd the Executable inclusive Path and all Parameters, except the File
	 * @param dir the Directory to start from and recourse into
	 * @param patterns the List of File Suffixes to search for
	 */
	final static public void execRecursive(final File dir, final PrintWriter stream, final String[] patterns) 
		throws IOException, InterruptedException {
		execRecursive(dir, stream, patterns, ""); 
	}
	
	/**
	 * Recursively applies the same Command to every File in all Subdirectories
	 * @param cmd the Executable inclusive Path and all Parameters, except the File
	 * @param dir the Directory to start from and recourse into
	 * @param patterns the List of File Suffixes to search for
	 */
	final static public void execRecursive(final File dir, final PrintWriter stream, final String[] patterns, final String prefix)
		throws IOException, InterruptedException {
		//System.out.println("cmd='" + cmd + "'");
		//System.out.println("dir='" + dir + "'");
		//System.out.println("pattern='" + pattern + "'");
		if (!dir.isDirectory()) 
			throw new RuntimeException("The given Path is not a Directory!");
		stream.print(prefix); 
		stream.print("<Dir name='"); 
		stream.print(dir.getName()); 
		stream.println("'>"); //Attributes are less sensitive to special Characters!
		final String[] files = dir.list();
		final String newPrefix = indent+prefix;
		final String path = dir.getPath();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd'T'hh:MM:ss"); //.sssZ");
		for (int i = files.length; --i >= 0;) {
			final String fName = files[i];
			//System.out.println("testing " + fileName);
			final File file = new File(path, fName);
			if (file.isDirectory()) {
				//System.out.println("recursing " + fileName);
				execRecursive(file, stream, patterns, newPrefix);
			} else {
				boolean found = (patterns.length <= 2);
				if (! found) {
					final int len = fName.length(); 
					for (int j = patterns.length; --j >= 2;) { //don't check the first Arg
						final String pattern = patterns[j];
						if ((len > pattern.length()) && 
							(pattern.equalsIgnoreCase(fName.substring(fName.length()-pattern.length())))) {
							found = true; 
							break; 
						}
					}
				}
				if (found) {
					stream.print(newPrefix); 
					stream.print("<File name='"); //Attributes are less sensitive to special Characters!
					stream.print(fName);
					stream.print("' size='"); //
					stream.print(file.length());
					stream.print("' date='"); //
					stream.print(sdf.format(new Date(file.lastModified())));
					stream.println("'/>");
				}
			}
		}
		stream.print(prefix); 
		stream.println("</Dir>"); //Attributes are less sensitive to special Characters!
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) {
		PrintWriter writer = new PrintWriter(System.out); 
		//writer = new PrintWriter(new FileWriter(args[1], true)); 
		try { //append to File
			if (args.length > 1) {
				File file = new File(args[1]); 
				if (file.exists()) {
					//final PrintWriter  error = new PrintWriter(System.err); 
					writer.println("File already exists! Cancel, Overwrite or Append?"); 
					char chr = ((char)System.in.read()); //.ToUpper();
					if (chr == 'c') 
						return; 
					if (chr == 'o') 
						file.delete(); 
				}
				writer = new PrintWriter(new FileWriter(file));
			}
			File dir = new File(args[0]); 
			execRecursive(dir, writer, args); 
		} catch (final Throwable x) { 
			System.err.println("Usage: java " + DirToXml.class.getName() + " <Path> <PathToOutputFile> [List of Suffixes]");
			if (x instanceof IOException) {
				System.err.println("Make sure you use the full Path or the correct relative Path to the *.exe!");
			}
			x.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close(); 
			}
		}
	}
}
