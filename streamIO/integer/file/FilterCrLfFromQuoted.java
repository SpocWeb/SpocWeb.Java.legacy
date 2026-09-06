package streamIO.integer.file;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
  * TrimFilter.java
  * Throws all CR/LF Characters out of Quoted Sections in the File
  * 
  * Created on 3. April 2001, 00:43
  * 
  * @author  Matthias Heuer
  * @version
  * <!-- docstate
  * tags: [code/file_io, code/stream_io]
  * concepts: [File-Backed StreamIO Implementations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * -->
  */
public class FilterCrLfFromQuoted {
	
    /** Creates new TrimFilter */
    public FilterCrLfFromQuoted () {
    }
    
    protected static final String [] DEFAULT_ARGS = new String [] {
    		"D:\\Personal\\Databases\\MusicCollection\\Tracks.txt", 
			"D:\\Personal\\Databases\\MusicCollection\\Tracks1.txt"};
	/** Throws all CR/LF Characters out of Quoted Sections in the File.
	  * @param args the command line arguments
	  */
	public static void main (String[] args) throws FileNotFoundException, IOException {
		if (args.length == 0)
			args = DEFAULT_ARGS;
		final  FileInputStream FI = new  FileInputStream(args[0]);
		final FileOutputStream FO = new FileOutputStream(args[1]);
		try {
			boolean inside = false;
			for(int val; (val = FI.read ()) != -1;) {
				if (val == '"') {
					inside = !inside; FO.write (val); }
				else if((val == '\r') ||
						(val == '\n')) {
					if (! inside)
						FO.write (val); }
				else FO.write (val);
			}
		} finally {
			try { FO.close (); } finally { FI.close (); }
		}
	}
	
}
