import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

/**
 * Title:        FileHex<p>
 * Description:  prints out the Bytes of the File in the Command Line Parameter in Hex Notation<p>
 * Copyright:    Copyright (c) Matthias Heuer<p>
 * Company:      personal<p>
 * @author Matthias Heuer
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T09:55:04Z
 * digest: ea5d68e468aa151c89e39c7df559b35730ac2ca42eb825029fcdd41fa15cb0a2
 * stale: false
 * tags: [code/cli_tool, code/hex_encoding]
 * concepts: [File I/O]
 * facets: {layer: utility, status: stable, complexity: low}
 * -->
 */
public class FileHex {

    /** No state to initialize; all Members are static.
     *
     * <!-- docstate
     * tags: [code/cli_tool]
     * concepts: [File I/O]
     * facets: {layer: utility, status: stable, complexity: low}
     * -->
     */
    public FileHex() {
    }

	/**just throws an Exception
	 *
	 * <!-- docstate
	 * tags: [code/test_harness]
	 * concepts: [Error Handling]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	public static void testEx() throws FileNotFoundException, IOException {
		throw new IOException("Test"); }// FileNotFoundException("Test"); }

    /** Prints the Bytes of the File named in {@code args[0]} as space-separated decimal Values,
 * <!-- docstate
 * tags: [code/hex_encoding]
 * concepts: [File I/O]
 * facets: {layer: utility, status: stable, complexity: low}
 * -->
     * with a Line Break after every CR (13) Byte. */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        int chr;
        try {
//			testEx(); //just throws an Exception
			FileInputStream FIS = new FileInputStream(args[0]);
			while ((chr = FIS.read()) >= 0) {
				System.out.print(chr);
				System.out.print(' ');
				if (chr == 13) System.out.println(); }
		}
        catch (FileNotFoundException e) { System.out.println("File not Found:" + e.toString()); }
        catch (IOException e) { System.out.println("IO Error:" + e);
			ByteArrayOutputStream OS = new ByteArrayOutputStream(); e.printStackTrace(new PrintStream(OS));
			IOException n = new IOException( "\n" + OS.toString (), e); //chains the original Cause
			throw n; } //shows how to propagate Exceptions with full Trace Information.
    }
}