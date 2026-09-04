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
 * =============  KUNDENKARTE TYPE ==================	
 * 				
 * 
 * @version $Revision$ $Date$
**/
public class Kundenkarte 
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

    public ZKDBBaseType nummer;

    public ZKDBBaseType befoerderungsklasse;

    public ZKDBBaseType gueltigkeitsdauer;

    public ZKDBBaseType gueltig_von;

    public ZKDBBaseType gueltig_bis;

    public ZKDBBaseType bezeichnung;


      //----------------/
     //- Constructors -/
    //----------------/

    public Kundenkarte() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Kundenkarte()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'befoerderungsklasse'.
     * 
     * @return the value of field 'befoerderungsklasse'.
    **/
    public ZKDBBaseType getBefoerderungsklasse()
    {
        return this.befoerderungsklasse;
    } //-- Befoerderungsklasse getBefoerderungsklasse() 

    /**
     * Returns the value of field 'bezeichnung'.
     * 
     * @return the value of field 'bezeichnung'.
    **/
    public ZKDBBaseType getBezeichnung()
    {
        return this.bezeichnung;
    } //-- Bezeichnung getBezeichnung() 

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
     * Returns the value of field 'gueltig_von'.
     * 
     * @return the value of field 'gueltig_von'.
    **/
    public ZKDBBaseType getGueltig_von()
    {
        return this.gueltig_von;
    } //-- Gueltig_von getGueltig_von() 

    /**
     * Returns the value of field 'gueltigkeitsdauer'.
     * 
     * @return the value of field 'gueltigkeitsdauer'.
    **/
    public ZKDBBaseType getGueltigkeitsdauer()
    {
        return this.gueltigkeitsdauer;
    } //-- Gueltigkeitsdauer getGueltigkeitsdauer() 

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
     * Sets the value of field 'befoerderungsklasse'.
     * 
     * @param befoerderungsklasse the value of field
     * 'befoerderungsklasse'.
    **/
    public void setBefoerderungsklasse(ZKDBBaseType befoerderungsklasse)
    {
        this.befoerderungsklasse = befoerderungsklasse;
    } //-- void setBefoerderungsklasse(Befoerderungsklasse) 

    /**
     * Sets the value of field 'bezeichnung'.
     * 
     * @param bezeichnung the value of field 'bezeichnung'.
    **/
    public void setBezeichnung(ZKDBBaseType bezeichnung)
    {
        this.bezeichnung = bezeichnung;
    } //-- void setBezeichnung(Bezeichnung) 

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
     * Sets the value of field 'gueltig_von'.
     * 
     * @param gueltig_von the value of field 'gueltig_von'.
    **/
    public void setGueltig_von(ZKDBBaseType gueltig_von)
    {
        this.gueltig_von = gueltig_von;
    } //-- void setGueltig_von(Gueltig_von) 

    /**
     * Sets the value of field 'gueltigkeitsdauer'.
     * 
     * @param gueltigkeitsdauer the value of field
     * 'gueltigkeitsdauer'.
    **/
    public void setGueltigkeitsdauer(ZKDBBaseType gueltigkeitsdauer)
    {
        this.gueltigkeitsdauer = gueltigkeitsdauer;
    } //-- void setGueltigkeitsdauer(Gueltigkeitsdauer) 

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
