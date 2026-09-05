package function;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import graphs.ICPair;

/**
  * Title: FactoryByType<p>
  * Description:
  * Purpose:
  * Factory that creates Objects, given the fully qualified Class Name, using their empty Constructors.
  * This can be seen as a Specialization of FactoryRegistry,
  * where the Classes don't need to register anymore,
  * since they are registered implicitly by their fully qualified Class Name anyway.
  * The only Disadvantage is the missing Mapping Layer
  * between Class Name and the Name used in retrieving.
  *
  * Design Decisions / Implementation Details:
  * Since this Class has no State (except possibly a specific ClassLoader),
  * it is implemented as a Singleton.
  *
  * SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-06-2002, 11:15 AM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  * <!-- docstate
  * pass: 2
  * mtime: 2026-09-05T10:12:24Z
  * digest: 4fd7420fd421c66b85dc2a7357fc7f173c5085ba11959ed301a67430ad0337ef
  * stale: false
  * tags: [code/function_contract, code/function_composition]
  * concepts: [Function/Relation Contract]
  * facets: {layer: utility, status: legacy, complexity: low}
  * -->
  */
final public class FactoryByType
implements IProcessor {

////////////////////////////////////////////////////////////////////////////////
/// #region : static Constants and Variables
////////////////////////////////////////////////////////////////////////////////

	/** Allows to access the Class as a Variable rather than using getClass	 */
	final static public Class CLASS = FactoryByType.class;

	/** Allows to access the Class Name as a Variable rather than using getClass	 */
	final static public String CLASS_NAME = CLASS.getName();

	/** Since this Class is stateless (excepty maybe for a custom ClassLoader)
	  * it can be implemented as a Singleton.	 */
	final static public FactoryByType FACTORY = new FactoryByType();

////////////////////////////////////////////////////////////////////////////////
/// #region : static Methods
////////////////////////////////////////////////////////////////////////////////

	/** static Factory Method	 */
	final static public Object NEXT_ITEM(String TypeName)
	throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return MAP_AT(TypeName); }

////////////////////////////////////////////////////////////////////////////////
/// #region : Variables
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Constructors, calling each other using this()/super()
////////////////////////////////////////////////////////////////////////////////

	/** private Constructor for a sealed Singleton Class */
	private FactoryByType() {}

////////////////////////////////////////////////////////////////////////////////
/// #region : public Methods, then private Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
/// #region : Interface Processor: Implementation
////////////////////////////////////////////////////////////////////////////////

	/** Member Factory Method	 */
	public Object MapAt(Object TypeName) {
		try {
			return MAP_AT((String) TypeName);
		} catch (Exception x) {
			throw new streamIO.exception.BaseException(CLASS_NAME + ".MapAt("+TypeName+")", x);
		}
	}

	/** static Factory Method taking the Class Name	 */
	final static public Object MAP_AT(String TypeName)
	throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class cls = Class.forName(TypeName);
		return cls.newInstance(); }

	/** static Factory Method using a Pair of Type Name and initializing Parameter.	 */
	final static public Object MAP_AT(ICPair TypeNameAndParam)
	throws
	ClassNotFoundException,
	InstantiationException,
	IllegalAccessException,
	 NoSuchMethodException,
	InvocationTargetException
	{
		Class cls = Class.forName((String)   TypeNameAndParam.getKey  ());
		Object[] params     = { TypeNameAndParam.getVal  ()};
		Class [] paramTypes = { params[0]       .getClass()};
		Constructor cnst = cls.getConstructor(paramTypes);
		return cnst.newInstance(params); }

////////////////////////////////////////////////////////////////////////////////
/// #region : static Testing and main() Methods
////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + CLASS_NAME);
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main (String[] args) { //throws java.io.IOException {
		testIt(args); }

}

