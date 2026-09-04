package persistences;

import java.sql.ResultSet;
import java.sql.SQLException;

import knowledge.IDescriptor;

/**
  * Title: Objekt<p>
  * Description:
  * Base Object for any persistable Class.
  * Defines Fields and Accessors for the Name and Description
  *
  * Known SubClasses:
  *
  * @see knowledge.IDescriptor defines Methods for accessing the Name and Description of an Object.
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	2001-01-10, 06;07;04<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Objekt
extends PersistedObject
implements IDescriptor
{

////////////////////////////////////////////////////////////////////////////////
//  static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  static Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Variables
////////////////////////////////////////////////////////////////////////////////

	/** Name of this Object	 */
	protected final String Name;

	/** Description of this Object	 */
	protected final String Description;

////////////////////////////////////////////////////////////////////////////////
//  Accessor Methods (getXXX/setXXX) from Interface IDescriptor
////////////////////////////////////////////////////////////////////////////////

	/** @return Name of this Object
	  * final does only work up to the Constructor */
//	public void setName(String Name_) { Name = Name_; }

	/** @return Name of this Object	 */
	public String getName() { return Name; }

	/** @return Description of this Object
	  * final does only work up to the Constructor */
//	public void setDescription(String Description_) { this.Description = Description_; }

	/** @return Description of this Object	 */
	public String getDescription() { return Description; }

////////////////////////////////////////////////////////////////////////////////
//  Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor
	  * The Name is expected in the Column named "Name"
	  * The Description is expected in the Column named "Description"
	  */
	public Objekt(ResultSet rs) throws SQLException {
		super(rs); //have to call the super Constructor first!
		this.Description = rs.getString("Description");
		this.Name = rs.getString("Name"); }	//then get the other fields

	/** Initializing Constructor	 */
	protected Objekt(String ID_, String Name_, String Description_) {
		super(ID_);
		this.Name = Name_;
		this.Description = Description_;
	}

////////////////////////////////////////////////////////////////////////////////
//  public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface TODO: abstract Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  Interface TODO: Implementation
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
//  static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) throws java.io.IOException {
		System.out.println("Testing " + Objekt.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) throws java.io.IOException {
		testIt(args); }

}
