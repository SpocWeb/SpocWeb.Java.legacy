import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/*
 * File Name: PropertyHierarchy.java
 * Created on: 02.06.2004
 *
 */

/**
 * Title: PropertyHierarchy<p>
 * Description:
 * Useful little Class to create nested Lists of Strings 
 * to parameterize your Application without resorting to Parsing XML. 
 * Also extends the Properties Class to be able resolve Property References, 
 * so Redundancies can be avoided, just like with ANT.  
 *
 * Similar Classes: 
 * @see java.util.Properties
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class PropertyHierarchy {
	
	/** Only static Methods to create a conventional Properties Object	 */
	private PropertyHierarchy() {}
	
	final static public String STR_PROPERTIES_SUFFIX = ".PROPERTIES"; 
	final static public String STR_PROPERTY_REF = "${";
	final static public String STR_PROPERTY_END = "}";
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void testIt() throws Exception {
		System.out.println("Usage: \n java PropertyHierarchy [FilePath]*");
		final Object[] arr = CONVERT_HIERARCHY(LOAD_HIERARCHY(PropertyHierarchy.class.getName()+STR_PROPERTIES_SUFFIX));
		System.out.println(arr);
	}
	
	/**Converts the nested Map Values into a nested Array. 
	 * But sometimes it is better to have both Keys and Values, 
	 * e.g. when using it on a nested XML DOM consisting of named Elements, 
	 * which again can contain named Elements. 
	 * @param props Map of Key/Value Pairs to be converted into an Array of Values. 
	 */
	final static public Object[] CONVERT_HIERARCHY(final Map props) {
		final Object[] ret = new Object[props.size()];
		int i = 0; 
		for(final Iterator iter = props.keySet().iterator(); iter.hasNext(); ++i) {
			final Object key = iter.next();
			final Object obj = props.get(key); //nullpointer Exception possible!
			if (obj == null) { //should not happen!?!
				continue; }
			if (obj instanceof Map) {
				ret[i] = CONVERT_HIERARCHY((Map) obj);
			} else {
				ret[i] = obj;
			}
		}
		return ret; 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	final static public Properties LOAD_HIERARCHY(final String path) throws FileNotFoundException, IOException {
		final Properties props = new Properties(); 
		props.load(new FileInputStream(path));
		for(final Iterator iter = props.keySet().iterator(); iter.hasNext();) {
			final Object key = iter.next();
			final Object obj = props.get(key); //nullpointer Exception possible!
			if (obj == null) { //should not happen!?!
				continue; }
			String val = obj.toString(); //nullpointer Exception possible!
			System.out.println(key+":"+val);
			for(int newPos, pos = 0; 0 <= (newPos = val.indexOf(STR_PROPERTY_REF, pos));) {
				final int nextPos = newPos+STR_PROPERTY_REF.length(); 
				final int endPos = val.indexOf(STR_PROPERTY_END, nextPos);
				if (endPos >= 0) {
					final String keyRef = val.substring(nextPos, endPos);
					String insert = props.getProperty(keyRef); 
					if (insert == null) { //or throw an Exception!
						//insert = ""; 
						throw new RuntimeException("Property Reference '"+keyRef+"' in File '"+path+"' not found");
					}
					val = val.substring(0, newPos)+insert+val.substring(endPos+STR_PROPERTY_END.length()); 
					props.put(key, val); 
					System.out.println(key+":"+val);
				} //not necessary otherwise, since replacing ALL Occurrences!
				pos = nextPos; //can be used to escape "${" using "${${}"
			}
			if (val != null) {
				if (val.toUpperCase().endsWith(STR_PROPERTIES_SUFFIX)) {
					props.put(key, LOAD_HIERARCHY(val));
				}
			}
		}
		return props; 
	}
	
	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(final String[] args) throws Exception {
		if ((args == null) || (args.length == 0)) {
			testIt();
		} else {
			for (int i = args.length; --i >= 0;) {
				LOAD_HIERARCHY(args[i]);
			}
		}
	}
	
}
