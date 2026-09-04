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
 * =============  STEURERUNG TYPE ==================	
 * 		***** Beinhaltet die Steurungsdaten vom Trafolayer, wird nicht
 * von dem BusinessLayer validiert. Alle Felder sind Pflicht und
 * dienen zur Steuerung des Protokolls ******Alle Felder sind
 * Pflicht und dienen zur Steuerung des Protokolls					
 * 			
 * 
 * @version $Revision$ $Date$
**/
public class Steuerung 
implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private java.lang.String _ZKDBID;

    private java.lang.String _txID;

    private Operation _operation;

    private java.lang.String _erstelltAm;

    private java.lang.String _gueltigAb;

    private java.lang.String _system;

    private java.lang.String _benutzerName;

    private int _statusNummer;

    private java.lang.String _bemerkung;


      //----------------/
     //- Constructors -/
    //----------------/

    public Steuerung() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Steuerung()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'bemerkung'.
     * 
     * @return the value of field 'bemerkung'.
    **/
    public java.lang.String getBemerkung()
    {
        return this._bemerkung;
    } //-- java.lang.String getBemerkung() 

    /**
     * Returns the value of field 'benutzerName'.
     * 
     * @return the value of field 'benutzerName'.
    **/
    public java.lang.String getBenutzerName()
    {
        return this._benutzerName;
    } //-- java.lang.String getBenutzerName() 

    /**
     * Returns the value of field 'erstelltAm'.
     * 
     * @return the value of field 'erstelltAm'.
    **/
    public java.lang.String getErstelltAm()
    {
        return this._erstelltAm;
    } //-- java.lang.String getErstelltAm() 

    /**
     * Returns the value of field 'gueltigAb'.
     * 
     * @return the value of field 'gueltigAb'.
    **/
    public java.lang.String getGueltigAb()
    {
        return this._gueltigAb;
    } //-- java.lang.String getGueltigAb() 

    /**
     * Returns the value of field 'operation'.
     * 
     * @return the value of field 'operation'.
    **/
    public Operation getOperation()
    {
        return this._operation;
    } //-- de.bahn.zkdb.bcm.data.types.Operation getOperation() 

    /**
     * Returns the value of field 'statusNummer'.
     * 
     * @return the value of field 'statusNummer'.
    **/
    public int getStatusNummer()
    {
        return this._statusNummer;
    } //-- java.lang.String getStatusNummer() 

    /**
     * Returns the value of field 'system'.
     * 
     * @return the value of field 'system'.
    **/
    public java.lang.String getSystem()
    {
        return this._system;
    } //-- java.lang.String getSystem() 

    /**
     * Returns the value of field 'txID'.
     * 
     * @return the value of field 'txID'.
    **/
    public java.lang.String getTxID()
    {
        return this._txID;
    } //-- java.lang.String getTxID() 

    /**
     * Returns the value of field 'ZKDBID'.
     * 
     * @return the value of field 'ZKDBID'.
    **/
    public java.lang.String getZKDBID()
    {
        return this._ZKDBID;
    } //-- java.lang.String getZKDBID() 

    /**
     * Sets the value of field 'bemerkung'.
     * 
     * @param bemerkung the value of field 'bemerkung'.
    **/
    public void setBemerkung(java.lang.String bemerkung)
    {
        this._bemerkung = bemerkung;
    } //-- void setBemerkung(java.lang.String) 

    /**
     * Sets the value of field 'benutzerName'.
     * 
     * @param benutzerName the value of field 'benutzerName'.
    **/
    public void setBenutzerName(java.lang.String benutzerName)
    {
        this._benutzerName = benutzerName;
    } //-- void setBenutzerName(java.lang.String) 

    /**
     * Sets the value of field 'erstelltAm'.
     * 
     * @param erstelltAm the value of field 'erstelltAm'.
    **/
    public void setErstelltAm(java.lang.String erstelltAm)
    {
        this._erstelltAm = erstelltAm;
    } //-- void setErstelltAm(java.lang.String) 

    /**
     * Sets the value of field 'gueltigAb'.
     * 
     * @param gueltigAb the value of field 'gueltigAb'.
    **/
    public void setGueltigAb(java.lang.String gueltigAb)
    {
        this._gueltigAb = gueltigAb;
    } //-- void setGueltigAb(java.lang.String) 

	/**
	 * Sets the value of field 'operation'.
	 * 
	 * @param operation the value of field 'operation'.
	**/
	public void setOperation(Operation operation)
	{
		this._operation = operation;
	} //-- void setOperation(de.bahn.zkdb.bcm.data.types.Operation) 

	/**
	 * Sets the value of field 'operation'.
	 * 
	 * @param operation the value of field 'operation'.
	**/
	public void setOperation(String operation)
	{
		this._operation = Operation.valueOf(operation);
	} //-- void setOperation(de.bahn.zkdb.bcm.data.types.Operation) 

    /**
     * Sets the value of field 'statusNummer'.
     * 
     * @param statusNummer the value of field 'statusNummer'.
    **/
    public void setStatusNummer(int statusNummer)
    {
        this._statusNummer = statusNummer;
    } //-- void setStatusNummer(java.lang.String) 

    /**
     * Sets the value of field 'system'.
     * 
     * @param system the value of field 'system'.
    **/
    public void setSystem(java.lang.String system)
    {
        this._system = system;
    } //-- void setSystem(java.lang.String) 

    /**
     * Sets the value of field 'txID'.
     * 
     * @param txID the value of field 'txID'.
    **/
    public void setTxID(java.lang.String txID)
    {
        this._txID = txID;
    } //-- void setTxID(java.lang.String) 

    /**
     * Sets the value of field 'ZKDBID'.
     * 
     * @param ZKDBID the value of field 'ZKDBID'.
    **/
    public void setZKDBID(java.lang.String ZKDBID)
    {
        this._ZKDBID = ZKDBID;
    } //-- void setZKDBID(java.lang.String) 

	/**
	 * @param args URLs to indicate the Input(args[0]), TrafoXSL(args[1]), Output(args[2])
	 * The URLs can also be absolute or relative FileSystem Paths! ^
	 * e.g. java technology.xml.XslTrafo
	 * "E:\Personal\Code\XSL\Music\example\Seal Second 06 Kiss_from_a_Rose.xml"
	 * E:\Personal\Code\XSL\Music\example\SongStyle.xsl
	 * E:\Personal\Code\xsl\Music\example\output.html
	 */
	public static void main(final String[] args) throws Exception { //
		final Class c1 = int.class;
		final Class c2 = Integer.class; 
		System.out.println(c1+"?="+c2+"="+c1.equals(c2)); 
		
		Steuerung.class.getMethod("setStatusNummer", new Class[] {int.class});
	}

}
