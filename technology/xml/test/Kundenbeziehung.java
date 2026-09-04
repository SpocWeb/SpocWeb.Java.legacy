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
 * =============  KUNDENBEZIEHUNG TYPE ==================	
 * 				
 * 
 * @version $Revision$ $Date$
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

	public ZKDBBaseType typ;

    public ZKDBBaseType ZKDBID;


      //----------------/
     //- Constructors -/
    //----------------/

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
