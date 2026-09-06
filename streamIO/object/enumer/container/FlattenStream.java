package streamIO.object.enumer.container;

import graphs.KeyValuePair;

import java.security.InvalidAlgorithmParameterException;

import streamIO.IIStreamIn;
import streamIO.copy.monoid.Association;
import streamIO.object.AFilterIn;
import streamIO.object.IStreamIn;

/** Filter to use for Column Streams to flatten the streamIO of Pairs coming from
  * @see JoinStreamByFields
  * into a streamIO of Relations consisting of Columns like those coming from
  * @see RecordSet .
  *
  * Fields can be selected from both Results and renamed.
  *
  * If no Fields are given for the second Element,
  * ALL Fields of the second Relation are added to the first one.
  *
  * If no Fields are given for the first  Element,
  * the original fist Element is being used.
  * The first Elements of the Pairs are then modified in Place,
  * so they should be copied, when they come from JoinStreamByFields etc.
  * <!-- docstate
  * tags: [code/container, code/hash_table, code/container_iteration]
  * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
  * facets: {layer: utility, status: legacy, complexity: high}
  * digest: 695eebcc3f128b59195d4cb32f52b9e17c719c06cfc93f5dee1cbdfc0de99740
  * stale: false
  * -->
  */
public class FlattenStream
extends AFilterIn {

	/** List of original Field Names of the key */
	final Object[] oldFieldsKey;

	/** List of new Field Names of the key */
	final Object[] newFieldsKey;

	/** List of original Field Names of the Value */
	final Object[] oldFieldsVal;

	/** List of new Field Names of the Value */
	final Object[] newFieldsVal;

	/** Initializing Constructor,
	  * adds all Items of the second Relation to the first Relation in Place. */
	public FlattenStream(final IIStreamIn _in) {
		this(null, null, null, null, _in); }

	/** Initializing Constructor
	  * adds the given Fields from the first and Second Relation to a new Relation,
	  * if KeyFields is null this is done in Place
	  * if ValFields is null, all Fields are added. */
	public FlattenStream(final IIStreamIn _in, final Object[] _keyFields, final Object[] _valFields) {
		this(_keyFields, _keyFields, _valFields, _valFields, _in); }

	/** Initializing Constructor
	  * adds the given Fields with the new Names
	  * from the first and Second Relation to a new Relation,
	  * if KeyFields is null this is done in Place
	  * if ValFields is null, all Fields are added. */
	protected FlattenStream(
		Object[] KeyFields_, Object[] newKeyFields_,
		Object[] ValFields_, Object[] newValFields_, IIStreamIn In) {
		super(In);
		if (newKeyFields_ == null)
			newKeyFields_ =  KeyFields_;
		if (newValFields_ == null)
			newValFields_ =  ValFields_;
		newFieldsKey = newKeyFields_;
		newFieldsVal = newValFields_;
		oldFieldsKey = KeyFields_;
		oldFieldsVal = ValFields_; }

	/** Initializing Constructor	*/
	public FlattenStream(IIStreamIn In,
		Object[] KeyFields_, Object[] newKeyFields_,
		Object[] ValFields_, Object[] newValFields_)
		throws InvalidAlgorithmParameterException {
		this(KeyFields_, newKeyFields_, ValFields_, newValFields_, In);
		if (KeyFields_ != null)
			if (KeyFields_.length != newKeyFields_.length)
				throw new InvalidAlgorithmParameterException("Number of Elements must be identical! #KeyFields = " +  KeyFields_.length + "  #newKeyFields = " + newKeyFields_.length);
		if (ValFields_ != null)
			if (ValFields_.length != newValFields_.length)
				throw new InvalidAlgorithmParameterException("Number of Elements must be identical! #ValFields = " +  ValFields_.length + "  #newValFields = " + newValFields_.length);
	}

	/** Adds all Items from the second Container to the first Container in Place
	  * This should only be used with unique Joins or copied Joins,
	  * because the original Relations are modified.
	  *
	  * The Field Names are unchanged,
	  * although the Elements of the Second Container
	  * could be selected AND renamed.
	  */
	protected Object nextItemInternal() {
		final KeyValuePair p = (KeyValuePair) in.nextItem();
		if  (p  == IIStreamIn.EOI)
			return IIStreamIn.EOI;
		Relation o1 = (Relation) p.key;
		Relation o2 = (Relation) p.val;
		//process the Key Fields
		Relation ret = o1;
		if (newFieldsKey == null) { //add all Fields with their original Column Name
//			ret = (Relation) ret.copy();
/*			Object Item; //create a Copy by adding all Fields
			StreamIn Fields = (StreamIn) o1.Iterator();
			try { Fields.reset();
			} catch (NoSuchMethodException x) { throw new NoSuchMethodError(x.toString()); }
			while ((IStreamIn.EOI != (Item = Fields.nextItem())) || Fields.isValid()) {
				 ret.addAt((Association) Item); }
*/		} else { //add only the Chosen Fields
			ret = (Relation) ret.newInstance(); //new Relation();
			int i = oldFieldsKey.length;
			while (--i >= 0) {
				ret.addItem(newFieldsKey[i], o1.getAt(oldFieldsKey[i])); }
		}	//process the Value Fields
		if (newFieldsVal == null) { //add all Fields with their original Column Name
			IStreamIn Fields = (IStreamIn) o2.Iterator();
			Fields.reSet();
			for (Object item; (EOI != (item = Fields.nextItem())) || Fields.isValid();) 
				 ret.addItem((Association) item); 
		} else { //add only the Chosen Fields
			for(int i = oldFieldsVal.length; --i >= 0; ) 
				ret.addItem(newFieldsVal[i], o2.getAt(oldFieldsVal[i])); 
		} return currItem = ret; }

}
