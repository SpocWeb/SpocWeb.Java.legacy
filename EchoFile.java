import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.SortedMap;

/*
 * File Name: EchoFile.java
 * Created on: 19.01.2004
 *
 */

/**
 * Title: EchoFile<p>
 * Description:
 * Purpose:
 * This Class prints out the Contents of the given File to it's Output Stream 
 * using the given Encoding (DOS is the Default). 
 *
 * Known Uses: <none>
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:54:52Z
 * digest: 7e0933ef7b03ad18f3399b92d16585cfab4e8e00a87376f9c8f40fe4d8d7a1b5
 * stale: false
 * tags: [code/cli_tool, code/text_encoding]
 * concepts: [Text Encoding]
 * facets: {layer: utility, status: stable, complexity: low}
 * -->
 */
public class EchoFile {

	/**	 */
	private EchoFile() { }

	/** Default Encoding used when none is given on the Command Line. */
	final static public String CP437 = "Cp437";
	
	/** Tests all Methods of this Class
	 *
	 * <!-- docstate
	 * tags: [code/text_encoding]
	 * concepts: [Text Encoding]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	public static void echoFile(final String fileName, final String encoding) throws Exception {
		System.out.println("Supported Encodings:");
		final SortedMap charSets = Charset.availableCharsets(); 
		for (final Iterator iter = charSets.keySet().iterator(); iter.hasNext();) {
			System.out.println(iter.next()); }
		if (Charset.isSupported(encoding)) {
			Charset.forName(encoding); 
		} else { 
			System.out.println("Charset not supported:"+encoding); 
		}
		final Reader fr = new InputStreamReader(new FileInputStream(fileName), encoding);
		for(int chr; -1 != (chr = fr.read()); ) {
			System.out.print((char) chr); 
		} 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * <!-- docstate
	 * tags: [code/cli_tool]
	 * concepts: [Text Encoding]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 * via the command line.	 */
	public static void main(String[] args) throws Exception {
		switch (args.length) {
			//case 0 : args = new String[] {"./EncodingTest.TXT", CP437}; break;
			case 1 : args = new String[] {args[0], CP437}; break;
			case 2 : args = new String[] {args[0], args[1]}; break;
			default: System.out.println("Syntax: java EchoFile <filePath> [encoding=CP437]"); return;
		}
		echoFile(args[0], args[1]);
	}

}
