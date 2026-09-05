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

/**
 * Castor-generated value object for the ZKDB "Kundenbeziehung" (customer relationship) XML type.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: f10c975b2919e1a13f614998a6f86a6a54626a500398952abb952746a0c84a1a
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class Kundenbeziehung 
extends ZKDBBaseType 
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Relationship type discriminator. */
	public ZKDBBaseType typ;

    /** ZKDB ID of the related customer. */
    public ZKDBBaseType ZKDBID;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty Kundenbeziehung with all fields unset. */
    public Kundenbeziehung() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Kundenbeziehung()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'typ'.
     * 
     * @return the value of field 'typ'.
    **/
    public ZKDBBaseType getTyp()
    {
        return this.typ;
    } //-- Typ getTyp() 

    /**
     * Returns the value of field 'ZKDBID'.
     * 
     * @return the value of field 'ZKDBID'.
    **/
    public ZKDBBaseType getZKDBID()
    {
        return this.ZKDBID;
    } //-- ZKDBID getZKDBID() 

    /**
     * Sets the value of field 'typ'.
     * 
     * @param typ the value of field 'typ'.
    **/
    public void setTyp(ZKDBBaseType typ)
    {
        this.typ = typ;
    } //-- void setTyp(Typ) 

    /**
     * Sets the value of field 'ZKDBID'.
     * 
     * @param ZKDBID the value of field 'ZKDBID'.
    **/
    public void setZKDBID(ZKDBBaseType ZKDBID)
    {
        this.ZKDBID = ZKDBID;
    } //-- void setZKDBID(ZKDBID) 

}
