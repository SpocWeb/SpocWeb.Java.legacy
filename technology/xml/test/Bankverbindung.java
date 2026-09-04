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
 * =============  BANKVERBINDUNG TYPE ==================	
 * 				
 * 
 * @version $Revision$ $Date$
**/
public class Bankverbindung 
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

    public ZKDBBaseType kto_inhaber;

    public ZKDBBaseType kto_nr;

    public ZKDBBaseType kreditinstitut;

    public ZKDBBaseType BLZ;

    public ZKDBBaseType bevorzugt;

    public ZKDBBaseType lastschrift_sperre;


      //----------------/
     //- Constructors -/
    //----------------/

    public Bankverbindung() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Bankverbindung()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'BLZ'.
     * 
     * @return the value of field 'BLZ'.
    **/
    public ZKDBBaseType getBLZ()
    {
        return this.BLZ;
    } //-- BLZ getBLZ() 

    /**
     * Returns the value of field 'bevorzugt'.
     * 
     * @return the value of field 'bevorzugt'.
    **/
    public ZKDBBaseType getBevorzugt()
    {
        return this.bevorzugt;
    } //-- Bevorzugt getBevorzugt() 

    /**
     * Returns the value of field 'kreditinstitut'.
     * 
     * @return the value of field 'kreditinstitut'.
    **/
    public ZKDBBaseType getKreditinstitut()
    {
        return this.kreditinstitut;
    } //-- Kreditinstitut getKreditinstitut() 

    /**
     * Returns the value of field 'kto_inhaber'.
     * 
     * @return the value of field 'kto_inhaber'.
    **/
    public ZKDBBaseType getKto_inhaber()
    {
        return this.kto_inhaber;
    } //-- Kto_inhaber getKto_inhaber() 

    /**
     * Returns the value of field 'kto_nr'.
     * 
     * @return the value of field 'kto_nr'.
    **/
    public ZKDBBaseType getKto_nr()
    {
        return this.kto_nr;
    } //-- Kto_nr getKto_nr() 

    /**
     * Returns the value of field 'lastschrift_sperre'.
     * 
     * @return the value of field 'lastschrift_sperre'.
    **/
    public ZKDBBaseType getLastschrift_sperre()
    {
        return this.lastschrift_sperre;
    } //-- Lastschrift_sperre getLastschrift_sperre() 

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
     * Sets the value of field 'BLZ'.
     * 
     * @param BLZ the value of field 'BLZ'.
    **/
    public void setBLZ(ZKDBBaseType BLZ)
    {
        this.BLZ = BLZ;
    } //-- void setBLZ(BLZ) 

    /**
     * Sets the value of field 'bevorzugt'.
     * 
     * @param bevorzugt the value of field 'bevorzugt'.
    **/
    public void setBevorzugt(ZKDBBaseType bevorzugt)
    {
        this.bevorzugt = bevorzugt;
    } //-- void setBevorzugt(Bevorzugt) 

    /**
     * Sets the value of field 'kreditinstitut'.
     * 
     * @param kreditinstitut the value of field 'kreditinstitut'.
    **/
    public void setKreditinstitut(ZKDBBaseType kreditinstitut)
    {
        this.kreditinstitut = kreditinstitut;
    } //-- void setKreditinstitut(Kreditinstitut) 

    /**
     * Sets the value of field 'kto_inhaber'.
     * 
     * @param kto_inhaber the value of field 'kto_inhaber'.
    **/
    public void setKto_inhaber(ZKDBBaseType kto_inhaber)
    {
        this.kto_inhaber = kto_inhaber;
    } //-- void setKto_inhaber(Kto_inhaber) 

    /**
     * Sets the value of field 'kto_nr'.
     * 
     * @param kto_nr the value of field 'kto_nr'.
    **/
    public void setKto_nr(ZKDBBaseType kto_nr)
    {
        this.kto_nr = kto_nr;
    } //-- void setKto_nr(Kto_nr) 

    /**
     * Sets the value of field 'lastschrift_sperre'.
     * 
     * @param lastschrift_sperre the value of field
     * 'lastschrift_sperre'.
    **/
    public void setLastschrift_sperre(ZKDBBaseType lastschrift_sperre)
    {
        this.lastschrift_sperre = lastschrift_sperre;
    } //-- void setLastschrift_sperre(Lastschrift_sperre) 

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
