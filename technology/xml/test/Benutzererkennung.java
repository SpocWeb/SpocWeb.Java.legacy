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
 * Castor-generated value object for the ZKDB "Benutzererkennung" (user credential) XML type.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 64e663e0990affe6c5335fd12a8079d173017fda6b49073ff3d26bf6f53b1bda
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class Benutzererkennung 
extends ZKDBBaseType 
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Credential type discriminator. */
	public ZKDBBaseType typ;

    /** User name. */
    public ZKDBBaseType benutzername;

    /** PIN/password. */
    public ZKDBBaseType pin;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty Benutzererkennung with all fields unset. */
    public Benutzererkennung() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Benutzererkennung()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'benutzername'.
     * 
     * @return the value of field 'benutzername'.
    **/
    public ZKDBBaseType getBenutzername()
    {
        return this.benutzername;
    } //-- Benutzername getBenutzername() 

    /**
     * Returns the value of field 'pin'.
     * 
     * @return the value of field 'pin'.
    **/
    public ZKDBBaseType getPin()
    {
        return this.pin;
    } //-- Pin getPin() 

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
     * Sets the value of field 'benutzername'.
     * 
     * @param benutzername the value of field 'benutzername'.
    **/
    public void setBenutzername(ZKDBBaseType benutzername)
    {
        this.benutzername = benutzername;
    } //-- void setBenutzername(Benutzername) 

    /**
     * Sets the value of field 'pin'.
     * 
     * @param pin the value of field 'pin'.
    **/
    public void setPin(ZKDBBaseType pin)
    {
        this.pin = pin;
    } //-- void setPin(Pin) 

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
