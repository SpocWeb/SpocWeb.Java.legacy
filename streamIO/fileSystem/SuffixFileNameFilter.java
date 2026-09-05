/*
 * File Name: SuffixFileNameFilter.java
 * Created on: 13.10.2003
 *
 */
package streamIO.fileSystem;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Implementation of the FileNameFilter
 * that accepts only those Files ending with a certain Suffix.
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
 * mtime: 2026-09-05T09:22:03Z
 * digest: ba1ce3c2600885a7f94570a9153ad71e8f422698aa486d73de7297e620ee7ae6
 * stale: false
 * tags: [code/file_filtering]
 * concepts: [File System, File I/O]
 * facets: {layer: infrastructure, status: stable, complexity: low}
 * -->
 */
public class SuffixFileNameFilter 
implements FilenameFilter {
	
	/** Reference to the Suffix against which to check the File Names */
	public String suffix;
	
	/** Initializing Constructor */
	public SuffixFileNameFilter(final String suffix_) {
		this.suffix = suffix_;
	}
	
	/** Accepts a File Name ending with {@link #suffix}.
	  * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)	 */
	public boolean accept(final File dir, final String name) {
		return name.endsWith(suffix);
	}
	
}
