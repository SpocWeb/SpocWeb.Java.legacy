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

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Castor-generated value object for the ZKDB "Daten" (data) XML type: holds the Kunde plus
 * all business-relevant collections (Adresse, Bankverbindung, etc.) for a Transaktion.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 5ebbe934eeaef67014bdda0f8981a4e4da9caec13f0dbab8b07313c6aca6d0fd
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object Root]
 * facets: {layer: domain, status: legacy, complexity: medium}
 * -->
**/
public class Daten 
extends ZKDBBaseType {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Kunde _kunde;

    private java.util.ArrayList _kundeInSystemList;

    private java.util.ArrayList _adresseList;

    private java.util.ArrayList _bankverbindungList;

    private java.util.ArrayList _benutzererkennungList;

    private java.util.ArrayList _kommunikationList;

    private java.util.ArrayList _kreditkarteList;

    private java.util.ArrayList _kundenkarteList;

    private java.util.ArrayList _kundenmerkmalList;

    private java.util.ArrayList _rolleList;

    private java.util.ArrayList _kundenbeziehungList;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty Daten with all collection fields initialized to empty lists. */
    public Daten() {
        super();
        _kundeInSystemList = new ArrayList();
        _adresseList = new ArrayList();
        _bankverbindungList = new ArrayList();
        _benutzererkennungList = new ArrayList();
        _kommunikationList = new ArrayList();
        _kreditkarteList = new ArrayList();
        _kundenkarteList = new ArrayList();
        _kundenmerkmalList = new ArrayList();
        _rolleList = new ArrayList();
        _kundenbeziehungList = new ArrayList();
    } //-- de.bahn.zkdb.bcm.data.DatenType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Appends a Adresse to the end of the list.
     *
     * @param vAdresse
    **/
    public void addAdresse(Adresse vAdresse)
        throws java.lang.IndexOutOfBoundsException
    {
        _adresseList.add(vAdresse);
    } //-- void addAdresse(Adresse) 

    /**
     * Inserts a Adresse at the given position in the list.
     *
     * @param index
     * @param vAdresse
    **/
    public void addAdresse(int index, Adresse vAdresse)
        throws java.lang.IndexOutOfBoundsException
    {
        _adresseList.add(index, vAdresse);
    } //-- void addAdresse(int, Adresse) 

    /**
     * Appends a Bankverbindung to the end of the list.
     *
     * @param vBankverbindung
    **/
    public void addBankverbindung(Bankverbindung vBankverbindung)
        throws java.lang.IndexOutOfBoundsException
    {
        _bankverbindungList.add(vBankverbindung);
    } //-- void addBankverbindung(Bankverbindung) 

    /**
     * Inserts a Bankverbindung at the given position in the list.
     *
     * @param index
     * @param vBankverbindung
    **/
    public void addBankverbindung(int index, Bankverbindung vBankverbindung)
        throws java.lang.IndexOutOfBoundsException
    {
        _bankverbindungList.add(index, vBankverbindung);
    } //-- void addBankverbindung(int, Bankverbindung) 

    /**
     * Appends a Benutzererkennung to the end of the list.
     *
     * @param vBenutzererkennung
    **/
    public void addBenutzererkennung(Benutzererkennung vBenutzererkennung)
        throws java.lang.IndexOutOfBoundsException
    {
        _benutzererkennungList.add(vBenutzererkennung);
    } //-- void addBenutzererkennung(Benutzererkennung) 

    /**
     * Inserts a Benutzererkennung at the given position in the list.
     *
     * @param index
     * @param vBenutzererkennung
    **/
    public void addBenutzererkennung(int index, Benutzererkennung vBenutzererkennung)
        throws java.lang.IndexOutOfBoundsException
    {
        _benutzererkennungList.add(index, vBenutzererkennung);
    } //-- void addBenutzererkennung(int, Benutzererkennung) 

    /**
     * Appends a Kommunikation to the end of the list.
     *
     * @param vKommunikation
    **/
    public void addKommunikation(Kommunikation vKommunikation)
        throws java.lang.IndexOutOfBoundsException
    {
        _kommunikationList.add(vKommunikation);
    } //-- void addKommunikation(Kommunikation) 

    /**
     * Inserts a Kommunikation at the given position in the list.
     *
     * @param index
     * @param vKommunikation
    **/
    public void addKommunikation(int index, Kommunikation vKommunikation)
        throws java.lang.IndexOutOfBoundsException
    {
        _kommunikationList.add(index, vKommunikation);
    } //-- void addKommunikation(int, Kommunikation) 

    /**
     * Appends a Kreditkarte to the end of the list.
     *
     * @param vKreditkarte
    **/
    public void addKreditkarte(Kreditkarte vKreditkarte)
        throws java.lang.IndexOutOfBoundsException
    {
        _kreditkarteList.add(vKreditkarte);
    } //-- void addKreditkarte(Kreditkarte) 

    /**
     * Inserts a Kreditkarte at the given position in the list.
     *
     * @param index
     * @param vKreditkarte
    **/
    public void addKreditkarte(int index, Kreditkarte vKreditkarte)
        throws java.lang.IndexOutOfBoundsException
    {
        _kreditkarteList.add(index, vKreditkarte);
    } //-- void addKreditkarte(int, Kreditkarte) 

    /**
     * Appends a KundeInSystem to the end of the list.
     *
     * @param vKundeInSystem
    **/
    public void addKundeInSystem(KundeInSystem vKundeInSystem)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundeInSystemList.add(vKundeInSystem);
    } //-- void addKundeInSystem(KundeInSystem) 

    /**
     * Inserts a KundeInSystem at the given position in the list.
     *
     * @param index
     * @param vKundeInSystem
    **/
    public void addKundeInSystem(int index, KundeInSystem vKundeInSystem)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundeInSystemList.add(index, vKundeInSystem);
    } //-- void addKundeInSystem(int, KundeInSystem) 

    /**
     * Appends a Kundenbeziehung to the end of the list.
     *
     * @param vKundenbeziehung
    **/
    public void addKundenbeziehung(Kundenbeziehung vKundenbeziehung)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundenbeziehungList.add(vKundenbeziehung);
    } //-- void addKundenbeziehung(Kundenbeziehung) 

    /**
     * Inserts a Kundenbeziehung at the given position in the list.
     *
     * @param index
     * @param vKundenbeziehung
    **/
    public void addKundenbeziehung(int index, Kundenbeziehung vKundenbeziehung)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundenbeziehungList.add(index, vKundenbeziehung);
    } //-- void addKundenbeziehung(int, Kundenbeziehung) 

    /**
     * Appends a Kundenkarte to the end of the list.
     *
     * @param vKundenkarte
    **/
    public void addKundenkarte(Kundenkarte vKundenkarte)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundenkarteList.add(vKundenkarte);
    } //-- void addKundenkarte(Kundenkarte) 

    /**
     * Inserts a Kundenkarte at the given position in the list.
     *
     * @param index
     * @param vKundenkarte
    **/
    public void addKundenkarte(int index, Kundenkarte vKundenkarte)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundenkarteList.add(index, vKundenkarte);
    } //-- void addKundenkarte(int, Kundenkarte) 

    /**
     * Appends a Kundenmerkmal to the end of the list.
     *
     * @param vKundenmerkmal
    **/
    public void addKundenmerkmal(Kundenmerkmal vKundenmerkmal)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundenmerkmalList.add(vKundenmerkmal);
    } //-- void addKundenmerkmal(Kundenmerkmal) 

    /**
     * Inserts a Kundenmerkmal at the given position in the list.
     *
     * @param index
     * @param vKundenmerkmal
    **/
    public void addKundenmerkmal(int index, Kundenmerkmal vKundenmerkmal)
        throws java.lang.IndexOutOfBoundsException
    {
        _kundenmerkmalList.add(index, vKundenmerkmal);
    } //-- void addKundenmerkmal(int, Kundenmerkmal) 

    /**
     * Appends a Rolle to the end of the list.
     *
     * @param vRolle
    **/
    public void addRolle(Rolle vRolle)
        throws java.lang.IndexOutOfBoundsException
    {
        _rolleList.add(vRolle);
    } //-- void addRolle(Rolle) 

    /**
     * Inserts a Rolle at the given position in the list.
     *
     * @param index
     * @param vRolle
    **/
    public void addRolle(int index, Rolle vRolle)
        throws java.lang.IndexOutOfBoundsException
    {
        _rolleList.add(index, vRolle);
    } //-- void addRolle(int, Rolle) 

    /** Removes all Adresse elements from the list. */
    public void clearAdresse()
    {
        _adresseList.clear();
    } //-- void clearAdresse() 

    /** Removes all Bankverbindung elements from the list. */
    public void clearBankverbindung()
    {
        _bankverbindungList.clear();
    } //-- void clearBankverbindung() 

    /** Removes all Benutzererkennung elements from the list. */
    public void clearBenutzererkennung()
    {
        _benutzererkennungList.clear();
    } //-- void clearBenutzererkennung() 

    /** Removes all Kommunikation elements from the list. */
    public void clearKommunikation()
    {
        _kommunikationList.clear();
    } //-- void clearKommunikation() 

    /** Removes all Kreditkarte elements from the list. */
    public void clearKreditkarte()
    {
        _kreditkarteList.clear();
    } //-- void clearKreditkarte() 

    /** Removes all KundeInSystem elements from the list. */
    public void clearKundeInSystem()
    {
        _kundeInSystemList.clear();
    } //-- void clearKundeInSystem() 

    /** Removes all Kundenbeziehung elements from the list. */
    public void clearKundenbeziehung()
    {
        _kundenbeziehungList.clear();
    } //-- void clearKundenbeziehung() 

    /** Removes all Kundenkarte elements from the list. */
    public void clearKundenkarte()
    {
        _kundenkarteList.clear();
    } //-- void clearKundenkarte() 

    /** Removes all Kundenmerkmal elements from the list. */
    public void clearKundenmerkmal()
    {
        _kundenmerkmalList.clear();
    } //-- void clearKundenmerkmal() 

    /** Removes all Rolle elements from the list. */
    public void clearRolle()
    {
        _rolleList.clear();
    } //-- void clearRolle() 

    /** Returns an Iterator over all Adresse elements in the list. */
    public Iterator enumerateAdresse()
    {
        return _adresseList.iterator();
    } //-- Iterator enumerateAdresse() 

    /** Returns an Iterator over all Bankverbindung elements in the list. */
    public Iterator enumerateBankverbindung()
    {
        return _bankverbindungList.iterator();
    } //-- Iterator enumerateBankverbindung() 

    /** Returns an Iterator over all Benutzererkennung elements in the list. */
    public Iterator enumerateBenutzererkennung()
    {
        return _benutzererkennungList.iterator();
    } //-- Iterator enumerateBenutzererkennung() 

    /** Returns an Iterator over all Kommunikation elements in the list. */
    public Iterator enumerateKommunikation()
    {
        return _kommunikationList.iterator();
    } //-- Iterator enumerateKommunikation() 

    /** Returns an Iterator over all Kreditkarte elements in the list. */
    public Iterator enumerateKreditkarte()
    {
        return _kreditkarteList.iterator();
    } //-- Iterator enumerateKreditkarte() 

    /** Returns an Iterator over all KundeInSystem elements in the list. */
    public Iterator enumerateKundeInSystem()
    {
        return _kundeInSystemList.iterator();
    } //-- Iterator enumerateKundeInSystem() 

    /** Returns an Iterator over all Kundenbeziehung elements in the list. */
    public Iterator enumerateKundenbeziehung()
    {
        return _kundenbeziehungList.iterator();
    } //-- Iterator enumerateKundenbeziehung() 

    /** Returns an Iterator over all Kundenkarte elements in the list. */
    public Iterator enumerateKundenkarte()
    {
        return _kundenkarteList.iterator();
    } //-- Iterator enumerateKundenkarte() 

    /** Returns an Iterator over all Kundenmerkmal elements in the list. */
    public Iterator enumerateKundenmerkmal()
    {
        return _kundenmerkmalList.iterator();
    } //-- Iterator enumerateKundenmerkmal() 

    /** Returns an Iterator over all Rolle elements in the list. */
    public Iterator enumerateRolle()
    {
        return _rolleList.iterator();
    } //-- Iterator enumerateRolle() 

    /**
     * Returns the Adresse at the given position in the list.
     *
     * @param index
    **/
    public Adresse getAdresse(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _adresseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Adresse) _adresseList.get(index);
    } //-- Adresse getAdresse(int) 

    /** Returns all Adresse elements as a new array. */
    public Adresse[] getAdresse()
    {
        int size = _adresseList.size();
        Adresse[] mArray = new Adresse[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Adresse) _adresseList.get(index);
        }
        return mArray;
    } //-- Adresse[] getAdresse() 

    /** Returns the number of Adresse elements in the list. */
    public int getAdresseCount()
    {
        return _adresseList.size();
    } //-- int getAdresseCount() 

    /**
     * Returns the Bankverbindung at the given position in the list.
     *
     * @param index
    **/
    public Bankverbindung getBankverbindung(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _bankverbindungList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Bankverbindung) _bankverbindungList.get(index);
    } //-- Bankverbindung getBankverbindung(int) 

    /** Returns all Bankverbindung elements as a new array. */
    public Bankverbindung[] getBankverbindung()
    {
        int size = _bankverbindungList.size();
        Bankverbindung[] mArray = new Bankverbindung[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Bankverbindung) _bankverbindungList.get(index);
        }
        return mArray;
    } //-- Bankverbindung[] getBankverbindung() 

    /** Returns the number of Bankverbindung elements in the list. */
    public int getBankverbindungCount()
    {
        return _bankverbindungList.size();
    } //-- int getBankverbindungCount() 

    /**
     * Returns the Benutzererkennung at the given position in the list.
     *
     * @param index
    **/
    public Benutzererkennung getBenutzererkennung(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _benutzererkennungList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Benutzererkennung) _benutzererkennungList.get(index);
    } //-- Benutzererkennung getBenutzererkennung(int) 

    /** Returns all Benutzererkennung elements as a new array. */
    public Benutzererkennung[] getBenutzererkennung()
    {
        int size = _benutzererkennungList.size();
        Benutzererkennung[] mArray = new Benutzererkennung[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Benutzererkennung) _benutzererkennungList.get(index);
        }
        return mArray;
    } //-- Benutzererkennung[] getBenutzererkennung() 

    /** Returns the number of Benutzererkennung elements in the list. */
    public int getBenutzererkennungCount()
    {
        return _benutzererkennungList.size();
    } //-- int getBenutzererkennungCount() 

    /**
     * Returns the Kommunikation at the given position in the list.
     *
     * @param index
    **/
    public Kommunikation getKommunikation(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kommunikationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Kommunikation) _kommunikationList.get(index);
    } //-- Kommunikation getKommunikation(int) 

    /** Returns all Kommunikation elements as a new array. */
    public Kommunikation[] getKommunikation()
    {
        int size = _kommunikationList.size();
        Kommunikation[] mArray = new Kommunikation[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Kommunikation) _kommunikationList.get(index);
        }
        return mArray;
    } //-- Kommunikation[] getKommunikation() 

    /** Returns the number of Kommunikation elements in the list. */
    public int getKommunikationCount()
    {
        return _kommunikationList.size();
    } //-- int getKommunikationCount() 

    /**
     * Returns the Kreditkarte at the given position in the list.
     *
     * @param index
    **/
    public Kreditkarte getKreditkarte(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kreditkarteList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Kreditkarte) _kreditkarteList.get(index);
    } //-- Kreditkarte getKreditkarte(int) 

    /** Returns all Kreditkarte elements as a new array. */
    public Kreditkarte[] getKreditkarte()
    {
        int size = _kreditkarteList.size();
        Kreditkarte[] mArray = new Kreditkarte[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Kreditkarte) _kreditkarteList.get(index);
        }
        return mArray;
    } //-- Kreditkarte[] getKreditkarte() 

    /** Returns the number of Kreditkarte elements in the list. */
    public int getKreditkarteCount()
    {
        return _kreditkarteList.size();
    } //-- int getKreditkarteCount() 

    /**
     * Returns the value of field 'kunde'.
     * 
     * @return the value of field 'kunde'.
    **/
    public Kunde getKunde()
    {
        return this._kunde;
    } //-- Kunde getKunde() 

    /**
     * Returns the KundeInSystem at the given position in the list.
     *
     * @param index
    **/
    public KundeInSystem getKundeInSystem(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundeInSystemList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (KundeInSystem) _kundeInSystemList.get(index);
    } //-- KundeInSystem getKundeInSystem(int) 

    /** Returns all KundeInSystem elements as a new array. */
    public KundeInSystem[] getKundeInSystem()
    {
        int size = _kundeInSystemList.size();
        KundeInSystem[] mArray = new KundeInSystem[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (KundeInSystem) _kundeInSystemList.get(index);
        }
        return mArray;
    } //-- KundeInSystem[] getKundeInSystem() 

    /** Returns the number of KundeInSystem elements in the list. */
    public int getKundeInSystemCount()
    {
        return _kundeInSystemList.size();
    } //-- int getKundeInSystemCount() 

    /**
     * Returns the Kundenbeziehung at the given position in the list.
     *
     * @param index
    **/
    public Kundenbeziehung getKundenbeziehung(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundenbeziehungList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Kundenbeziehung) _kundenbeziehungList.get(index);
    } //-- Kundenbeziehung getKundenbeziehung(int) 

    /** Returns all Kundenbeziehung elements as a new array. */
    public Kundenbeziehung[] getKundenbeziehung()
    {
        int size = _kundenbeziehungList.size();
        Kundenbeziehung[] mArray = new Kundenbeziehung[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Kundenbeziehung) _kundenbeziehungList.get(index);
        }
        return mArray;
    } //-- Kundenbeziehung[] getKundenbeziehung() 

    /** Returns the number of Kundenbeziehung elements in the list. */
    public int getKundenbeziehungCount()
    {
        return _kundenbeziehungList.size();
    } //-- int getKundenbeziehungCount() 

    /**
     * Returns the Kundenkarte at the given position in the list.
     *
     * @param index
    **/
    public Kundenkarte getKundenkarte(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundenkarteList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Kundenkarte) _kundenkarteList.get(index);
    } //-- Kundenkarte getKundenkarte(int) 

    /** Returns all Kundenkarte elements as a new array. */
    public Kundenkarte[] getKundenkarte()
    {
        int size = _kundenkarteList.size();
        Kundenkarte[] mArray = new Kundenkarte[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Kundenkarte) _kundenkarteList.get(index);
        }
        return mArray;
    } //-- Kundenkarte[] getKundenkarte() 

    /** Returns the number of Kundenkarte elements in the list. */
    public int getKundenkarteCount()
    {
        return _kundenkarteList.size();
    } //-- int getKundenkarteCount() 

    /**
     * Returns the Kundenmerkmal at the given position in the list.
     *
     * @param index
    **/
    public Kundenmerkmal getKundenmerkmal(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundenmerkmalList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Kundenmerkmal) _kundenmerkmalList.get(index);
    } //-- Kundenmerkmal getKundenmerkmal(int) 

    /** Returns all Kundenmerkmal elements as a new array. */
    public Kundenmerkmal[] getKundenmerkmal()
    {
        int size = _kundenmerkmalList.size();
        Kundenmerkmal[] mArray = new Kundenmerkmal[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Kundenmerkmal) _kundenmerkmalList.get(index);
        }
        return mArray;
    } //-- Kundenmerkmal[] getKundenmerkmal() 

    /** Returns the number of Kundenmerkmal elements in the list. */
    public int getKundenmerkmalCount()
    {
        return _kundenmerkmalList.size();
    } //-- int getKundenmerkmalCount() 

    /**
     * Returns the Rolle at the given position in the list.
     *
     * @param index
    **/
    public Rolle getRolle(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _rolleList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Rolle) _rolleList.get(index);
    } //-- Rolle getRolle(int) 

    /** Returns all Rolle elements as a new array. */
    public Rolle[] getRolle()
    {
        int size = _rolleList.size();
        Rolle[] mArray = new Rolle[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Rolle) _rolleList.get(index);
        }
        return mArray;
    } //-- Rolle[] getRolle() 

    /** Returns the number of Rolle elements in the list. */
    public int getRolleCount()
    {
        return _rolleList.size();
    } //-- int getRolleCount() 

    /**
     * Removes the given Adresse from the list.
     *
     * @param vAdresse
    **/
    public boolean removeAdresse(Adresse vAdresse)
    {
        boolean removed = _adresseList.remove(vAdresse);
        return removed;
    } //-- boolean removeAdresse(Adresse) 

    /**
     * Removes the given Bankverbindung from the list.
     *
     * @param vBankverbindung
    **/
    public boolean removeBankverbindung(Bankverbindung vBankverbindung)
    {
        boolean removed = _bankverbindungList.remove(vBankverbindung);
        return removed;
    } //-- boolean removeBankverbindung(Bankverbindung) 

    /**
     * Removes the given Benutzererkennung from the list.
     *
     * @param vBenutzererkennung
    **/
    public boolean removeBenutzererkennung(Benutzererkennung vBenutzererkennung)
    {
        boolean removed = _benutzererkennungList.remove(vBenutzererkennung);
        return removed;
    } //-- boolean removeBenutzererkennung(Benutzererkennung) 

    /**
     * Removes the given Kommunikation from the list.
     *
     * @param vKommunikation
    **/
    public boolean removeKommunikation(Kommunikation vKommunikation)
    {
        boolean removed = _kommunikationList.remove(vKommunikation);
        return removed;
    } //-- boolean removeKommunikation(Kommunikation) 

    /**
     * Removes the given Kreditkarte from the list.
     *
     * @param vKreditkarte
    **/
    public boolean removeKreditkarte(Kreditkarte vKreditkarte)
    {
        boolean removed = _kreditkarteList.remove(vKreditkarte);
        return removed;
    } //-- boolean removeKreditkarte(Kreditkarte) 

    /**
     * Removes the given KundeInSystem from the list.
     *
     * @param vKundeInSystem
    **/
    public boolean removeKundeInSystem(KundeInSystem vKundeInSystem)
    {
        boolean removed = _kundeInSystemList.remove(vKundeInSystem);
        return removed;
    } //-- boolean removeKundeInSystem(KundeInSystem) 

    /**
     * Removes the given Kundenbeziehung from the list.
     *
     * @param vKundenbeziehung
    **/
    public boolean removeKundenbeziehung(Kundenbeziehung vKundenbeziehung)
    {
        boolean removed = _kundenbeziehungList.remove(vKundenbeziehung);
        return removed;
    } //-- boolean removeKundenbeziehung(Kundenbeziehung) 

    /**
     * Removes the given Kundenkarte from the list.
     *
     * @param vKundenkarte
    **/
    public boolean removeKundenkarte(Kundenkarte vKundenkarte)
    {
        boolean removed = _kundenkarteList.remove(vKundenkarte);
        return removed;
    } //-- boolean removeKundenkarte(Kundenkarte) 

    /**
     * Removes the given Kundenmerkmal from the list.
     *
     * @param vKundenmerkmal
    **/
    public boolean removeKundenmerkmal(Kundenmerkmal vKundenmerkmal)
    {
        boolean removed = _kundenmerkmalList.remove(vKundenmerkmal);
        return removed;
    } //-- boolean removeKundenmerkmal(Kundenmerkmal) 

    /**
     * Removes the given Rolle from the list.
     *
     * @param vRolle
    **/
    public boolean removeRolle(Rolle vRolle)
    {
        boolean removed = _rolleList.remove(vRolle);
        return removed;
    } //-- boolean removeRolle(Rolle) 

    /**
     * Replaces the Adresse at the given position in the list.
     *
     * @param index
     * @param vAdresse
    **/
    public void setAdresse(int index, Adresse vAdresse)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _adresseList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _adresseList.set(index, vAdresse);
    } //-- void setAdresse(int, Adresse) 

    /**
     * Replaces the entire list of Adresse elements with the given array's contents.
     *
     * @param adresseArray
    **/
    public void setAdresse(Adresse[] adresseArray)
    {
        //-- copy array
        _adresseList.clear();
        for (int i = 0; i < adresseArray.length; i++) {
            _adresseList.add(adresseArray[i]);
        }
    } //-- void setAdresse(Adresse) 

    /**
     * Replaces the Bankverbindung at the given position in the list.
     *
     * @param index
     * @param vBankverbindung
    **/
    public void setBankverbindung(int index, Bankverbindung vBankverbindung)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _bankverbindungList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _bankverbindungList.set(index, vBankverbindung);
    } //-- void setBankverbindung(int, Bankverbindung) 

    /**
     * Replaces the entire list of Bankverbindung elements with the given array's contents.
     *
     * @param bankverbindungArray
    **/
    public void setBankverbindung(Bankverbindung[] bankverbindungArray)
    {
        //-- copy array
        _bankverbindungList.clear();
        for (int i = 0; i < bankverbindungArray.length; i++) {
            _bankverbindungList.add(bankverbindungArray[i]);
        }
    } //-- void setBankverbindung(Bankverbindung) 

    /**
     * Replaces the Benutzererkennung at the given position in the list.
     *
     * @param index
     * @param vBenutzererkennung
    **/
    public void setBenutzererkennung(int index, Benutzererkennung vBenutzererkennung)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _benutzererkennungList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _benutzererkennungList.set(index, vBenutzererkennung);
    } //-- void setBenutzererkennung(int, Benutzererkennung) 

    /**
     * Replaces the entire list of Benutzererkennung elements with the given array's contents.
     *
     * @param benutzererkennungArray
    **/
    public void setBenutzererkennung(Benutzererkennung[] benutzererkennungArray)
    {
        //-- copy array
        _benutzererkennungList.clear();
        for (int i = 0; i < benutzererkennungArray.length; i++) {
            _benutzererkennungList.add(benutzererkennungArray[i]);
        }
    } //-- void setBenutzererkennung(Benutzererkennung) 

    /**
     * Replaces the Kommunikation at the given position in the list.
     *
     * @param index
     * @param vKommunikation
    **/
    public void setKommunikation(int index, Kommunikation vKommunikation)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kommunikationList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _kommunikationList.set(index, vKommunikation);
    } //-- void setKommunikation(int, Kommunikation) 

    /**
     * Replaces the entire list of Kommunikation elements with the given array's contents.
     *
     * @param kommunikationArray
    **/
    public void setKommunikation(Kommunikation[] kommunikationArray)
    {
        //-- copy array
        _kommunikationList.clear();
        for (int i = 0; i < kommunikationArray.length; i++) {
            _kommunikationList.add(kommunikationArray[i]);
        }
    } //-- void setKommunikation(Kommunikation) 

    /**
     * Replaces the Kreditkarte at the given position in the list.
     *
     * @param index
     * @param vKreditkarte
    **/
    public void setKreditkarte(int index, Kreditkarte vKreditkarte)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kreditkarteList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _kreditkarteList.set(index, vKreditkarte);
    } //-- void setKreditkarte(int, Kreditkarte) 

    /**
     * Replaces the entire list of Kreditkarte elements with the given array's contents.
     *
     * @param kreditkarteArray
    **/
    public void setKreditkarte(Kreditkarte[] kreditkarteArray)
    {
        //-- copy array
        _kreditkarteList.clear();
        for (int i = 0; i < kreditkarteArray.length; i++) {
            _kreditkarteList.add(kreditkarteArray[i]);
        }
    } //-- void setKreditkarte(Kreditkarte) 

    /**
     * Sets the value of field 'kunde'.
     * 
     * @param kunde the value of field 'kunde'.
    **/
    public void setKunde(Kunde kunde)
    {
        this._kunde = kunde;
    } //-- void setKunde(Kunde) 

    /**
     * Replaces the KundeInSystem at the given position in the list.
     *
     * @param index
     * @param vKundeInSystem
    **/
    public void setKundeInSystem(int index, KundeInSystem vKundeInSystem)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundeInSystemList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _kundeInSystemList.set(index, vKundeInSystem);
    } //-- void setKundeInSystem(int, KundeInSystem) 

    /**
     * Replaces the entire list of KundeInSystem elements with the given array's contents.
     *
     * @param kundeInSystemArray
    **/
    public void setKundeInSystem(KundeInSystem[] kundeInSystemArray)
    {
        //-- copy array
        _kundeInSystemList.clear();
        for (int i = 0; i < kundeInSystemArray.length; i++) {
            _kundeInSystemList.add(kundeInSystemArray[i]);
        }
    } //-- void setKundeInSystem(KundeInSystem) 

    /**
     * Replaces the Kundenbeziehung at the given position in the list.
     *
     * @param index
     * @param vKundenbeziehung
    **/
    public void setKundenbeziehung(int index, Kundenbeziehung vKundenbeziehung)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundenbeziehungList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _kundenbeziehungList.set(index, vKundenbeziehung);
    } //-- void setKundenbeziehung(int, Kundenbeziehung) 

    /**
     * Replaces the entire list of Kundenbeziehung elements with the given array's contents.
     *
     * @param kundenbeziehungArray
    **/
    public void setKundenbeziehung(Kundenbeziehung[] kundenbeziehungArray)
    {
        //-- copy array
        _kundenbeziehungList.clear();
        for (int i = 0; i < kundenbeziehungArray.length; i++) {
            _kundenbeziehungList.add(kundenbeziehungArray[i]);
        }
    } //-- void setKundenbeziehung(Kundenbeziehung) 

    /**
     * Replaces the Kundenkarte at the given position in the list.
     *
     * @param index
     * @param vKundenkarte
    **/
    public void setKundenkarte(int index, Kundenkarte vKundenkarte)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundenkarteList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _kundenkarteList.set(index, vKundenkarte);
    } //-- void setKundenkarte(int, Kundenkarte) 

    /**
     * Replaces the entire list of Kundenkarte elements with the given array's contents.
     *
     * @param kundenkarteArray
    **/
    public void setKundenkarte(Kundenkarte[] kundenkarteArray)
    {
        //-- copy array
        _kundenkarteList.clear();
        for (int i = 0; i < kundenkarteArray.length; i++) {
            _kundenkarteList.add(kundenkarteArray[i]);
        }
    } //-- void setKundenkarte(Kundenkarte) 

    /**
     * Replaces the Kundenmerkmal at the given position in the list.
     *
     * @param index
     * @param vKundenmerkmal
    **/
    public void setKundenmerkmal(int index, Kundenmerkmal vKundenmerkmal)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _kundenmerkmalList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _kundenmerkmalList.set(index, vKundenmerkmal);
    } //-- void setKundenmerkmal(int, Kundenmerkmal) 

    /**
     * Replaces the entire list of Kundenmerkmal elements with the given array's contents.
     *
     * @param kundenmerkmalArray
    **/
    public void setKundenmerkmal(Kundenmerkmal[] kundenmerkmalArray)
    {
        //-- copy array
        _kundenmerkmalList.clear();
        for (int i = 0; i < kundenmerkmalArray.length; i++) {
            _kundenmerkmalList.add(kundenmerkmalArray[i]);
        }
    } //-- void setKundenmerkmal(Kundenmerkmal) 

    /**
     * Replaces the Rolle at the given position in the list.
     *
     * @param index
     * @param vRolle
    **/
    public void setRolle(int index, Rolle vRolle)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _rolleList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _rolleList.set(index, vRolle);
    } //-- void setRolle(int, Rolle) 

    /**
     * Replaces the entire list of Rolle elements with the given array's contents.
     *
     * @param rolleArray
    **/
    public void setRolle(Rolle[] rolleArray)
    {
        //-- copy array
        _rolleList.clear();
        for (int i = 0; i < rolleArray.length; i++) {
            _rolleList.add(rolleArray[i]);
        }
    } //-- void setRolle(Rolle) 

}
