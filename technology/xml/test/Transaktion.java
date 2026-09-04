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
 * =============  TRANSAKTION TYPE ================= 
 * 			
 * 
 * @version $Revision$ $Date$
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
