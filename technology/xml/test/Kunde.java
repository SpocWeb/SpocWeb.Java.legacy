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
 * Castor-generated value object for the ZKDB "Kunde" (customer) XML type.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 94ffef6168f0c7a307319877809de5c57fe02ccaf986b7625b4b5fb722e1d152
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class Kunde 
extends ZKDBBaseType 
{


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Primary ZKDB customer ID. */
	public ZKDBBaseType ZKDBID;

	/** Secondary/alternate customer ID. */
	public ZKDBBaseType anotherID;

    /** Flag indicating this customer is an organization rather than a natural person. */
    public ZKDBBaseType orgflag;

    /** Customer's last name. */
    public ZKDBBaseType kundenname;

    /** Customer's first name. */
    public ZKDBBaseType vorname;

    /** Customer's middle name. */
    public ZKDBBaseType mittelname;

    /** Additional name qualifier (e.g. "von", "-"). */
    public ZKDBBaseType namenzusatz;

    /** Academic or professional title. */
    public ZKDBBaseType titel;

    /** Salutation (e.g. Mr./Ms.). */
    public ZKDBBaseType anrede;

    /** Special/alternate salutation. */
    public ZKDBBaseType bes_anrede;

    /** Date of birth. */
    public ZKDBBaseType geburtsdatum;

    /** Gender. */
    public ZKDBBaseType geschlecht;

    /** Marital status. */
    public ZKDBBaseType familienstand;

    /** ISO nationality code. */
    public ZKDBBaseType nationalitaet_iso_code;

    /** Number of children. */
    public ZKDBBaseType anzahl_kinder;

    /** Income bracket. */
    public ZKDBBaseType gehaltsklasse;

    /** Education level. */
    public ZKDBBaseType ausbildung;

    /** Smoker flag. */
    public ZKDBBaseType raucher;

    /** Preferred travel class. */
    public ZKDBBaseType wagenklasse;

    /** Legal form, for organization customers. */
    public ZKDBBaseType rechtsform;

    /** Commercial register number, for organization customers. */
    public ZKDBBaseType registernr;

    /** Commercial register location, for organization customers. */
    public ZKDBBaseType registerort;

    /** Flag blocking this customer record. */
    public ZKDBBaseType kundensperre;

    /** Flag blocking marketing communication to this customer. */
    public ZKDBBaseType werbungssperre;

    /** Preferred payment method. */
    public ZKDBBaseType zahlungsart;

    /** Direct debit status. */
    public ZKDBBaseType lastschrift_status;

    /** Flag blocking direct debit for this customer. */
    public ZKDBBaseType lastschrift_sperre;

    /** Sales/distribution channel. */
    public ZKDBBaseType vertriebsweg;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty Kunde with all fields unset. */
    public Kunde() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Kunde()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'anrede'.
     * 
     * @return the value of field 'anrede'.
    **/
    public ZKDBBaseType getAnrede()
    {
        return this.anrede;
    } //-- Anrede getAnrede() 

    /**
     * Returns the value of field 'anzahl_kinder'.
     * 
     * @return the value of field 'anzahl_kinder'.
    **/
    public ZKDBBaseType getAnzahl_kinder()
    {
        return this.anzahl_kinder;
    } //-- Anzahl_kinder getAnzahl_kinder() 

    /**
     * Returns the value of field 'ausbildung'.
     * 
     * @return the value of field 'ausbildung'.
    **/
    public ZKDBBaseType getAusbildung()
    {
        return this.ausbildung;
    } //-- Ausbildung getAusbildung() 

    /**
     * Returns the value of field 'bes_anrede'.
     * 
     * @return the value of field 'bes_anrede'.
    **/
    public ZKDBBaseType getBes_anrede()
    {
        return this.bes_anrede;
    } //-- Bes_anrede getBes_anrede() 

    /**
     * Returns the value of field 'familienstand'.
     * 
     * @return the value of field 'familienstand'.
    **/
    public ZKDBBaseType getFamilienstand()
    {
        return this.familienstand;
    } //-- Familienstand getFamilienstand() 

    /**
     * Returns the value of field 'geburtsdatum'.
     * 
     * @return the value of field 'geburtsdatum'.
    **/
    public ZKDBBaseType getGeburtsdatum()
    {
        return this.geburtsdatum;
    } //-- Geburtsdatum getGeburtsdatum() 

    /**
     * Returns the value of field 'gehaltsklasse'.
     * 
     * @return the value of field 'gehaltsklasse'.
    **/
    public ZKDBBaseType getGehaltsklasse()
    {
        return this.gehaltsklasse;
    } //-- Gehaltsklasse getGehaltsklasse() 

    /**
     * Returns the value of field 'geschlecht'.
     * 
     * @return the value of field 'geschlecht'.
    **/
    public ZKDBBaseType getGeschlecht()
    {
        return this.geschlecht;
    } //-- Geschlecht getGeschlecht() 

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
     * Returns the value of field 'kundensperre'.
     * 
     * @return the value of field 'kundensperre'.
    **/
    public ZKDBBaseType getKundensperre()
    {
        return this.kundensperre;
    } //-- Kundensperre getKundensperre() 

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
     * Returns the value of field 'lastschrift_status'.
     * 
     * @return the value of field 'lastschrift_status'.
    **/
    public ZKDBBaseType getLastschrift_status()
    {
        return this.lastschrift_status;
    } //-- Lastschrift_status getLastschrift_status() 

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
     * Returns the value of field 'namenzusatz'.
     * 
     * @return the value of field 'namenzusatz'.
    **/
    public ZKDBBaseType getNamenzusatz()
    {
        return this.namenzusatz;
    } //-- Namenzusatz getNamenzusatz() 

    /**
     * Returns the value of field 'nationalitaet_iso_code'.
     * 
     * @return the value of field 'nationalitaet_iso_code'.
    **/
    public ZKDBBaseType getNationalitaet_iso_code()
    {
        return this.nationalitaet_iso_code;
    } //-- Nationalitaet_iso_code getNationalitaet_iso_code() 

    /**
     * Returns the value of field 'orgflag'.
     * 
     * @return the value of field 'orgflag'.
    **/
    public ZKDBBaseType getOrgflag()
    {
        return this.orgflag;
    } //-- Orgflag getOrgflag() 

    /**
     * Returns the value of field 'raucher'.
     * 
     * @return the value of field 'raucher'.
    **/
    public ZKDBBaseType getRaucher()
    {
        return this.raucher;
    } //-- Raucher getRaucher() 

    /**
     * Returns the value of field 'rechtsform'.
     * 
     * @return the value of field 'rechtsform'.
    **/
    public ZKDBBaseType getRechtsform()
    {
        return this.rechtsform;
    } //-- Rechtsform getRechtsform() 

    /**
     * Returns the value of field 'registernr'.
     * 
     * @return the value of field 'registernr'.
    **/
    public ZKDBBaseType getRegisternr()
    {
        return this.registernr;
    } //-- Registernr getRegisternr() 

    /**
     * Returns the value of field 'registerort'.
     * 
     * @return the value of field 'registerort'.
    **/
    public ZKDBBaseType getRegisterort()
    {
        return this.registerort;
    } //-- Registerort getRegisterort() 

    /**
     * Returns the value of field 'titel'.
     * 
     * @return the value of field 'titel'.
    **/
    public ZKDBBaseType getTitel()
    {
        return this.titel;
    } //-- Titel getTitel() 

    /**
     * Returns the value of field 'vertriebsweg'.
     * 
     * @return the value of field 'vertriebsweg'.
    **/
    public ZKDBBaseType getVertriebsweg()
    {
        return this.vertriebsweg;
    } //-- Vertriebsweg getVertriebsweg() 

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
     * Returns the value of field 'wagenklasse'.
     * 
     * @return the value of field 'wagenklasse'.
    **/
    public ZKDBBaseType getWagenklasse()
    {
        return this.wagenklasse;
    } //-- Wagenklasse getWagenklasse() 

    /**
     * Returns the value of field 'werbungssperre'.
     * 
     * @return the value of field 'werbungssperre'.
    **/
    public ZKDBBaseType getWerbungssperre()
    {
        return this.werbungssperre;
    } //-- Werbungssperre getWerbungssperre() 
	
	/**
	 * Returns the value of field 'anotherID'.
	 * 
	 * @return the value of field 'anotherID'.
	**/
	public ZKDBBaseType getAnotherID()
	{
		return this.anotherID;
	} //-- ZKDBID getAnotherID() 

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
     * Returns the value of field 'zahlungsart'.
     * 
     * @return the value of field 'zahlungsart'.
    **/
    public ZKDBBaseType getZahlungsart()
    {
        return this.zahlungsart;
    } //-- Zahlungsart getZahlungsart() 

    /**
     * Sets the value of field 'anrede'.
     * 
     * @param anrede the value of field 'anrede'.
    **/
    public void setAnrede(ZKDBBaseType anrede)
    {
        this.anrede = anrede;
    } //-- void setAnrede(Anrede) 

    /**
     * Sets the value of field 'anzahl_kinder'.
     * 
     * @param anzahl_kinder the value of field 'anzahl_kinder'.
    **/
    public void setAnzahl_kinder(ZKDBBaseType anzahl_kinder)
    {
        this.anzahl_kinder = anzahl_kinder;
    } //-- void setAnzahl_kinder(Anzahl_kinder) 

    /**
     * Sets the value of field 'ausbildung'.
     * 
     * @param ausbildung the value of field 'ausbildung'.
    **/
    public void setAusbildung(ZKDBBaseType ausbildung)
    {
        this.ausbildung = ausbildung;
    } //-- void setAusbildung(Ausbildung) 

    /**
     * Sets the value of field 'bes_anrede'.
     * 
     * @param bes_anrede the value of field 'bes_anrede'.
    **/
    public void setBes_anrede(ZKDBBaseType bes_anrede)
    {
        this.bes_anrede = bes_anrede;
    } //-- void setBes_anrede(Bes_anrede) 

    /**
     * Sets the value of field 'familienstand'.
     * 
     * @param familienstand the value of field 'familienstand'.
    **/
    public void setFamilienstand(ZKDBBaseType familienstand)
    {
        this.familienstand = familienstand;
    } //-- void setFamilienstand(Familienstand) 

    /**
     * Sets the value of field 'geburtsdatum'.
     * 
     * @param geburtsdatum the value of field 'geburtsdatum'.
    **/
    public void setGeburtsdatum(ZKDBBaseType geburtsdatum)
    {
        this.geburtsdatum = geburtsdatum;
    } //-- void setGeburtsdatum(Geburtsdatum) 

    /**
     * Sets the value of field 'gehaltsklasse'.
     * 
     * @param gehaltsklasse the value of field 'gehaltsklasse'.
    **/
    public void setGehaltsklasse(ZKDBBaseType gehaltsklasse)
    {
        this.gehaltsklasse = gehaltsklasse;
    } //-- void setGehaltsklasse(Gehaltsklasse) 

    /**
     * Sets the value of field 'geschlecht'.
     * 
     * @param geschlecht the value of field 'geschlecht'.
    **/
    public void setGeschlecht(ZKDBBaseType geschlecht)
    {
        this.geschlecht = geschlecht;
    } //-- void setGeschlecht(Geschlecht) 

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
     * Sets the value of field 'kundensperre'.
     * 
     * @param kundensperre the value of field 'kundensperre'.
    **/
    public void setKundensperre(ZKDBBaseType kundensperre)
    {
        this.kundensperre = kundensperre;
    } //-- void setKundensperre(Kundensperre) 

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
     * Sets the value of field 'lastschrift_status'.
     * 
     * @param lastschrift_status the value of field
     * 'lastschrift_status'.
    **/
    public void setLastschrift_status(ZKDBBaseType lastschrift_status)
    {
        this.lastschrift_status = lastschrift_status;
    } //-- void setLastschrift_status(Lastschrift_status) 

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
     * Sets the value of field 'namenzusatz'.
     * 
     * @param namenzusatz the value of field 'namenzusatz'.
    **/
    public void setNamenzusatz(ZKDBBaseType namenzusatz)
    {
        this.namenzusatz = namenzusatz;
    } //-- void setNamenzusatz(Namenzusatz) 

    /**
     * Sets the value of field 'nationalitaet_iso_code'.
     * 
     * @param nationalitaet_iso_code the value of field
     * 'nationalitaet_iso_code'.
    **/
    public void setNationalitaet_iso_code(ZKDBBaseType nationalitaet_iso_code)
    {
        this.nationalitaet_iso_code = nationalitaet_iso_code;
    } //-- void setNationalitaet_iso_code(Nationalitaet_iso_code) 

    /**
     * Sets the value of field 'orgflag'.
     * 
     * @param orgflag the value of field 'orgflag'.
    **/
    public void setOrgflag(ZKDBBaseType orgflag)
    {
        this.orgflag = orgflag;
    } //-- void setOrgflag(Orgflag) 

    /**
     * Sets the value of field 'raucher'.
     * 
     * @param raucher the value of field 'raucher'.
    **/
    public void setRaucher(ZKDBBaseType raucher)
    {
        this.raucher = raucher;
    } //-- void setRaucher(Raucher) 

    /**
     * Sets the value of field 'rechtsform'.
     * 
     * @param rechtsform the value of field 'rechtsform'.
    **/
    public void setRechtsform(ZKDBBaseType rechtsform)
    {
        this.rechtsform = rechtsform;
    } //-- void setRechtsform(Rechtsform) 

    /**
     * Sets the value of field 'registernr'.
     * 
     * @param registernr the value of field 'registernr'.
    **/
    public void setRegisternr(ZKDBBaseType registernr)
    {
        this.registernr = registernr;
    } //-- void setRegisternr(Registernr) 

    /**
     * Sets the value of field 'registerort'.
     * 
     * @param registerort the value of field 'registerort'.
    **/
    public void setRegisterort(ZKDBBaseType registerort)
    {
        this.registerort = registerort;
    } //-- void setRegisterort(Registerort) 

    /**
     * Sets the value of field 'titel'.
     * 
     * @param titel the value of field 'titel'.
    **/
    public void setTitel(ZKDBBaseType titel)
    {
        this.titel = titel;
    } //-- void setTitel(Titel) 

    /**
     * Sets the value of field 'vertriebsweg'.
     * 
     * @param vertriebsweg the value of field 'vertriebsweg'.
    **/
    public void setVertriebsweg(ZKDBBaseType vertriebsweg)
    {
        this.vertriebsweg = vertriebsweg;
    } //-- void setVertriebsweg(Vertriebsweg) 

    /**
     * Sets the value of field 'vorname'.
     * 
     * @param vorname the value of field 'vorname'.
    **/
    public void setVorname(ZKDBBaseType vorname)
    {
        this.vorname = vorname;
    } //-- void setVorname(Vorname) 

    /**
     * Sets the value of field 'wagenklasse'.
     * 
     * @param wagenklasse the value of field 'wagenklasse'.
    **/
    public void setWagenklasse(ZKDBBaseType wagenklasse)
    {
        this.wagenklasse = wagenklasse;
    } //-- void setWagenklasse(Wagenklasse) 

    /**
     * Sets the value of field 'werbungssperre'.
     * 
     * @param werbungssperre the value of field 'werbungssperre'.
    **/
    public void setWerbungssperre(ZKDBBaseType werbungssperre)
    {
        this.werbungssperre = werbungssperre;
    } //-- void setWerbungssperre(Werbungssperre) 

	/**
	 * Sets the value of field 'anotherID'.
	 * 
	 * @param ZKDBID the value of field 'anotherID'.
	**/
	public void setAnotherID(ZKDBBaseType anotherID)
	{
		this.anotherID = anotherID;
	} //-- void setAnotherID(anotherID) 

	/**
	 * Sets the value of field 'ZKDBID'.
	 * 
	 * @param ZKDBID the value of field 'ZKDBID'.
	**/
	public void setZKDBID(ZKDBBaseType ZKDBID)
	{
		this.ZKDBID = ZKDBID;
	} //-- void setZKDBID(ZKDBID) 

    /**
     * Sets the value of field 'zahlungsart'.
     * 
     * @param zahlungsart the value of field 'zahlungsart'.
    **/
    public void setZahlungsart(ZKDBBaseType zahlungsart)
    {
        this.zahlungsart = zahlungsart;
    } //-- void setZahlungsart(Zahlungsart) 

}
