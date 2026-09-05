/*
 * File Name: DirectoryFilter.java
 * Created on: 18.10.2003
 *
 */
package streamIO.fileSystem;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Filters Files, so that only Directories are returned.
 *
 * Can be a Singleton, as long as the Code is Thread safe
 *
 * Known SubClasses: <none>
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
 * mtime: 2026-09-05T09:21:54Z
 * digest: 0734be025e87c733d435bf8bbcfc084a8b7ccff5a62071b3a1324472ab701ec1
 * stale: false
 * tags: [code/file_filtering]
 * concepts: [File System, File I/O]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
 */
public class DirectoryFilter 
implements FilenameFilter, FileFilter {

	/** Singleton Instance of this Class */
	final static public DirectoryFilter FILTER = new DirectoryFilter(); 

	/** private Constructor to enforce Singleton use */
	private DirectoryFilter() {};

	/** Accepts a Directory Entry named within the given parent Directory.
	  * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)	 */
	public boolean accept(final File dir, final String name) {
		return new File(dir, name).isDirectory(); }

	/** Accepts a File denoting a Directory.
	  * @see java.io.FileFilter#accept(java.io.File)	 */
	public boolean accept(final File pathname) {
		return pathname.isDirectory(); }

}
