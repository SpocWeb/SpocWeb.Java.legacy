/*
 * File Name: DamlHandler.java
 * Created on: 18.08.2003
 *
 */
package technology.xml;

import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import math.vector.VectorString;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import streamIO.Log;

/**
 * Title: DamlHandler<p>
 * Description:
 * Purpose:
 * Handles DAML Syntax from an XmlReader and writes out relational Data. 
 *
 * Design Decisions / Implementation Details:
 * The current Context collects the Details of different Tables 
 * (Entity, Relation, etc.)
 * and writes them out as soon as the wrapping Element ends. 
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
public class DamlHandler {

	final static public char SEPARATOR = '\t';

	/**
	 * 
	 * @param atts
	 * @return the rdf:resource Attribute trimmed by the first '#' Character
	 */
	final static public String getResource(final Attributes atts) {
		return atts.getValue("rdf:resource").substring(1); //trim the first '#' Character 
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Member Variables
	////////////////////////////////////////////////////////////////////////////

	/** Collects the current Relation Record 
	 * ID, Relation, Subject, Object
	 */
	private final FileWriter tripleOut;

	/** Collects the current Entity Record:
	 *  ID, instanceOf, URI, Description, Icon, Status
	 */
	private final FileWriter entityOut;

	/** Collects the current Entity Record:
	 *  ID, instanceOf, URI, Description, Icon, Status
	 */
	private final FileWriter relationOut;

	private String entityID;

	private String instanceOf;

	private String comment;

	/// for Binary Relations (Attributes)

	/** Definitionsbereich = Subject Range */
	private String domain;

	/** Wertebereich = Object Range */
	private String range;

	/**
	 * Constructor
	 * @param entityPath
	 * @param relationPath
	 * @throws IOException
	 */
	public DamlHandler(final String entityPath, final String triplePath, final String relationPath)
		throws IOException {
		relationOut = new FileWriter(relationPath);
		tripleOut = new FileWriter(triplePath);
		entityOut = new FileWriter(entityPath);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Helper Methods
	////////////////////////////////////////////////////////////////////////////

	/** initializes the Member Variables */
	private void initMembers(final String entityID_) {
		this.entityID = entityID_;
		instanceOf = "";
		comment = "";
	}

	/** writes an Entity Record into its Stream	 */
	public void writeEntity(final String instanceOfDefault) throws IOException {
		if ((instanceOf == null) || (instanceOf.length() == 0)) {
			instanceOf = instanceOfDefault;
		}
		writeEntity(entityID, instanceOf, comment);
	}

	/** writes an Entity Record into its Stream	 */
	public void writeEntity(
		final String entityID_,
		final String instanceOf_,
		final String comment_)
		throws IOException {
		entityOut.write(entityID_);
		entityOut.write(SEPARATOR);
		entityOut.write(instanceOf_);
		entityOut.write(SEPARATOR);
		entityOut.write(SEPARATOR);
		entityOut.write(comment_);
		entityOut.write(SEPARATOR);
		entityOut.write(SEPARATOR);
		entityOut.write('1');
		//entityOut.write(SEPARATOR);
		//entityOut.write(SEPARATOR);
		//entityOut.write(SEPARATOR);
		entityOut.write("\r\n");
	}

	final static public int ENTITY_ID_LENGTH = 50;

	final static public int TRIPLE_ID_LENGTH = ENTITY_ID_LENGTH / 3;

	/** writes a Triple Record (Relation Element), for the current Entity  
	 * used e.g. by rdfs:subClassOf
	 * ID, Relation, Subject, Object
	 * 
	 * @param object 
	 * @param relation
	 * @throws IOException
	 */
	private void writeTriple(final String object, final String relation) throws IOException {
		final String tripleID =
			VectorString.TRIM_LENGTH(
				VectorString.TRIM_LENGTH(entityID, TRIPLE_ID_LENGTH)
					+ '.'
					+ VectorString.TRIM_LENGTH(relation, TRIPLE_ID_LENGTH)
					+ '.'
					+ object,
				ENTITY_ID_LENGTH);
		assert null != Log.L("object=" + object + "; relation=" + relation + ";"); //+";="
		tripleOut.write(tripleID);
		tripleOut.write(SEPARATOR);
		tripleOut.write(entityID);
		tripleOut.write(SEPARATOR);
		tripleOut.write(relation);
		tripleOut.write(SEPARATOR);
		tripleOut.write(object);
		tripleOut.write(SEPARATOR);
		tripleOut.write('1');
		tripleOut.write("\r\n");
		//writeEntity(tripleID, "Triple", ""); //only needed when trying to talk about Triples (Meta, e.g. Proofs etc.)
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : Syntax Methods, made public so they are accessible to Scripting and Reflection
	////////////////////////////////////////////////////////////////////////////

	/** rdfs:subClassOf
	 * may occur several Times!
	 * ID, Relation, Subject, Object
	 * @param atts
	 */
	public void subClassOf(final Attributes atts) throws IOException {
		final String object = getResource(atts);
		writeTriple(object, "isSubClassOf");
	}

	/** rdfs:subClassOf
	 * may occur several Times!
	 * ID, Relation, Subject, Object
	 * @param atts
	 */
	public void subPropertyOf(final Attributes atts) throws IOException {
		final String object = getResource(atts);
		writeTriple(object, "isSubRelationOf");
	}

	/** rdfs:Class
	 * @param atts
	 */
	public void Class(final Attributes atts) {
		initMembers(atts.getValue("rdf:ID"));
	}

	/** rdfs:Class
	 *  ID, instanceOf, URI, Description, Icon, Status
	 * @param atts
	 */
	public void Class() throws IOException {
		writeEntity("Class"); //"SetOrClass"); 
	}

	/**
	 * daml:ObjectProperty binary Attribute Relations 
	 * @param atts rdf:ID 
	 */
	public void ObjectProperty(final Attributes atts) {
		initMembers(atts.getValue("rdf:ID"));
	}

	/**
	 * daml:ObjectProperty binary Attribute Relations 
	 * unfortunately the DAML File specifies several rdf:type Elements
	 * for each Property, which is non-canonical. 
	 * Format: EntityID RangeSubject= RangeObject= Arity=2 Cardinality=0
	 * @param atts rdf:ID 
	 */
	public void ObjectProperty() throws IOException {
		writeEntity("Relation"); //"SetOrClass");
		//write out the Relation Record 
		relationOut.write(entityID);
		relationOut.write(SEPARATOR);
		relationOut.write(domain);
		relationOut.write(SEPARATOR);
		relationOut.write(range);
		relationOut.write(SEPARATOR);
		relationOut.write("2"); //Properties are binary Relations
		relationOut.write(SEPARATOR);
		relationOut.write("0");
		relationOut.write("\r\n");
	}

	/**
	 * rdfs:domain binary Attribute Relations 
	 * unfortunately the DAML File specifies several rdf:type Elements
	 * for each Property, which is non-canonical. 
	 * @param atts rdf:resource 
	 */
	public void domain(final Attributes atts) throws IOException {
		domain = getResource(atts);
	}

	/**
	 * rdfs:range binary Attribute Relations 
	 * unfortunately the DAML File specifies several rdf:type Elements
	 * for each Property, which is non-canonical. 
	 * @param atts rdf:resource
	 */
	public void range(final Attributes atts) throws IOException {
		range = getResource(atts);
	}

	/**Description of Classes and Instances Start Element
	 * @param atts
	 */
	public void Description(final Attributes atts) {
		initMembers(atts.getValue("rdf:ID"));
	}

	/** 
	 * Description End Element
	 */
	public void Description() throws IOException {
		writeEntity("Entity");
	}

	/** 
	 * Typing of Classes and Instances
	 * may occur several Times! But this defies a canonical Structure! 
	 * @param atts
	 */
	public void type(final Attributes atts) {
		instanceOf = getResource(atts);
	}

	/** rdfs:comment of Classes and Instances
	 * @param atts
	 */
	//public void comment(final Attributes atts) { }

	/** rdfs:comment
	 * is being stripped from multiple WhiteSpace and CR/LFs to allow for a tabular Format. 
	 * @param atts
	 */
	public void comment() {
		comment = saxDispatcher.buffer.toString();
		final StringBuffer sb = VectorString.NORMALIZE(comment, 0);
		if (sb.length() > 255)
			sb.setLength (255);
		comment = sb.toString(); 
	}

	/** rdf:RDF
	 * @param atts
	 */
	public void RDF() throws IOException {
		relationOut.close();
		tripleOut.close();
		entityOut.close();
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods 
	////////////////////////////////////////////////////////////////////////////////

	/** Make the Dispatcher available to the Methods of this Class */
	private SaxDispatcher saxDispatcher = new SaxDispatcher(this);

	/** 
	 * Processes the XML Document at the given URI,  
	 * which describes which Documents to load, merge, transform and output. 
	 */
	public synchronized void process(final String uri)
		throws IOException, SAXException, ParserConfigurationException {
		saxDispatcher.parse(uri);
	}

	////////////////////////////////////////////////////////////////////////////
	/// #region : main() Methods (not in Interfaces)
	////////////////////////////////////////////////////////////////////////////

	/**
	 * @param args URLs to indicate the Input(args[0]), TrafoXSL(args[1]), Output(args[2])
	 * The URLs can also be absolute or relative FileSystem Paths! ^
	 * e.g. java technology.xml.XslTrafo
	 * "E:\Personal\Code\XSL\Music\example\Seal Second 06 Kiss_from_a_Rose.xml"
	 * E:\Personal\Code\XSL\Music\example\SongStyle.xsl
	 * E:\Personal\Code\xsl\Music\example\output.html
	 */
	public static void main(String[] args) throws Exception { //
		parse(args);
	}

	/**
	 */
	final static public void parse(String[] args)
		throws IOException, SAXException, ParserConfigurationException {
		if (args.length == 4) { //read a single Document describing the Trafos 
			new DamlHandler(args[1], args[2], args[3]).process(args[0]);
			return;
		}
		System.out.println(
			"Syntax: \n "
				+ "java technology.xml.DamlHandler file.daml entities.tab triples.tab relations.tab \n");
	}

}
