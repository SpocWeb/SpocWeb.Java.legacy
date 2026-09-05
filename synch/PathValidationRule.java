package synch;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
  * Title: PathValidationRule<p>
  * Description:
  * Purpose:
  * Encapsulates a Path together with the Rule.
  * Since the Path is a variable Length Array,
  * it cannot stem from a fixed Size structure.
  * Alternatives are: XML or a Row based variable Length Tokenizer (2D Tokenizer)
  * like ResultsetSep where the Path starts from a given Position
  * and extends with variable Size to the End of the Row.
  * 1D Tokenizer like Properties are not well suited.
  *
  * Design Decisions / Implementation Details:
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-15-2002, 12:57 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:13:32Z
  * digest: ab2dc44c2af6ee88cc8281a526f3b240cffb3602226a379a27d3876f3dcd908d
  * stale: false
  * tags: [code/validation_rule]
  * concepts: [Path-Based Validation Rule]
  * facets: {layer: domain, status: legacy, complexity: medium}
  * -->
  */
public class PathValidationRule
extends ValidationRule {

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

	/** Path to the Value to be validated by this Rule	 */
	protected String[] path;

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** Initializing Constructor well usable for reading Parameters from a File
	  * By directly creating the Instance and directly validating the Method
	  * some Checks can be performed already early on creating the Rule.
	  * @param class_  the Class containing the Validation Method
	  * @param value_  the second Validation Method Parameter
	  * The Class must implement the IValidationRule Interface.
	  */
	public PathValidationRule(ResultSet rs, int classCol, int methodCol, int paramCol, int pathStartCol)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SQLException {
		super(rs, classCol, methodCol, paramCol);
		ResultSetMetaData meta = rs.getMetaData();
		int len = meta.getColumnCount();
		path = new String[len-pathStartCol];
		for (int i = path.length; --i >= 0;) {
			path[i] = rs.getString(i+pathStartCol); }
	}

	/** Initializing Constructor	 */
	public PathValidationRule(String class_, String method_, String value_, String[] path_)
		throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException {
		super(class_, method_, value_); this.path = path_; }

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface : Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Constant for the 'get' prepended to all Accessors */
	final static public String STR_GET = "get";

	/** Validates the given Object by navigating along a DOM  */

	/** Validates the given Object by navigating along an Object Graph  */
	public void validate(Object arg) throws InvalidException {
		validate(arg, 0, ""); }

	/** Validates the given Object by navigating along an Object Graph  */
	private void validate(Object arg, int pathLevel, String basePath) throws InvalidException {
		StringBuffer fullPath=new StringBuffer(basePath);
		try {
			for(; pathLevel < path.length; ++pathLevel) {
				Class cls = arg.getClass();
				if (cls.isArray()) {
					int pathLen = fullPath.length();
					Object[] arr = (Object[]) arg;
					for (int i = arr.length; --i >= 0;) {
						fullPath.append('[').append(i).append(']'); //make it readable
						validate(arr[i], pathLevel, fullPath.toString());
						fullPath.setLength(pathLen); //und the Change
					}
				}
				String strPath = path[pathLevel];
				if ((strPath == null) || (strPath.length() <= 0)) { //stop on empty Paths!
					break; }
				fullPath.append(strPath).append('.');
				Method method = cls.getMethod(STR_GET + strPath, null); //no Parameters
				arg = method.invoke(arg, null);
			}
			super.validate(arg);
		} catch (InvalidException x) { //all Runtime Exceptions and declared Exceptions
			throw new InvalidException(x.getSource(), x.getValue(), x.getMessage()+" for Path '"+"'"); //are rethrown nested.
		} catch (Exception x) { //all Runtime Exceptions and declared Exceptions
			throw new streamIO.exception.BaseException(x); //are rethrown nested.
		}
	}

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + PathValidationRule.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

