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
 * Castor-generated value object for the ZKDB "Adresse" (address) XML type.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: fc02235315d187d260313dc80497c304ee15a1ada73c542da18f4bd1129b4140
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class Adresse 
extends ZKDBBaseType 
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Address type discriminator (e.g. private/business address). */
	public ZKDBBaseType typ;

    /** Flag indicating this is a PO box ("Postfach") address. */
    public ZKDBBaseType postfachflag;

    /** Flag indicating whether this address record is currently valid. */
    public ZKDBBaseType valid_flag;

    /** Customer's last name. */
    public ZKDBBaseType kundenname;

    /** Customer's first name. */
    public ZKDBBaseType vorname;

    /** Customer's middle name. */
    public ZKDBBaseType mittelname;

    /** Street and house number, or PO box number. */
    public ZKDBBaseType strasse_postfach;

    /** Additional address line (e.g. building, floor). */
    public ZKDBBaseType adresszusatz;

    /** Postal code. */
    public ZKDBBaseType plz;

    /** City/town. */
    public ZKDBBaseType ort;

    /** Additional locality qualifier for the city/town. */
    public ZKDBBaseType ergaenzung_ort;

    /** State/region. */
    public ZKDBBaseType region;

    /** Preferred salutation for correspondence. */
    public ZKDBBaseType briefanrede;

    /** ISO country code. */
    public ZKDBBaseType land_iso_code;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty Adresse with all fields unset. */
    public Adresse() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Adresse()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'adresszusatz'.
     * 
     * @return the value of field 'adresszusatz'.
    **/
    public ZKDBBaseType getAdresszusatz()
    {
        return this.adresszusatz;
    } //-- Adresszusatz getAdresszusatz() 

    /**
     * Returns the value of field 'briefanrede'.
     * 
     * @return the value of field 'briefanrede'.
    **/
    public ZKDBBaseType getBriefanrede()
    {
        return this.briefanrede;
    } //-- Briefanrede getBriefanrede() 

    /**
     * Returns the value of field 'ergaenzung_ort'.
     * 
     * @return the value of field 'ergaenzung_ort'.
    **/
    public ZKDBBaseType getErgaenzung_ort()
    {
        return this.ergaenzung_ort;
    } //-- Ergaenzung_ort getErgaenzung_ort() 

    /**
     * Returns the value of field 'kundenname'.
     * 
     * @return the value of field 'kundenname'.
    **/
    public ZKDBBaseType getKundenname()
    {
        return this.kundenname;
    } //-- Kundenname getKundenname() 

    /**
     * Returns the value of field 'land_iso_code'.
     * 
     * @return the value of field 'land_iso_code'.
    **/
    public ZKDBBaseType getLand_iso_code()
    {
        return this.land_iso_code;
    } //-- Land_iso_code getLand_iso_code() 

    /**
     * Returns the value of field 'mittelname'.
     * 
     * @return the value of field 'mittelname'.
    **/
    public ZKDBBaseType getMittelname()
    {
        return this.mittelname;
    } //-- Mittelname getMittelname() 

    /**
     * Returns the value of field 'ort'.
     * 
     * @return the value of field 'ort'.
    **/
    public ZKDBBaseType getOrt()
    {
        return this.ort;
    } //-- Ort getOrt() 

    /**
     * Returns the value of field 'plz'.
     * 
     * @return the value of field 'plz'.
    **/
    public ZKDBBaseType getPlz()
    {
        return this.plz;
    } //-- Plz getPlz() 

    /**
     * Returns the value of field 'postfachflag'.
     * 
     * @return the value of field 'postfachflag'.
    **/
    public ZKDBBaseType getPostfachflag()
    {
        return this.postfachflag;
    } //-- Postfachflag getPostfachflag() 

    /**
     * Returns the value of field 'region'.
     * 
     * @return the value of field 'region'.
    **/
    public ZKDBBaseType getRegion()
    {
        return this.region;
    } //-- Region getRegion() 

    /**
     * Returns the value of field 'strasse_postfach'.
     * 
     * @return the value of field 'strasse_postfach'.
    **/
    public ZKDBBaseType getStrasse_postfach()
    {
        return this.strasse_postfach;
    } //-- Strasse_postfach getStrasse_postfach() 

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
     * Returns the value of field 'valid_flag'.
     * 
     * @return the value of field 'valid_flag'.
    **/
    public ZKDBBaseType getValid_flag()
    {
        return this.valid_flag;
    } //-- Valid_flag getValid_flag() 

    /**
     * Returns the value of field 'vorname'.
     * 
     * @return the value of field 'vorname'.
    **/
    public ZKDBBaseType getVorname()
    {
        return this.vorname;
    } //-- Vorname getVorname() 

    /**
     * Sets the value of field 'adresszusatz'.
     * 
     * @param adresszusatz the value of field 'adresszusatz'.
    **/
    public void setAdresszusatz(ZKDBBaseType adresszusatz)
    {
        this.adresszusatz = adresszusatz;
    } //-- void setAdresszusatz(Adresszusatz) 

    /**
     * Sets the value of field 'briefanrede'.
     * 
     * @param briefanrede the value of field 'briefanrede'.
    **/
    public void setBriefanrede(ZKDBBaseType briefanrede)
    {
        this.briefanrede = briefanrede;
    } //-- void setBriefanrede(Briefanrede) 

    /**
     * Sets the value of field 'ergaenzung_ort'.
     * 
     * @param ergaenzung_ort the value of field 'ergaenzung_ort'.
    **/
    public void setErgaenzung_ort(ZKDBBaseType ergaenzung_ort)
    {
        this.ergaenzung_ort = ergaenzung_ort;
    } //-- void setErgaenzung_ort(Ergaenzung_ort) 

    /**
     * Sets the value of field 'kundenname'.
     * 
     * @param kundenname the value of field 'kundenname'.
    **/
    public void setKundenname(ZKDBBaseType kundenname)
    {
        this.kundenname = kundenname;
    } //-- void setKundenname(Kundenname) 

    /**
     * Sets the value of field 'land_iso_code'.
     * 
     * @param land_iso_code the value of field 'land_iso_code'.
    **/
    public void setLand_iso_code(ZKDBBaseType land_iso_code)
    {
        this.land_iso_code = land_iso_code;
    } //-- void setLand_iso_code(Land_iso_code) 

    /**
     * Sets the value of field 'mittelname'.
     * 
     * @param mittelname the value of field 'mittelname'.
    **/
    public void setMittelname(ZKDBBaseType mittelname)
    {
        this.mittelname = mittelname;
    } //-- void setMittelname(Mittelname) 

    /**
     * Sets the value of field 'ort'.
     * 
     * @param ort the value of field 'ort'.
    **/
    public void setOrt(ZKDBBaseType ort)
    {
        this.ort = ort;
    } //-- void setOrt(Ort) 

    /**
     * Sets the value of field 'plz'.
     * 
     * @param plz the value of field 'plz'.
    **/
    public void setPlz(ZKDBBaseType plz)
    {
        this.plz = plz;
    } //-- void setPlz(Plz) 

    /**
     * Sets the value of field 'postfachflag'.
     * 
     * @param postfachflag the value of field 'postfachflag'.
    **/
    public void setPostfachflag(ZKDBBaseType postfachflag)
    {
        this.postfachflag = postfachflag;
    } //-- void setPostfachflag(Postfachflag) 

    /**
     * Sets the value of field 'region'.
     * 
     * @param region the value of field 'region'.
    **/
    public void setRegion(ZKDBBaseType region)
    {
        this.region = region;
    } //-- void setRegion(Region) 

    /**
     * Sets the value of field 'strasse_postfach'.
     * 
     * @param strasse_postfach the value of field 'strasse_postfach'
    **/
    public void setStrasse_postfach(ZKDBBaseType strasse_postfach)
    {
        this.strasse_postfach = strasse_postfach;
    } //-- void setStrasse_postfach(Strasse_postfach) 

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
     * Sets the value of field 'valid_flag'.
     * 
     * @param valid_flag the value of field 'valid_flag'.
    **/
    public void setValid_flag(ZKDBBaseType valid_flag)
    {
        this.valid_flag = valid_flag;
    } //-- void setValid_flag(Valid_flag) 

    /**
     * Sets the value of field 'vorname'.
     * 
     * @param vorname the value of field 'vorname'.
    **/
    public void setVorname(ZKDBBaseType vorname)
    {
        this.vorname = vorname;
    } //-- void setVorname(Vorname) 

}
