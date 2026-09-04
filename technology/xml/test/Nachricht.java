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
 *  
 *  =============  NACHRICHT TYPE ==================	
 * 				Das Root Element, kann mehrere Transaktionen beinhalten
 * 			
 * 
 * @version $Revision$ $Date$
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

    public Nachricht() {
        super();
        _transaktionList = new ArrayList();
    } //-- de.bahn.zkdb.bcm.data.Nachricht()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * 
     * 
     * @param vTransaktion
    **/
    public void addTransaktion(Transaktion vTransaktion)
        throws java.lang.IndexOutOfBoundsException
    {
        _transaktionList.add(vTransaktion);
    } //-- void addTransaktion(Transaktion) 

    /**
     * 
     * 
     * @param index
     * @param vTransaktion
    **/
    public void addTransaktion(int index, Transaktion vTransaktion)
        throws java.lang.IndexOutOfBoundsException
    {
        _transaktionList.add(index, vTransaktion);
    } //-- void addTransaktion(int, Transaktion) 

    /**
    **/
    public void clearTransaktion()
    {
        _transaktionList.clear();
    } //-- void clearTransaktion() 

    /**
    **/
    public java.util.Iterator enumerateTransaktion()
    {
        return _transaktionList.iterator();
    } //-- Iterator enumerateTransaktion() 

    /**
     * 
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

    /**
    **/
    public Transaktion[] getTransaktion()
    {
        int size = _transaktionList.size();
        Transaktion[] mArray = new Transaktion[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (Transaktion) _transaktionList.get(index);
        }
        return mArray;
    } //-- Transaktion[] getTransaktion() 

    /**
    **/
    public int getTransaktionCount()
    {
        return _transaktionList.size();
    } //-- int getTransaktionCount() 

    /**
     * 
     * 
     * @param vTransaktion
    **/
    public boolean removeTransaktion(Transaktion vTransaktion)
    {
        boolean removed = _transaktionList.remove(vTransaktion);
        return removed;
    } //-- boolean removeTransaktion(Transaktion) 

    /**
     * 
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
     * 
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
