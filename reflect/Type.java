package reflect;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.ProtectionDomain;

/**
  * Title: Type<p>
  * Description:
  * Purpose:
  * Class of all Types, i.e. Sets described by common Properties and Methods instead of being Collections of Items
  *
  * instanceof
  * newInstance is not supported, 
  * because there is no Standard Class for an Interface. 
  * the Standard instanceOf Operator 
  * is exactly the correct Method to determine Child Relations. 
  * 
  * @see java.lang.Class which cannot be inherited from.
  * It contains Lists of Method and Field Objects
  * describing the common Elements of all Members of this Type.
  *
  * Additionally this Class contains Reflection Methods
  * to create Instances and Class Objects.
  *
  * Design Decisions / Implementation Details:
  * If similar Classes exist (e.g. Polymorphism),
  * characterize the specific Differences to compare these.
  *
  * Known SubClasses: <none>
  *
  * Known Uses: <none>
  *
  * Copyright:	Copyright (c) Matthias Heuer<p>
  * Company:	personal<p>
  * Created on	10-29-2002, 10:40 PM<p>
  * @author 	Matthias Heuer
  * @version	1.0
  */
public class Type implements IType
//extends Class //not possible, since final
{

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Constants and Variables
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Variables
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reference to the Interface represented by this Type 
	 * Only Interfaces are allowed 
	 * and every Interface must be represented by a Type Instance. 
	 */
	private Class cls;

	/**
	 * Reference to the Parent Types
	 * Instead of single Inheritance, multiple Inheritance is used.
	 * For this Reason only Methods are inherited, but not Fields.
	 *
	 * This corresponds to the getInterfaces() Method of Class.
	 *
	 * The Necessity for multiple Inheritance lies in the Fact,
	 * that the same Object can be Member of several Classes
	 * which reflect different Aspects of this Object.
	 *
	 * Additionally this Object can be Member of many Sets
	 * with no specific Characteristics.
	 */
	//	protected Type[] parentTypes;

	/**
	 * Reference to the Parent Types
	 * Instead of single Inheritance, multiple Inheritance is used.
	 * For this Reason only Methods are inherited, but not Fields.
	 *
	 * This corresponds to the getInterfaces() Method of Class.
	 *
	 * The Necessity for multiple Inheritance lies in the Fact,
	 * that the same Object can be Member of several Classes
	 * which reflect different Aspects of this Object.
	 *
	 * Additionally this Object can be Member of many Sets
	 * with no specific Characteristics.
	 */
	//	protected Collection members = new ArrayList();

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Accessor Methods (getXXX/isXXX/setXXX)
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * This requires a base Constructor
	 * which registers an Object with its Type,
	 * so the Relation to the Type is bidirectional.
	 *
	 * Rather than an Object being direct Member of several Types,
	 * a separate Type has to be created that is a Subtype of several Types.
	 *
	 * @return an Array of all currently loaded Members of this Type.
	 */
	//	protected Object[] getMembers() {
	//		return members.toArray(); }

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Constructors, calling each other using this()/super()
	////////////////////////////////////////////////////////////////////////////////

	/** Empty Constructor	 */
	public Type(Class cls_) {
		if (!cls.isInterface()) {
			throw new ClassCastException("Only Interfaces are allowed!");
		}
		this.cls = cls_;
	}

	////////////////////////////////////////////////////////////////////////////////
	/// #region : public Methods, then private Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent : abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Parent : Implementation / Overrides
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface : abstract Methods
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : Interface : Implementation
	////////////////////////////////////////////////////////////////////////////////

	////////////////////////////////////////////////////////////////////////////////
	/// #region : static Testing and main() Methods
	////////////////////////////////////////////////////////////////////////////////

	/** Tests all Methods of this Class	 */
	public static void testIt(String[] args) { //throws java.io.IOException {
		System.out.println("Testing " + Type.class.getName());
	}

	/**The main entry point for the application.
	 *
	 * @param args Array of parameters passed to the application
	 * via the command line.	 */
	public static void main(String[] args) { //throws java.io.IOException {
		testIt(args);
	}

	/**
	 * Converts the object to a string.
	 * The string representation is the string "class" or "interface", followed by a space,
	 * and then by the fully qualified name of the class in the format returned by getName.
	 * If this Class object represents a primitive type,
	 * this method returns the name of the primitive type.
	 * If this Class object represents void this method returns "void".
	 *
	 * a string representation of this class object.
	 */
	public String toString() {
		return cls.toString();
	}

	/**
	 * Returns the Class object associated with the class or interface with the given string name.
	 * Invoking this method is equivalent to:
	 * Class.forName(className, true, currentLoader)
	 * where currentLoader denotes the defining class loader of the current class.
	 * A call to forName("X") causes the class named X to be initialized.
	 *
	 * @param className - the fully qualified name of the desired class.
	 * @return the Class object for the class with the specified name.
	 * @throws LinkageError - if the linkage fails
	 * @throws ExceptionInInitializerError - if the initialization provoked by this method fails
	 * @throws ClassNotFoundException - if the class cannot be located
	 */
	public static Class forName(String className) throws ClassNotFoundException {
		return Class.forName(className);
	}

	/**
	 * Returns the Class object associated with the class or interface
	 * with the given string name, using the given class loader.
	 * Given the fully qualified name for a class or interface
	 * (in the same format returned by getName)
	 * this method attempts to locate, load, and link the class or interface.
	 * The specified class loader is used to load the class or interface.
	 * If the parameter loader is null, the class is loaded through the bootstrap class loader.
	 * The class is initialized only if the initialize parameter is true
	 * and if it has not been initialized earlier.
	 * If name denotes a primitive type or void,
	 * an attempt will be made to locate a user-defined class in the unnamed package whose name is name.
	 * Therefore, this method cannot be used to obtain any of the Class objects representing primitive types or void.
	 *
	 * If name denotes an array class,
	 * the component type of the array class is loaded but not initialized.
	 *
	 * Note that this method throws errors related to loading, linking or initializing
	 * as specified in Sections 12.2, 12.3 and 12.4 of The Java Language Specification.
	 *
	 * If the loader is null, and a security manager is present, and the caller's class loader is not null,
	 * then this method calls the security manager's checkPermission method with a RuntimePermission("getClassLoader") permission
	 * to ensure it's ok to access the bootstrap class loader.
	 *
	 * @param name - fully qualified name of the desired class
	 * @param initialize - whether the class must be initialized
	 * @param loader - class loader from which the class must be loaded
	 * @return class object representing the desired class
	 * @throw LinkageError - if the linkage fails
	 * @throw ExceptionInInitializerError - if the initialization provoked by this method fails
	 * @throw ClassNotFoundException - if the class cannot be located by the specified class loader
	 *
	 * @see forName(String)
	 * @see ClassLoader
	 */
	public static Type forName(String name, boolean initialize, ClassLoader loader) throws ClassNotFoundException {
		return new Type(Class.forName(name, initialize, loader));
	}

	/**
	 * Creates a new instance of the class represented by this Class object.
	 * The class is instantiatied as if by a new expression with an empty argument list.
	 * The class is initialized if it has not already been initialized.
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.PUBLIC as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @return a newly allocated instance of the class represented by this object.
	 * @throw IllegalAccessException - if the class or initializer is not accessible.
	 * @throw InstantiationException - if this Class represents an abstract class, an interface, an array class, a primitive type, or void; or if the instantiation fails for some other reason.
	 * @throw ExceptionInInitializerError - if the initialization provoked by this method fails.
	 * @throw SecurityException - if there is no permission to create a new instance.
	 */
	public Object newInstance() throws InstantiationException, IllegalAccessException {
		return cls.newInstance();
	}

	/**
	 * Determines if the specified Object is assignment-compatible
	 * with the object represented by this Class.
	 * This method is the dynamic equivalent of the Java language instanceof operator.
	 * The method returns true if the specified Object argument is non-null
	 * and can be cast to the reference type represented by this Class object without raising a ClassCastException.
	 * It returns false otherwise.
	 * Specifically, if this Class object represents a declared class,
	 * this method returns true if the specified Object argument
	 * is an instance of the represented class (or of any of its subclasses);
	 * it returns false otherwise.
	 * If this Class object represents an array class,
	 * this method returns true if the specified Object argument can be converted
	 * to an object of the array class by an identity conversion or by a widening reference conversion;
	 * it returns false otherwise.
	 * If this Class object represents an interface, this method returns true
	 * if the class or any superclass of the specified Object argument
	 * implements this interface; it returns false otherwise.
	 * If this Class object represents a primitive type, this method returns false.
	 *
	 * @param obj - the object to check
	 * @return true if obj is an instance of this class
	 */
	public boolean isInstance(Object obj) {
		return cls.isInstance(obj);
	}

	/**
	 * Determines if the class or interface represented by this Class object
	 * is either the same as, or is a superclass or superinterface of,
	 * the class or interface represented by the specified Class parameter.
	 * It returns true if so; otherwise it returns false.
	 * If this Class object represents a primitive type,
	 * this method returns true if the specified Class parameter
	 * is exactly this Class object; otherwise it returns false.
	 * Specifically, this method tests whether the type represented by the specified Class parameter
	 * can be converted to the type represented by this Class object
	 * via an identity conversion or via a widening reference conversion.
	 *
	 * @param cls - the Class object to be checked
	 * @return the boolean value indicating whether objects of the type cls can be assigned to objects of this class
	 * @throw NullPointerException - if the specified Class parameter is null.
	 */
	public boolean isAssignableFrom(Class cls) {
		return cls.isAssignableFrom(cls);
	}

	/**
	 * Determines if the specified Class object represents an interface type.
	 * @return true if this object represents an interface; false otherwise.
	 */
	public boolean isInterface() {
		return cls.isInterface();
	}

	/**
	 * Determines if this Class object represents an array class.
	 * @return true if this object represents an array class; false otherwise.
	 */
	public boolean isArray() {
		return cls.isArray();
	}

	/**
	 * Determines if the specified Class object represents a primitive type.
	 * There are nine predefined Class objects to represent the eight primitive types and void.
	 * These are created by the Java Virtual Machine,
	 * and have the same names as the primitive types that they represent,
	 * namely boolean, byte, char, short, int, long, float, and double.
	 *
	 * These objects may only be accessed via the following final static public variables,
	 * and are the only Class objects for which this method returns true.
	 *
	 * @return true if and only if this class represents a primitive type
	 * @see Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE,
	 * Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE
	 */
	public boolean isPrimitive() {
		return cls.isPrimitive();
	}

	/**
	 * Returns the fully-qualified name of the entity
	 * (class, interface, array class, primitive type, or void)
	 * represented by this Class object, as a String.
	 * If this Class object represents a class of arrays,
	 * then the internal form of the name consists of the name of the element type in Java signature format,
	 * preceded by one or more "[" characters representing the depth of array nesting.
	 * The encoding of element type names is as follows:
	 * B            byte
	 * C            char
	 * D            double
	 * F            float
	 * I            int
	 * J            long
	 * Lclassname;  class or interface
	 * S            short
	 * Z            boolean
	 *
	 * The class or interface name classname is given in fully qualified form.
	 * @return the fully qualified name of the class or interface
	 * represented by this object.
	 */
	public String getName() {
		return cls.getName();
	}

	/**
	 * Returns the class loader for the class.
	 * Some implementations may use null to represent the bootstrap class loader.
	 * This method will return null in such implementations if this class was loaded by the bootstrap class loader.
	 *
	 * If a security manager is present, and the caller's class loader is not null
	 * and the caller's class loader is not the same as or an ancestor of the class loader for the class whose class loader is requested,
	 * then this method calls the security manager's checkPermission method with a RuntimePermission("getClassLoader") permission
	 * to ensure it's ok to access the class loader for the class.
	 *
	 * If this object represents a primitive type or void, null is returned.
	 * @return the class loader that loaded the class or interface represented by this object.
	 * @throw SecurityException - if a security manager exists
	 * and its checkPermission method denies access to the class loader for the class.
	 * @see ClassLoader, SecurityManager.checkPermission(java.security.Permission), RuntimePermission
	 */
	public ClassLoader getClassLoader() {
		return cls.getClassLoader();
	}

	/**
	 * Returns the Class representing the superclass of the entity
	 * (class, interface, primitive type or void) represented by this Class.
	 * If this Class represents either the Object class, an interface,
	 * a primitive type, or void, then null is returned.
	 * If this object represents an array class
	 * then the Class object representing the Object class is returned.
	 * @return the superclass of the class represented by this object.
	 */
	public Class getSuperclass() {
		return cls.getSuperclass();
	}

	/**
	 * Gets the package for this class.
	 * The class loader of this class is used to find the package.
	 * If the class was loaded by the bootstrap class loader
	 * the set of packages loaded from CLASSPATH is searched
	 * to find the package of the class.
	 * Null is returned if no package object was created by the class loader of this class.
	 * Packages have attributes for versions and specifications
	 * only if the information was defined in the manifests that accompany the classes,
	 * and if the class loader created the package instance with the attributes from the manifest.
	 *
	 * @return the package of the class,
	 * or null if no package information is available from the archive or codebase.
	 */
	public Package getPackage() {
		return cls.getPackage();
	}

	/**
	 * Determines the interfaces implemented by the class or interface represented by this object.
	 * If this object represents a class, the return value is an array
	 * containing objects representing all interfaces implemented by the class.
	 * The order of the interface objects in the array
	 * corresponds to the order of the interface names in the implements clause
	 * of the declaration of the class represented by this object.
	 * If this object represents an interface,
	 * the array contains objects representing all interfaces extended by the interface.
	 * The order of the interface objects in the array
	 * corresponds to the order of the interface names in the extends clause
	 * of the declaration of the interface represented by this object.
	 *
	 * If this object represents a class or interface that implements no interfaces,
	 * the method returns an array of length 0.
	 *
	 * If this object represents a primitive type or void, the method returns an array of length 0.
	 *
	 * @return an array of interfaces implemented by this class.
	 */
	public Class[] getInterfaces() {
		return cls.getInterfaces();
	}

	/**
	 * Returns the Class representing the component type of an array.
	 * If this class does not represent an array class this method returns null.
	 * @return the Class representing the component type of this class if this class is an array
	 *
	 * @see Array
	 */
	public Class getComponentType() {
		return cls.getComponentType();
	}

	/**
	 * Returns the Java language modifiers for this class or interface, encoded in an integer.
	 * The modifiers consist of the Java Virtual Machine's constants
	 * for public, protected, private, final, static, abstract and interface;
	 * they should be decoded using the methods of class Modifier.
	 * If the underlying class is an array class,
	 * then its public, private and protected modifiers are the same as those of its component type.
	 * If this Class represents a primitive type or void, its public modifier is always true,
	 * and its protected and private modifers are always false.
	 * If this object represents an array class, a primitive type or void,
	 * then its final modifier is always true and its interface modifer is always false.
	 * The values of its other modifiers are not determined by this specification.
	 *
	 * The modifier encodings are defined in The Java Virtual Machine Specification, table 4.1.
	 *
	 * @return the int representing the modifiers for this class
	 * @see Modifier
	 */
	public int getModifiers() {
		return cls.getModifiers();
	}

	/**
	 * Gets the signers of this class.
	 * @return the signers of this class, or null if there are no signers.
	 * In particular, this method returns null if this object represents a primitive type or void.
	 */
	public Object[] getSigners() {
		return cls.getSigners();
	}

	/**
	 * If the class or interface represented by this Class object
	 * is a member of another class, returns the Class object representing the class in which it was declared.
	 * This method returns null if this class or interface is not a member of any other class.
	 * If this Class object represents an array class, a primitive type, or void,then this method returns null.
	 *
	 * @return the declaring class for this class
	 */
	public Class getDeclaringClass() {
		return cls.getDeclaringClass();
	}

	/**
	 * Returns an array containing Class objects
	 * representing all the public classes and interfaces
	 * that are members of the class represented by this Class object.
	 * This includes public class and interface members inherited from superclasses
	 * and public class and interface members declared by the class.
	 * This method returns an array of length 0
	 * if this Class object has no public member classes or interfaces.
	 * This method also returns an array of length 0
	 * if this Class object represents a primitive type, an array class, or void.
	 *
	 * For this class and each of its superclasses,
	 * the following security checks are performed:
	 * If there is a security manager,
	 * the security manager's checkMemberAccess method is called
	 * with this and Member.PUBLIC as its arguments,
	 * where this is this class or the superclass whose members are being determined.
	 * If the class is in a package, then the security manager's checkPackageAccess method is also called
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @return the array of Class objects representing the public members of this class
	 * @throw SecurityException - if access to the information is denied.
	 *
	 * @see SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Class[] getClasses() {
		return cls.getClasses();
	}

	/**
	 * Returns an array containing Field objects
	 * reflecting all the accessible public fields of the class or interface represented by this Class object.
	 * The elements in the array returned are not sorted and are not in any particular order.
	 * This method returns an array of length 0 if the class or interface has no accessible public fields,
	 * or if it represents an array class, a primitive type, or void.
	 *
	 * Specifically, if this Class object represents a class,
	 * this method returns the public fields of this class and of all its superclasses.
	 * If this Class object represents an interface,
	 * this method returns the fields of this interface and of all its superinterfaces.
	 *
	 * If there is a security manager,
	 * this method first calls the security manager's checkMemberAccess method
	 * with this and Member.PUBLIC as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * The implicit length field for array classs is not reflected by this method.
	 * User code should use the methods of class Array to manipulate arrays.
	 *
	 * @return the array of Field objects representing the public fields
	 * @throw SecurityException - if access to the information is denied.
	 * @see Field, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Field[] getFields() throws SecurityException {
		return cls.getFields();
	}

	/**
	 * Returns an array containing Constructor objects reflecting all the public constructors
	 * of the class represented by this Class object.
	 * An array of length 0 is returned if the class has no public constructors,
	 * or if the class is an array class, or if the class reflects a primitive type or void.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.PUBLIC as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @return the array containing Method objects for all the declared public constructors of this class matches the specified parameterTypes
	 * @throw SecurityException - if access to the information is denied.
	 * @see Constructor, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Constructor[] getConstructors() throws SecurityException {
		return cls.getConstructors();
	}

	/**
	 * Returns a Field object that reflects the specified public member field
	 * of the class or interface represented by this Class object.
	 * The name parameter is a String specifying the simple name of the desired field.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.PUBLIC as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * The field to be reflected is determined by the algorithm that follows.
	 * Let C be the class represented by this object:
	 * If C declares a public field with the name specified, that is the field to be reflected.
	 * If no field was found in step 1 above, this algorithm is applied recursively to each direct superinterface of C. The direct superinterfaces are searched in the order they were declared.
	 * If no field was found in steps 1 and 2 above, and C has a superclass S, then this algorithm is invoked recursively upon S. If C has no superclass, then a NoSuchFieldException is thrown.
	 *
	 * @param name - the field name
	 * @return the Field object of this class specified by name
	 * @throw NoSuchFieldException - if a field with the specified name is not found.
	 * @throw SecurityException - if access to the information is denied.
	 * @see Field, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Field getField(String name) throws NoSuchFieldException, SecurityException {
		return cls.getField(name);
	}

	/**
	 * Returns a Method object that reflects the specified public member method
	 * of the class or interface represented by this Class object.
	 * The name parameter is a String specifying the simple name the desired method.
	 * The parameterTypes parameter is an array of Class objects
	 * that identify the method's formal parameter types, in declared order.
	 * If parameterTypes is null, it is treated as if it were an empty array.
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.PUBLIC as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * If the name is "<init>"or "<clinit>" a NoSuchMethodException is raised.
	 * Otherwise, the method to be reflected is determined by the algorithm that follows.
	 * Let C be the class represented by this object:
	 * C is searched for any matching methods.
	 * If no matching method is found, the algorithm of step 1 is invoked recursively on the superclass of C.
	 * If no method was found in step 1 above, the superinterfaces of C are searched for a matching method.
	 * If any such method is found, it is reflected.
	 * To find a matching method in a class C:
	 * If C declares exactly one public method with the specified name
	 * and exactly the same formal parameter types, that is the method reflected.
	 * If more than one such method is found in C, and one of these methods has a return type
	 * that is more specific than any of the others, that method is reflected;
	 * otherwise one of the methods is chosen arbitrarily.
	 *
	 * @param name - the name of the method
	 * @param parameterTypes - the list of parameters
	 * @return the Method object that matches the specified name and parameterTypes
	 * @throw NoSuchMethodException - if a matching method is not found or if then name is "<init>"or "<clinit>".
	 * @throw SecurityException - if access to the information is denied.
	 * @see Method, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Method[] getMethods() throws SecurityException {
		return cls.getMethods();
	}

	/**
	 * Returns a Constructor object that reflects the specified public constructor
	 * of the class represented by this Class object.
	 * The parameterTypes parameter is an array of Class objects
	 * that identify the constructor's formal parameter types, in declared order.
	 * The constructor to reflect is the public constructor of the class
	 * represented by this Class object whose formal parameter types match those specified by parameterTypes.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.PUBLIC as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @param parameterTypes - the parameter array
	 * @return the Method object of the public constructor that matches the specified parameterTypes
	 * @throws NoSuchMethodException - if a matching method is not found.
	 * @throws SecurityException - if access to the information is denied.
	 *
	 * @see Constructor, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Constructor getConstructor(Class[] parameterTypes) throws NoSuchMethodException, SecurityException {
		return cls.getConstructor(parameterTypes);
	}

	/**
	 * Returns an array of Class objects reflecting all the classes and interfaces
	 * declared as members of the class represented by this Class object.
	 * This includes public, protected, default (package) access, and private classes and interfaces
	 * declared by the class, but excludes inherited classes and interfaces.
	 * This method returns an array of length 0 if the class declares no classes or interfaces as members,
	 * or if this Class object represents a primitive type, an array class, or void.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.DECLARED as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @return the array of Class objects representing all the declared members of this class
	 * @throw SecurityException - if access to the information is denied.
	 * @see SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Class[] getDeclaredClasses() throws SecurityException {
		return cls.getDeclaredClasses();
	}

	/**
	 * Returns an array of Field objects reflecting all the fields declared by the class or interface
	 * represented by this Class object.
	 * This includes public, protected, default (package) access, and private fields, but excludes inherited fields.
	 * The elements in the array returned are not sorted and are not in any particular order.
	 * This method returns an array of length 0 if the class or interface declares no fields,
	 * or if this Class object represents a primitive type, an array class, or void.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.DECLARED as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @return the array of Field objects representing all the declared fields of this class
	 * @throw SecurityException - if access to the information is denied.
	 * @see Field, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Field[] getDeclaredFields() throws SecurityException {
		return cls.getDeclaredFields();
	}

	/**
	 * Returns an array of Method objects reflecting all the methods
	 * declared by the class or interface represented by this Class object.
	 * This includes public, protected, default (package) access, and private methods, but excludes inherited methods.
	 * The elements in the array returned are not sorted and are not in any particular order.
	 * This method returns an array of length 0 if the class or interface declares no methods,
	 * or if this Class object represents a primitive type, an array class, or void.
	 * The class initialization method <clinit> is not included in the returned array.
	 * If the class declares multiple public member methods with the same parameter types,
	 * they are all included in the returned array.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.DECLARED as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @return the array of Method objects representing all the declared methods of this class
	 * @throw SecurityException - if access to the information is denied.
	 * @see Method, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Method[] getDeclaredMethods() throws SecurityException {
		return cls.getDeclaredMethods();
	}

	/**
	 * Returns an array of Constructor objects reflecting all the constructors
	 * declared by the class represented by this Class object.
	 * These are public, protected, default (package) access, and private constructors.
	 * The elements in the array returned are not sorted and are not in any particular order.
	 * If the class has a default constructor, it is included in the returned array.
	 * This method returns an array of length 0
	 * if this Class object represents an interface, a primitive type, an array class, or void.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.DECLARED as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @return the array of Method objects representing all the declared constructors of this class
	 * @throw SecurityException - if access to the information is denied.
	 * @see Constructor, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Constructor[] getDeclaredConstructors() throws SecurityException {
		return cls.getDeclaredConstructors();
	}

	/**
	 * Returns a Field object that reflects the specified declared field
	 * of the class or interface represented by this Class object.
	 * The name parameter is a String that specifies the simple name of the desired field.
	 * Note that this method will not reflect the length field of an array class.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.DECLARED as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @param name - the name of the field
	 * @return the Field object for the specified field in this class
	 * @throw NoSuchFieldException - if a field with the specified name is not found.
	 * @throw SecurityException - if access to the information is denied.
	 * @see Field, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Field getDeclaredField(String name) throws NoSuchFieldException, SecurityException {
		return cls.getDeclaredField(name);
	}

	/**
	 * Returns a Method object that reflects the specified declared method
	 * of the class or interface represented by this Class object.
	 * The name parameter is a String that specifies the simple name
	 * of the desired method, and the parameterTypes parameter is an array of Class objects
	 * that identify the method's formal parameter types, in declared order.
	 * If more than one method with the same parameter types is declared in a class,
	 * and one of these methods has a return type that is more specific than any of the others, that method is returned;
	 * otherwise one of the methods is chosen arbitrarily.
	 * If the name is "<init>"or "<clinit>" a NoSuchMethodException is raised.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.DECLARED as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @param name - the name of the method
	 * @param parameterTypes - the parameter array
	 * @return the Method object for the method of this class matching the specified name and parameters
	 * @throw NoSuchMethodException - if a matching method is not found.
	 * @throw SecurityException - if access to the information is denied.
	 * @see Method, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Method getDeclaredMethod(String name, Class[] parameterTypes)
		throws NoSuchMethodException, SecurityException {
		return cls.getDeclaredMethod(name, parameterTypes);
	}

	/**
	 * Returns a Constructor object that reflects the specified constructor
	 * of the class or interface represented by this Class object.
	 * The parameterTypes parameter is an array of Class objects
	 * that identify the constructor's formal parameter types, in declared order.
	 *
	 * If there is a security manager, this method first calls the security manager's checkMemberAccess method
	 * with this and Member.DECLARED as its arguments.
	 * If the class is in a package, then this method also calls the security manager's checkPackageAccess method
	 * with the package name as its argument.
	 * Either of these calls could result in a SecurityException.
	 *
	 * @param parameterTypes - the parameter array
	 * @return The Method object for the constructor with the specified parameter list
	 * @throw NoSuchMethodException - if a matching method is not found.
	 * @throw SecurityException - if access to the information is denied.
	 * @see Constructor, SecurityManager.checkMemberAccess(Class, int), SecurityManager.checkPackageAccess(String)
	 */
	public Constructor getDeclaredConstructor(Class[] parameterTypes) throws NoSuchMethodException, SecurityException {
		return cls.getDeclaredConstructor(parameterTypes);
	}

	/**
	 * Finds a resource with a given name.
	 * This method returns null if no resource with this name is found.
	 * The rules for searching resources associated with a given class
	 * are implemented by the defining class loader of the class.
	 * This method delegates the call to its class loader,
	 * after making these changes to the resource name:
	 * if the resource name starts with "/", it is unchanged;
	 * otherwise, the package name is prepended to the resource name after converting "." to "/".
	 * If this object was loaded by the bootstrap loader,
	 * the call is delegated to ClassLoader.getSystemResourceAsStream.
	 *
	 * @param name - name of the desired resource
	 * @return a java.io.InputStream object.
	 *
	 * @see ClassLoader
	 */
	public InputStream getResourceAsStream(String name) {
		return cls.getResourceAsStream(name);
	}

	/**
	 * Finds a resource with a given name.
	 * This method returns null if no resource with this name is found.
	 * The rules for searching resources associated with a given class
	 * are implemented by the defining class loader of the class.
	 *
	 * This method delegates the call to its class loader,
	 * after making these changes to the resource name:
	 * if the resource name starts with "/", it is unchanged;
	 * otherwise, the package name is prepended to the resource name after converting "." to "/".
	 * If this object was loaded by the bootstrap loader,
	 * the call is delegated to ClassLoader.getSystemResource.
	 *
	 * @param name - name of the desired resource
	 * @return a java.net.URL object.
	 *
	 * @see ClassLoader
	 */
	public URL getResource(String name) {
		return cls.getResource(name);
	}

	/**
	 * Returns the ProtectionDomain of this class.
	 * If there is a security manager installed,
	 * this method first calls the security manager's checkPermission method
	 * with a RuntimePermission("getProtectionDomain") permission
	 * to ensure it's ok to get the ProtectionDomain.
	 *
	 * @return the ProtectionDomain of this class
	 * @throw SecurityException - if a security manager exists
	 * and its checkPermission method doesn't allow geting the ProtectionDomain.
	 *
	 * @see ProtectionDomain, SecurityManager.checkPermission(java.security.Permission), RuntimePermission
	 */
	public ProtectionDomain getProtectionDomain() {
		return cls.getProtectionDomain();
	}

	/**
	 * @see reflect.IThing#getType()
	 */
	public Type getType() {
		return IType.TYPE; }
		

}
