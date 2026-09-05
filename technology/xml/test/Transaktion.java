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
 * Castor-generated value object for the ZKDB "Transaktion" (transaction) XML type, pairing a
 * {@link Steuerung} control section with its {@link Daten} payload.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: d9528e86954b8504b3ffbc5393db38f47b3a1e466c2ceb5c75ae6a3cfdd7b447
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class Transaktion 
implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Steuerung _steuerung;

    private Daten _daten;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty Transaktion with no Steuerung or Daten set. */
    public Transaktion() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Transaktion()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'daten'.
     * 
     * @return the value of field 'daten'.
    **/
    public Daten getDaten()
    {
        return this._daten;
    } //-- Daten getDaten() 

    /**
     * Returns the value of field 'steuerung'.
     * 
     * @return the value of field 'steuerung'.
    **/
    public Steuerung getSteuerung()
    {
        return this._steuerung;
    } //-- Steuerung getSteuerung() 

    /**
     * Sets the value of field 'daten'.
     * 
     * @param daten the value of field 'daten'.
    **/
    public void setDaten(Daten daten)
    {
        this._daten = daten;
    } //-- void setDaten(Daten) 

    /**
     * Sets the value of field 'steuerung'.
     * 
     * @param steuerung the value of field 'steuerung'.
    **/
    public void setSteuerung(Steuerung steuerung)
    {
        this._steuerung = steuerung;
    } //-- void setSteuerung(Steuerung) 

}
