/*
 * File Name: SuffixFileNameFilter.java
 * Created on: 13.10.2003
 *
 */
package streamIO.fileSystem;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Title: SuffixFileNameFilter<p>
 * Description:
 * Purpose:
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
 */
public class SuffixFileNameFilter 
implements FilenameFilter {
	
	/** Reference to the Suffix against which to check the File Names */
	public String suffix;
	
	/** Initializing Constructor */
	public SuffixFileNameFilter(final String suffix_) {
		this.suffix = suffix_;
	}
	
	/** @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)	 */
	public boolean accept(final File dir, final String name) {
		return name.endsWith(suffix);
	}
	
}
