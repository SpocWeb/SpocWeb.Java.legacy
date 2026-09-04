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
 * =============  KOMMUNIKATION TYPE ==================	
 * 				
 * 
 * @version $Revision$ $Date$
**/
public class Kommunikation 
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

    public ZKDBBaseType bevorzugt;

    public ZKDBBaseType verb_info;


      //----------------/
     //- Constructors -/
    //----------------/

    public Kommunikation() {
        super();
    } //-- de.bahn.zkdb.bcm.data.Kommunikation()


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
     * Returns the value of field 'typ'.
     * 
     * @return the value of field 'typ'.
    **/
    public ZKDBBaseType getTyp()
    {
        return this.typ;
    } //-- Typ getTyp() 

    /**
     * Returns the value of field 'verb_info'.
     * 
     * @return the value of field 'verb_info'.
    **/
    public ZKDBBaseType getVerb_info()
    {
        return this.verb_info;
    } //-- Verb_info getVerb_info() 

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
     * Sets the value of field 'typ'.
     * 
     * @param typ the value of field 'typ'.
    **/
    public void setTyp(ZKDBBaseType typ)
    {
        this.typ = typ;
    } //-- void setTyp(Typ) 

    /**
     * Sets the value of field 'verb_info'.
     * 
     * @param verb_info the value of field 'verb_info'.
    **/
    public void setVerb_info(ZKDBBaseType verb_info)
    {
        this.verb_info = verb_info;
    } //-- void setVerb_info(Verb_info) 

}
