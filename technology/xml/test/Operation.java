/*
 * This class was automatically generated with 
 * <a href="http://castor.exolab.org">Castor 0.9.3.9+</a>, using an
 * XML Schema.
 * $Id$
 */

package technology.xml.test;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.util.Hashtable;

/**
 * Castor-generated typesafe enumeration for the ZKDB "Operation" XML type: marks a Transaktion
 * as C(reate), D(elete), S, E, or the uninitialized-message marker X.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 897aa8bc4d355c10a02576020a4095ca1ef3ed576a392afd47229134baba282c
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class Operation 
implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The C type
    **/
    final static public int C_TYPE = 0;

    /**
     * The instance of the C type
    **/
    final static public Operation C = new Operation(C_TYPE, "C");

    /**
     * The D type
    **/
    final static public int D_TYPE = 1;

    /**
     * The instance of the D type
    **/
    final static public Operation D = new Operation(D_TYPE, "D");

    /**
     * The E type
    **/
    final static public int E_TYPE = 2;

    /**
     * The instance of the E type
    **/
    final static public Operation E = new Operation(E_TYPE, "E");

    /**
     * The S type
    **/
    final static public int S_TYPE = 3;

    /**
     * The instance of the S type
    **/
    final static public Operation S = new Operation(S_TYPE, "S");

    /**
     * The X type
    **/
    final static public int X_TYPE = 4;

    /**
     * The instance of the X type
    **/
    final static public Operation X = new Operation(X_TYPE, "X");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private Operation(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- de.bahn.zkdb.bcm.data.types.Operation(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * Operation
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this Operation
    **/
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
    **/
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("C", C);
        members.put("D", D);
        members.put("E", E);
        members.put("S", S);
        members.put("X", X);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this Operation
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new Operation based on the given String value.
     * 
     * @param string
    **/
    public static Operation valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid OperationType";
            throw new IllegalArgumentException(err);
        }
        return (Operation) obj;
    } //-- de.bahn.zkdb.bcm.data.types.Operation valueOf(java.lang.String) 

}
