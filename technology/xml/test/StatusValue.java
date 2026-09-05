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
 * Castor-generated typesafe enumeration for the ZKDB "StatusValue" XML type, the base type
 * for the frequently-recurring status attributes (CHANGED, DELETED, UNCHANGED, ERROR).
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 61b78b2060a4ab039b8cfccf0a6c86ef77edc5969f573f2d67eae414d4f767a3
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class StatusValue 
implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The changed type
    **/
    final static public int CHANGED_TYPE = 0;

    /**
     * The instance of the changed type
    **/
    final static public StatusValue CHANGED = new StatusValue(CHANGED_TYPE, "changed");

    /**
     * The deleted type
    **/
    final static public int DELETED_TYPE = 1;

    /**
     * The instance of the deleted type
    **/
    final static public StatusValue DELETED = new StatusValue(DELETED_TYPE, "deleted");

    /**
     * The unchanged type
    **/
    final static public int UNCHANGED_TYPE = 2;

    /**
     * The instance of the unchanged type
    **/
    final static public StatusValue UNCHANGED = new StatusValue(UNCHANGED_TYPE, "unchanged");

    /**
     * The error type
    **/
    final static public int ERROR_TYPE = 3;

    /**
     * The instance of the error type
    **/
    final static public StatusValue ERROR = new StatusValue(ERROR_TYPE, "error");

    private static java.util.Hashtable _memberTable = init();

    private int type = -1;

    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private StatusValue(int type, java.lang.String value) {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- de.bahn.zkdb.bcm.data.types.StatusValue(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns an enumeration of all possible instances of
     * StatusValue
    **/
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Returns the type of this StatusValue
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
        members.put("changed", CHANGED);
        members.put("deleted", DELETED);
        members.put("unchanged", UNCHANGED);
        members.put("error", ERROR);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Returns the String representation of this StatusValue
    **/
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Returns a new StatusValue based on the given String
     * value.
     * 
     * @param string
    **/
    public static StatusValue valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid StatusValueType";
            throw new IllegalArgumentException(err);
        }
        return (StatusValue) obj;
    } //-- de.bahn.zkdb.bcm.data.types.StatusValue valueOf(java.lang.String) 

}
