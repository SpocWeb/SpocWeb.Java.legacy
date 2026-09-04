/**
 * File  Name: ResultSetToAttributes.java
 * Created on: 09.02.2003
 */
package technology.xml;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.xml.sax.Attributes;

import streamIO.exception.BaseException;
import streamIO.object.parser.jdbc.ResultSetToSax;

/**
 * Title: ResultSetToAttributes<p>
 * Description:
 * Purpose:
 * Represents the Fields/Columns of a JDBC ResultSet's current Row 
 * as a SAX XML Attributes Object. 
 * Also contains static Methods to convert other Objects into 
 * @see Attributes (which are read-only)
 * @see ResultSet
 * @see Map
 * @see HashMap
 * @see HashTable
 * @see Properties
 *
 * Known SubClasses: <none>
 *
 * Known Uses: <none>
 * @see ResultSetToSax
 *
 * Copyright:	Copyright (c) Matthias Heuer<p>
 * Company:	personal<p>
 * Created on	10-26-2002, 12:47 PM<p>
 * @author mheuer
 * @version	1.0
 *
 */
public class ResultSetToAttributes
implements Attributes {

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods for converting between Map, ResultSet and Properties
	////////////////////////////////////////////////////////////////////////////////
	
	/** 
	 * @param ret Properties Object to fill
	 * @param rs Resultset to read from, not advanded
	 * @return a new Properties Object filled with the Field Mappings of the given ResultSet
	 * @throws SQLException on any rs Exception
	 */
	final static public Properties MAP2PROPERTIES ( final Map map) throws SQLException {
		return MAP2PROPERTIES(new Properties(), map); 
	}
	
	/** Fills the given Properties Object with the Field Mappings of the given ResultSet
	 * 
	 * @param ret Properties Object to fill
	 * @param rs Resultset to read from, not advanded
	 * @return ret
	 * @throws SQLException on any rs Exception
	 */
	final static public Properties MAP2PROPERTIES
	( final Properties ret
	, final Map map
	) throws SQLException {
		for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
			final Map.Entry entry = (Map.Entry) iter.next();
			ret.setProperty(entry.getKey().toString(), entry.getValue().toString());
		}
		return ret; 
	}

	/** Updates the given ResultSet Object with the Field Mappings of the given Map 
	 * 
	 * @param map Map Object to read from 
	 * @param rs Resultset to write to, not advanded
	 * @throws SQLException
	 */
	final static public void MAP2RESULTSET
	( final ResultSet rs
	, final Map map
	) throws SQLException {
		for(final Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
			final Map.Entry entry = (Map.Entry) iter.next();
			rs.updateObject(entry.getKey().toString(), entry.getValue());
		}
	}

	/** Fills the given Map Object with the Field Mappings of the given ResultSet
	 * 
	 * @param map Map Object to fill
	 * @param rs Resultset to read from, not advanded
	 * @throws SQLException on any rs Exception
	 */
	final static public void RESULTSET2MAP
	( final Map map
	, final ResultSet rs
	) throws SQLException {
		final ResultSetMetaData rsMeta = rs.getMetaData();
		for (int i = rsMeta.getColumnCount(); --i >= 0;) {
			map.put(rsMeta.getColumnName(i), rs.getString(i));
		}
	}

	/** Fills the given Map Object with the Field Mappings of the given Properties
	 * 
	 * @param map Properties Object to fill
	 * @param props Properties to read from
	 */
	final static public void PROPERTIES2MAP
	( final Map map
	, final Properties props) {
		for (Enumeration keyEnum = props.keys(); keyEnum.hasMoreElements(); ) {
			final String key = (String) keyEnum.nextElement();
			map.put(key, props.getProperty(key));
		}
	}

	/** Fills the given Properties Object with the Field Mappings of the given ResultSet
	 * 
	 * @param rs Resultset to read from, not advanded
	 * @return a new Properties Object filled from the ResultSet Strings
	 * @throws SQLException on any rs Exception
	 */
	final static public Properties RESULTSET2PROPERTIES 
	( final ResultSet rs) throws SQLException {
		return RESULTSET2PROPERTIES(new Properties(), rs); 
	}
	
	/** Fills the given Properties Object with the Field Mappings of the given ResultSet
	 * 
	 * @param ret Properties Object to fill
	 * @param rs Resultset to read from, not advanded
	 * @return ret
	 * @throws SQLException on any rs Exception
	 */
	final static public Properties RESULTSET2PROPERTIES
	( final Properties ret
	, final ResultSet rs
	) throws SQLException {
		final ResultSetMetaData rsMeta = rs.getMetaData();
		for (int i = rsMeta.getColumnCount(); --i >= 0;) {
			ret.setProperty(rsMeta.getColumnName(i), rs.getString(i));
		}
		return ret; 
	}

	/** Updates the given Resultset Object with the Field Mappings of the given Properties 
	 * 
	 * @param props Properties Object read from
	 * @param rs Resultset to update, not advanded
	 * @throws SQLException on any rs Exception
	 */
	final static public void PROPERTIES2RESULTSET
	( final Properties props
	, final ResultSet rs
	) throws SQLException {
		for (Enumeration keyEnum = props.keys(); keyEnum.hasMoreElements(); ) {
			final String key = (String) keyEnum.nextElement();
			rs.updateString(key, props.getProperty(key));
		}
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods for writing Attributes into a Stream (see XMLWriter)
	////////////////////////////////////////////////////////////////////////////////
	
	/** writes out an Attribute Set into the given Writer 
	 * @see streamIO.object.parser.StreamOutXML for a correct Implementation
	 */
	final static public void WRITE_ATTRIBUTES(final Attributes atts, final Writer out) {
		WRITE_ATTRIBUTES(atts, new PrintWriter(out)); }

	/** writes out an Attribute into the given PrintWriter */
	final static public void WRITE_ATTRIBUTES(final Attributes atts, final PrintWriter out) {
		for (int i = atts.getLength(); --i >= 0;) {
			out.write(" " ); out.write(atts.getQName(i));
			out.write("='"); out.write(atts.getValue(i));
			out.write("'" );
		}
	}

	/** writes out an Attribute into the given OutputStream */
	public static void WRITE_ATTRIBUTES(Attributes atts, OutputStream out) {
		WRITE_ATTRIBUTES(atts, new PrintWriter(out)); }

	////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////
	
	/** URI returned by @see #getURI(int) */
	public String uri = ""; 
	
	/** Reference to the ResultSet	 */
	protected final ResultSet resultSet; 
	
	/** Reference to the optional ResultSet MetaData	 */
	protected final ResultSetMetaData rsMetaData; 

	////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super() (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Constructor for ResultSetToAttributes.
	 */
	public ResultSetToAttributes(final ResultSet resultSet_) {
		this.resultSet = resultSet_;
		try {
			rsMetaData = resultSet.getMetaData();
		} catch(SQLException x) {
			throw new BaseException(x);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Interface Attributes: Implementation
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @see org.xml.sax.Attributes#getLength()
	 */
	public int getLength() {
		try {
			return rsMetaData.getColumnCount();
		} catch(SQLException x) {
			throw new BaseException(x);
		}
	}

	/**
	 * QName = namespace-uri+localName
	 * @see org.xml.sax.Attributes#getURI(int)
	 */
	public String getURI(final int index) {
		return uri;
	}

	/**
	 * QName = namespace-uri+localName
	 * @see org.xml.sax.Attributes#getLocalName(int)
	 */
	public String getLocalName(final int index) {
		try {
			return rsMetaData.getColumnName(index);
		} catch(SQLException x) {
			throw new BaseException(x);
		}
	}

	/**
	 * QName = namespace-uri+localName
	 * @see org.xml.sax.Attributes#getQName(int)
	 */
	public String getQName(final int index) {
		try {
			String ret = rsMetaData.getColumnName(index).trim();
			if (ret.length() <= 0)
				ret = ""+index; 
			return uri+ret;
		} catch(SQLException x) {
			throw new BaseException(x);
		}
	}

	/**
	 * Look up an attribute's type by index. 
	 * The Attribute Type is one of the Strings (always in upper case)
	 * "CDATA", "ID", "IDREF", "IDREFS", "NMTOKEN", "NMTOKENS", "ENTITY", "ENTITIES", or "NOTATION".
	 * If the parser has not read a declaration for the attribute, 
	 * or if the parser does not report attribute types, 
	 * then it must return the value "CDATA" as stated in the XML 1.0 Recommentation 
	 * (clause 3.3.3, "Attribute-Value Normalization").
	 * 
	 * For an enumerated attribute that is not a notation, 
	 * the parser will report the type as "NMTOKEN".
	 * 
	 * @param ndex - The attribute index (zero-based). 
	 * @return the Attribute's Type as a String, or null if the Index is out of Range.
	 * @see getLength()
	 * @see org.xml.sax.Attributes#getType(int)
	 */
	public String getType(final int index) {
		return "CDATA";
	}

	/**
	 * @see org.xml.sax.Attributes#getValue(int)
	 */
	public String getValue(final int index) {
		try {
			return resultSet.getString(index);
		} catch(SQLException x) {
			throw new BaseException(x);
		}
	}

	/**
	 * @see org.xml.sax.Attributes#getIndex(String, String)
	 */
	public int getIndex(final String uri, final String localName) {
		if (( this.uri != uri) && 
			(!this.uri.equals(uri))) {
			return -1; }
		try {
			return resultSet.findColumn(localName);
		} catch(SQLException x) {
			throw new BaseException(x);
		}
	}

	/**
	 * @see org.xml.sax.Attributes#getIndex(String)
	 * @see java.sql.ResultSet#findColumn(String) which has the same Meaning and Interface
	 */
	public int getIndex(final String qName) {
		if (! qName.startsWith(uri)) {
			return -1; }
		return getIndex(uri, qName.substring(uri.length())); 
	}

	/**
	 * @see org.xml.sax.Attributes#getType(String, String)
	 */
	public String getType(final String uri, final String localName) {
		return getType(getIndex(uri, localName));
	}

	/**
	 * @see org.xml.sax.Attributes#getType(String)
	 */
	public String getType(final String qName) {
		return getType(getIndex(qName));
	}

	/**
	 * @see org.xml.sax.Attributes#getValue(String, String)
	 */
	public String getValue(final String uri, final String localName) {
		return getValue(getIndex(uri, localName));
	}

	/**
	 * @see org.xml.sax.Attributes#getValue(String)
	 */
	public String getValue(final String qName) {
		return getValue(getIndex(qName));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringWriter writer = new StringWriter(); 
		WRITE_ATTRIBUTES(this, writer); 
		return writer.toString();
	}
	
	

}
