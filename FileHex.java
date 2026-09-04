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
 */
public class FileHex {

    public FileHex() {
    }

	/**just throws an Exception	*/
	public static void testEx() throws FileNotFoundException, IOException { 
		throw new IOException("Test"); }// FileNotFoundException("Test"); }
		
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
			IOException n = new IOException( "\n" + OS.toString ()); e.fillInStackTrace(); 
			throw n; } //shows how to propagate Exceptions with full Trace Information.
    }
}