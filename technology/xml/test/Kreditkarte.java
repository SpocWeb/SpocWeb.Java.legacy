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
 * =============  KREDITKARTE TYPE ==================	
 * 			
 * 
 * @version $Revision$ $Date$
**/
public class Kreditkarte 
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

    public ZKDBBaseType inhaber;

    public ZKDBBaseType nummer;

    public ZKDBBaseType kreditinstitut;

    public ZKDBBaseType bevorzugt;

    public ZKDBBaseType gueltig_bis;


      //----------------/
     //- Constructors -/
    //----------------/

    public Kreditkarte() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Kreditkarte()


      //-----------/
     //- Methods -/
    //-----------/

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
     * Returns the value of field 'gueltig_bis'.
     * 
     * @return the value of field 'gueltig_bis'.
    **/
    public ZKDBBaseType getGueltig_bis()
    {
        return this.gueltig_bis;
    } //-- Gueltig_bis getGueltig_bis() 

    /**
     * Returns the value of field 'inhaber'.
     * 
     * @return the value of field 'inhaber'.
    **/
    public ZKDBBaseType getInhaber()
    {
        return this.inhaber;
    } //-- Inhaber getInhaber() 

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
     * Returns the value of field 'nummer'.
     * 
     * @return the value of field 'nummer'.
    **/
    public ZKDBBaseType getNummer()
    {
        return this.nummer;
    } //-- Nummer getNummer() 

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
     * Sets the value of field 'bevorzugt'.
     * 
     * @param bevorzugt the value of field 'bevorzugt'.
    **/
    public void setBevorzugt(ZKDBBaseType bevorzugt)
    {
        this.bevorzugt = bevorzugt;
    } //-- void setBevorzugt(Bevorzugt) 

    /**
     * Sets the value of field 'gueltig_bis'.
     * 
     * @param gueltig_bis the value of field 'gueltig_bis'.
    **/
    public void setGueltig_bis(ZKDBBaseType gueltig_bis)
    {
        this.gueltig_bis = gueltig_bis;
    } //-- void setGueltig_bis(Gueltig_bis) 

    /**
     * Sets the value of field 'inhaber'.
     * 
     * @param inhaber the value of field 'inhaber'.
    **/
    public void setInhaber(ZKDBBaseType inhaber)
    {
        this.inhaber = inhaber;
    } //-- void setInhaber(Inhaber) 

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
     * Sets the value of field 'nummer'.
     * 
     * @param nummer the value of field 'nummer'.
    **/
    public void setNummer(ZKDBBaseType nummer)
    {
        this.nummer = nummer;
    } //-- void setNummer(Nummer) 

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
