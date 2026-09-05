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
 * Castor-generated value object for the ZKDB "KundeInSystem" (customer-per-system) XML type.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: dc0b17ab239375509d0e55d7c9c35667ba77ed1dd8d3dca43090512ee493a666
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: broken, complexity: low}
 * -->
**/
public class KundeInSystem 
extends ZKDBBaseType 
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Record type discriminator. */
	public ZKDBBaseType typ;

    /** Flag blocking this customer in the given system. */
    public ZKDBBaseType sperre;

    /** Status of the customer within the given system. */
    public ZKDBBaseType systemStatus;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty KundeInSystem with all fields unset. */
    public KundeInSystem() {
        super();
    } //-- de.bahn.zkdb.bcm.data.KundeInSystem()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'sperre'.
     * 
     * @return the value of field 'sperre'.
    **/
    public ZKDBBaseType getSperre()
    {
        return this.sperre;
    } //-- Sperre getSperre() 

    /**
     * Returns the value of field 'systemStatus'.
     * 
     * @return the value of field 'systemStatus'.
    **/
    public ZKDBBaseType getSystemStatus()
    {
        return this.systemStatus;
    } //-- SystemStatus getSystemStatus() 

    // TODO: LOGIC: this accessor is named "ZKDBBaseType" instead of "getTyp" (compare the
    // sibling setTyp(ZKDBBaseType) below and every other class in this package's getTyp()) -
    // almost certainly a copy/rename mistake. Reflection-based access by naming convention
    // (as used elsewhere in this codebase, e.g. Accessor/SaxDispatcher) will not find a
    // "getTyp" method on this class.
    /**
     * Returns the value of field 'typ'.
     *
     * @return the value of field 'typ'.
    **/
    public ZKDBBaseType ZKDBBaseType()
    {
        return this.typ;
    } //-- Typ getTyp()

    /**
     * Sets the value of field 'sperre'.
     * 
     * @param sperre the value of field 'sperre'.
    **/
    public void setSperre(ZKDBBaseType sperre)
    {
        this.sperre = sperre;
    } //-- void setSperre(Sperre) 

    /**
     * Sets the value of field 'systemStatus'.
     * 
     * @param systemStatus the value of field 'systemStatus'.
    **/
    public void setSystemStatus(ZKDBBaseType systemStatus)
    {
        this.systemStatus = systemStatus;
    } //-- void setSystemStatus(SystemStatus) 

    /**
     * Sets the value of field 'typ'.
     * 
     * @param typ the value of field 'typ'.
    **/
    public void setTyp(ZKDBBaseType typ)
    {
        this.typ = typ;
    } //-- void setTyp(Typ) 

}
