import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import streamIO.integer.IStreamOutByte;

/*
 * Created on 10.08.2004
 *
 * Copies rapidly as much as possible from a Stream. 
 * This means you have to read bytewise, not blockwise. 
 */

/**
 * Copies rapidly as much as possible from a Stream. 
 * This means you have to read bytewise, not blockwise. 
 * @see streamIO.integer.pipe.ByteStreamerThread which does the same by creating a new Thread. 
 * @author heuerm
 * <!-- docstate
 * pass: 2
 * mtime: 2008-06-29T19:05:35Z
 * digest: c32b0a503ce739019039971c44a74130e5d16424b1e8fa01098e600a6d150c92
 * stale: false
 * tags: [code/cli_tool, code/file_transfer]
 * concepts: [File Copying]
 * facets: {layer: utility, status: stable, complexity: low}
 * -->
 */
public class CopyAllPossible {

	/**
	 * Main Method to be called from the Command Line
	 * @param args <InputFile/Folder> <OutputFile/Folder> [ChunkSize=1 [SuffixPattern='']]"
	 * @throws IOException
	 * <!-- docstate
	 * tags: [code/cli_tool]
	 * concepts: [File Copying]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	public static void main(final String[] args) throws IOException {
		switch (args.length) {
		case 2: COPY(args[0], args[1], 1, ""); break;
		case 3: COPY(args[0], args[1], Integer.parseInt(args[2]), ""); break;
		case 4: COPY(args[0], args[1], Integer.parseInt(args[2]), args[3]); break;
		default: System.out.println("Syntax: java CopyAllPossible <InputFile/Folder> <OutputFile/Folder> [ChunkSize=1 [SuffixPattern='']]"); 
		}
	}
	/** Copies the whole In Directory Chunk-wise into the Out Directory
	 *
	 * <!-- docstate
	 * tags: [code/directory_traversal]
	 * concepts: [File Copying]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public long COPY(final String in, final String out, int ChunkSize, final String pattern) throws IOException {
		File in_File = new File(in);
		File outFile = new File(out);
		long retVal = 0; 
		if (in_File.isDirectory()) {
			outFile.mkdirs(); 
			String[] files = in_File.list();
			for (int i = files.length; --i >= 0;) {
				String in_FileName = in_File.getPath() + "\\" + files[i];
				String outFileName = outFile.getPath() + "\\" + files[i];
				retVal += COPY(in_FileName, outFileName, ChunkSize, pattern);
			}
		} else if (in.endsWith(pattern)) {
			retVal = STREAM(in_File.getAbsolutePath(), outFile.getAbsolutePath(), ChunkSize);
		} 
		return retVal; 
	}


	/**Streams the whole InputStream Chunk-wise into the OutputStream
	 *
	 * <!-- docstate
	 * tags: [code/file_transfer]
	 * concepts: [File Copying]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public long STREAM(final String in, final String out, int ChunkSize) throws IOException {
		final InputStream  inStr; 
		OutputStream outStr = null; 
		try {
			inStr  = new FileInputStream(in);
			outStr = new FileOutputStream(out);
			return STREAM(inStr, outStr, new byte[ChunkSize]); 
		} catch(IOException x) {
			if (outStr != null) 
				outStr.close(); 
			System.out.println(x); 
			throw x; 
		}
	}

	/**Streams the whole InputStream Chunk-wise into the OutputStream
	 *
	 * <!-- docstate
	 * tags: [code/file_transfer]
	 * concepts: [File Copying]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 */
	final static public long STREAM(final InputStream in, final OutputStream out, int ChunkSize) throws IOException {
		return STREAM(in, out, new byte[ChunkSize]); }

	/**Streams the whole InputStream Chunk-wise into the OutputStream 
	 * copied from
	 * <!-- docstate
	 * tags: [code/file_transfer]
	 * concepts: [File Copying]
	 * facets: {layer: utility, status: stable, complexity: low}
	 * -->
	 * @see streamIO.integer.pipe.ByteStreamerThread#STREAM(InputStream, IStreamOutByte, byte[]) */
	final static public long STREAM(final InputStream in, final OutputStream out, final byte[] chunk) throws IOException {
		long ret = 0;
		for (int size; 0 < (size = in.read(chunk)); ret += size) {
			out.write (chunk, 0, size); } //Stop when the last Chunk was not full
		out.flush();
		return ret; }

}
