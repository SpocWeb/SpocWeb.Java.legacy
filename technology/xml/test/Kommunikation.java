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
 * Castor-generated value object for the ZKDB "Kommunikation" (contact channel) XML type.
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 3617429d44ff7604594bf052d7535f6a407b078e52851a1577093ad3f515f1a8
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
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

	/** Communication channel type discriminator (e.g. phone, e-mail). */
	public ZKDBBaseType typ;

    /** Flag indicating this is the preferred communication channel. */
    public ZKDBBaseType bevorzugt;

    /** Connection-specific detail (e.g. the phone number or e-mail address). */
    public ZKDBBaseType verb_info;


      //----------------/
     //- Constructors -/
    //----------------/

    /** Creates an empty Kommunikation with all fields unset. */
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
