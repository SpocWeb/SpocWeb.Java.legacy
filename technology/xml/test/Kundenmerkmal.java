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
 * =============  KUNDENMERKMAL TYPE ==================	
 * 			
 * 
 * @version $Revision$ $Date$
**/
public class Kundenmerkmal 
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

    /**
     * Im Trafo defaulted auf 'NVS', sollte aus DB und XML
     * entfallen. Kann nicht Pflichtfeld sein, obwohl DB darauf
     * achtet; dies sollte entfernt werden.
    **/
    public ZKDBBaseType sys_extern;

    public ZKDBBaseType merkmal;


      //----------------/
     //- Constructors -/
    //----------------/

    public Kundenmerkmal() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Kundenmerkmal()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'merkmal'.
     * 
     * @return the value of field 'merkmal'.
    **/
    public ZKDBBaseType getMerkmal()
    {
        return this.merkmal;
    } //-- Merkmal getMerkmal() 

    /**
     * Returns the value of field 'sys_extern'. The field
     * 'sys_extern' has the following description: Im Trafo
     * defaulted auf 'NVS', sollte aus DB und XML entfallen. Kann
     * nicht Pflichtfeld sein, obwohl DB darauf achtet; dies sollte
     * entfernt werden.
     * 
     * @return the value of field 'sys_extern'.
    **/
    public ZKDBBaseType getSys_extern()
    {
        return this.sys_extern;
    } //-- Sys_extern getSys_extern() 

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
     * Sets the value of field 'merkmal'.
     * 
     * @param merkmal the value of field 'merkmal'.
    **/
    public void setMerkmal(ZKDBBaseType merkmal)
    {
        this.merkmal = merkmal;
    } //-- void setMerkmal(Merkmal) 

    /**
     * Sets the value of field 'sys_extern'. The field 'sys_extern'
     * has the following description: Im Trafo defaulted auf 'NVS',
     * sollte aus DB und XML entfallen. Kann nicht Pflichtfeld
     * sein, obwohl DB darauf achtet; dies sollte entfernt werden.
     * 
     * @param sys_extern the value of field 'sys_extern'.
    **/
    public void setSys_extern(ZKDBBaseType sys_extern)
    {
        this.sys_extern = sys_extern;
    } //-- void setSys_extern(Sys_extern) 

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
