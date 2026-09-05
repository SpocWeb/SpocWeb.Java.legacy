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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Castor-generated root value object for the ZKDB "Nachricht" (message) XML type, holding
 * the list of {@link Transaktion} elements it contains.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 2bd48b5d684f1bd43be25bebc29833be2bc1af033f2125f1b3304e53e2636fd6
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class Nachricht 
implements Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private java.util.ArrayList _transaktionList;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates a Nachricht with an empty Transaktion list. */
    public Nachricht() {
        super();
        _transaktionList = new ArrayList();
    } //-- de.bahn.zkdb.bcm.data.Nachricht()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Appends a Transaktion to the end of the list.
     *
     * @param vTransaktion
    **/
    public void addTransaktion(Transaktion vTransaktion)
        throws java.lang.IndexOutOfBoundsException
    {
        _transaktionList.add(vTransaktion);
    } //-- void addTransaktion(Transaktion)

    /**
     * Inserts a Transaktion at the given position in the list.
     *
     * @param index
     * @param vTransaktion
    **/
    public void addTransaktion(int index, Transaktion vTransaktion)
        throws java.lang.IndexOutOfBoundsException
    {
        _transaktionList.add(index, vTransaktion);
    } //-- void addTransaktion(int, Transaktion)

    /** Removes all Transaktion elements from the list. */
    public void clearTransaktion()
    {
        _transaktionList.clear();
    } //-- void clearTransaktion()

    /** Returns an Iterator over all Transaktion elements in the list. */
    public java.util.Iterator enumerateTransaktion()
    {
        return _transaktionList.iterator();
    } //-- Iterator enumerateTransaktion()

    /**
     * Returns the Transaktion at the given position in the list.
     *
     * @param index
    **/
    public Transaktion getTransaktion(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _transaktionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (Transaktion) _transaktionList.get(index);
    } //-- Transaktion getTransaktion(int) 

    /** Returns all Transaktion elements as a new array. */
    public Transaktion[] getTransaktion()
    {
        int size = _transaktionList.size();
        Transaktion[] mArray = new Transaktion[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Transaktion) _transaktionList.get(index);
        }
        return mArray;
    } //-- Transaktion[] getTransaktion()

    /** Returns the number of Transaktion elements in the list. */
    public int getTransaktionCount()
    {
        return _transaktionList.size();
    } //-- int getTransaktionCount()

    /**
     * Removes the given Transaktion from the list.
     *
     * @param vTransaktion
     * @return true if the Transaktion was present and removed
    **/
    public boolean removeTransaktion(Transaktion vTransaktion)
    {
        boolean removed = _transaktionList.remove(vTransaktion);
        return removed;
    } //-- boolean removeTransaktion(Transaktion)

    /**
     * Replaces the Transaktion at the given position in the list.
     *
     * @param index
     * @param vTransaktion
    **/
    public void setTransaktion(int index, Transaktion vTransaktion)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _transaktionList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _transaktionList.set(index, vTransaktion);
    } //-- void setTransaktion(int, Transaktion) 

    /**
     * Replaces the entire list of Transaktion elements with the given array's contents.
     *
     * @param transaktionArray
    **/
    public void setTransaktion(Transaktion[] transaktionArray)
    {
        //-- copy array
        _transaktionList.clear();
        for (int i = 0; i < transaktionArray.length; i++) {
            _transaktionList.add(transaktionArray[i]);
        }
    } //-- void setTransaktion(Transaktion) 

}
