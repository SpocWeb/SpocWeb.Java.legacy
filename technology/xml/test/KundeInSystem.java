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
 * 
 * ============  KUNDEINSYSTEM TYPE ==================	
 * 			
 * 
 * @version $Revision$ $Date$
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

	public ZKDBBaseType typ;

    public ZKDBBaseType sperre;

    public ZKDBBaseType systemStatus;


      //----------------/
     //- Constructors -/
    //----------------/

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
