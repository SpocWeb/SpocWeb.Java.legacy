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
 * Castor-generated base value object for every ZKDB XML element carrying a content string,
 * status and modification time (a "Delta-Attribute" element).
 *
 * @version $Revision$ $Date$
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T10:13:32Z
 * digest: 054c92bf4e88b9e2ff34e994923ac483b6da1adcf657ccc83ffc5beba8de9d4d
 * stale: false
 * tags: [code/data_transfer_object]
 * concepts: [Castor Data Transfer Object Base]
 * facets: {layer: domain, status: legacy, complexity: low}
 * -->
**/
public class ZKDBBaseType 
implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StatusValue _status;

    private java.util.Date _mtime;

    /**
     * internal content storage
    **/
    private java.lang.String _content = "";


      //----------------/
     //- Constructors -/
    //----------------/

	/** Creates a ZKDBBaseType with empty content. */
	public ZKDBBaseType() {
		super();
		setContent("");
	} //-- de.bahn.zkdb.bcm.data.ZKDBBaseType()


	/**
	 * Creates a ZKDBBaseType wrapping the given content.
	 * @param content
	 */
	public ZKDBBaseType(final String content) {
		super();
		setContent(content);
	} //-- de.bahn.zkdb.bcm.data.ZKDBBaseType()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Returns the value of field 'content'. The field 'content'
     * has the following description: internal content storage
     * 
     * @return the value of field 'content'.
    **/
    public java.lang.String getContent()
    {
        return this._content;
    } //-- java.lang.String getContent() 

    /**
     * Returns the value of field 'mtime'.
     * 
     * @return the value of field 'mtime'.
    **/
    public java.util.Date getMtime()
    {
        return this._mtime;
    } //-- java.util.Date getMtime() 

    /**
     * Returns the value of field 'status'.
     * 
     * @return the value of field 'status'.
    **/
    public StatusValue getStatus()
    {
        return this._status;
    } //-- de.bahn.zkdb.bcm.data.types.StatusValueType getStatus() 

    /**
     * Sets the value of field 'content'. The field 'content' has
     * the following description: internal content storage
     * 
     * @param content the value of field 'content'.
    **/
    public void setContent(java.lang.String content)
    {
        this._content = content;
    } //-- void setContent(java.lang.String) 

    /**
     * Sets the value of field 'mtime'.
     * 
     * @param mtime the value of field 'mtime'.
    **/
    public void setMtime(java.util.Date mtime)
    {
        this._mtime = mtime;
    } //-- void setMtime(java.util.Date) 

	/**
	 * Sets the value of field 'status'.
	 * 
	 * @param status the value of field 'status'.
	**/
	public void setStatus(StatusValue status)
	{
		this._status = status;
	} //-- void setStatus(de.bahn.zkdb.bcm.data.types.StatusValueType) 

	/**
	 * Sets the value of field 'status'.
	 * 
	 * @param status the value of field 'status'.
	**/
	public void setStatus(String status)
	{
		this._status = StatusValue.valueOf(status);
	} //-- void setStatus(de.bahn.zkdb.bcm.data.types.StatusValueType) 

	/**
	 * Returns this instance's content string.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this._content;
	}

}
