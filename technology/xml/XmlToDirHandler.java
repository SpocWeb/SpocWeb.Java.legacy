/**
 * File  Name: XmlToDirHandler.java
 * Created on: 22.12.2002
 */
package technology.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Title: XmlToDirHandler<p>
 * Description:
 * Purpose:
 * Handler for the generic Sax Parser technology.xml.SaxDispatcher
 * Converts XML File Structures into File System Structures. 
 *
 * Design Decisions / Implementation Details:
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
public class XmlToDirHandler {

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants for the XML Grammar
	////////////////////////////////////////////////////////////////////////////////

	/// Attributes
	final static public String STR_ATTR_NAME = "name";
	final static public String STR_ATTR_CATEGORY = "MusicCategory";

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////

	/** temporary State on creating the Directory Structure */
	private File currDir;

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/** Constructor for XmlToDirHandler.	 */
	public XmlToDirHandler() {
		//this(workDir); 
	}

	/** Constructor for XmlToDirHandler.	 */
	public XmlToDirHandler(String baseDir) {
		this(new File(baseDir));
	}

	/** Constructor for XmlToDirHandler.	 */
	public XmlToDirHandler(final File baseDir) {
		currDir = baseDir; 
		if (!currDir.mkdirs()) {
			System.out.println("Directory was not created (already existed)"); 
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Methods, made public so they are accessible to Scripting and Reflection
	////////////////////////////////////////////////////////////////////////////

	//support for XML Databases
	
	/** joins the Variables with all given Attribute Names
	 * beneath a new Root Element given by the Attribute "root"
	 * @throws IOException
	 */
	public void Row(final Attributes atts) throws IOException {
		createDir(atts.getValue(STR_ATTR_CATEGORY)); 
	}
	
	//support for the DirList Output
	
	/** 'node' Child Element of 'join' */
	public File createDir(final String name) {
		final File file = new File(currDir, name);
		file.mkdirs();
		return file; 
	}
	
	/** 'node' Child Element of 'join' */
	public void Dir(final Attributes atts) {
		createDir(atts.getValue(STR_ATTR_NAME)); 
	}
	
	/** 'node' Child Element of 'join' 
	 * @throws IOException when the File could not be created. */
	public File createFile(final String name) throws IOException {
		final File file = new File(currDir, name);
		file.createNewFile(); 
		return file; 
	}
	
	/** stores the Node determined by the given qName in the given File
	 * @param node the Document or Element to store.
	 */
	public void File(final Attributes atts) throws SAXException, IOException {
		createFile(atts.getValue(STR_ATTR_NAME)); 
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods 
	////////////////////////////////////////////////////////////////////////////////

	/** Make the Dispatcher available to the Methods of this Class */
	private SaxDispatcher saxDispatcher = new SaxDispatcher(this, false);

	/** 
	 * Processes the XML Document at the given URI,  
	 * which describes which Documents to load, merge, transform and output. 
	 */
	public synchronized void process(final String uri)
		throws IOException, SAXException, ParserConfigurationException {
		//process(new File(uri));
		saxDispatcher.parse(uri);
	}

	/** 
	 * Processes the XML Document at the given URI,  
	 * which describes which Documents to load, merge, transform and output. 
	 */
	public synchronized void process(final File file)
		throws IOException, SAXException, ParserConfigurationException {
		saxDispatcher.parse(file);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing Methods 
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Demonstrates streaming a parsed XML File
	 */
	final static public void testIt()
		throws IOException, SAXException {
	}
	

	////////////////////////////////////////////////////////////////////////////
	/// #region : main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Doesn't work: the Xalan Trafo is too implicit to easily integrate a new SAX Source!
	 * Possibly easier to add a DOM Source? Needn't be parsed anymore, but doesn't work streaming!
	 */
	final static public void createFiles(final String[] args
	) throws IOException
	, SAXException
	, ParserConfigurationException {
		final String baseDir; 
		switch(args.length) { //read a single Document describing the Directories and Files 
			case 0: 
				System.out.println(
					"Syntax: \n "
						+ "java technology.xml.XmlToDirHandler <filesAndDirs.xml> \n"
						+ "java technology.xml.XmlToDirHandler <filesAndDirs.xml> <baseDir>\n");
				return; 
			case 1: //File only; Base Directory must have an absolute Path, 
				//otherwise the current Directory is used.
				//By default the classes in the java.io package always resolve relative pathnames against the current user directory. 
				//This directory is named by the system property user.dir, and is typically the directory in which the Java virtual machine was invoked.
				baseDir = System.getProperty("user.dir"); break; 
			case 2: baseDir = args[1]; break; //File and Base Directory 
			default: testIt(); return; 
		}
		new XmlToDirHandler(baseDir).process(args[0]); return;
	}

	/**
	 * @param args The FileList and the Base Directory. 
	 * ../../Databases/MusicCollection/Categories.xml 
	 * M:\\__Categories
	 */
	public static void main(String[] args
	) throws Exception { //
		System.out.println(args);
		createFiles(args);
	}

}
