package streamIO.object.enumer.container;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import streamIO.IMarkAble;
import streamIO.IReSetAble;
import streamIO.copy.monoid.Association;
import streamIO.exception.OperationNotSupported;
import streamIO.object.CopyStreamIn;
import streamIO.object.IPipe;
import streamIO.object.IStreamIn;
import streamIO.object.enumer.AIndexEnumerator;
import streamIO.object.enumer.IndexEnumerator;

/**
 * Adapter Class to access a RecordSet like a streamIO
 * E.g. to be able to load it into a Container
 * or to perform Joins outside of the DB.
 * The Field Names are chosen as the Keys for the Fields of the Relations returned.
 * TODO: there is a certain optimization possible,
 * because you can reuse the same Relation Object and just swap the Values
 * out of the Associations!!!
 * This is the same Approach as for the Number and PrimeNumber Streams.
 * Just like with other Streams you have to create a Copy first,
 * to be able to further use the Result e.g. by inserting a
 * @see CopyStreamIn
 * Alternatively a new Relation is created anyway on flattening the Cross Product.
 * But also flattening would reuse the first Relation.
 * <!-- docstate
 * tags: [code/container, code/hash_table, code/container_iteration]
 * concepts: [Concrete Storage Containers - Arrays - Hash Tables and Relations]
 * facets: {layer: utility, status: legacy, complexity: high}
 * digest: 95b3753afd3cc7a7891b3daa6428a54706bf4e3765b34bd4eedb4eb97523392a
 * stale: false
 * -->
 */
public class RecordSet
	extends AIndexEnumerator //AStreamIn {
	implements IndexEnumerator {

	////////////////////////////////////////////////////////////////////////////////
	//  static Constants
	////////////////////////////////////////////////////////////////////////////////

	/** Select Statement for selecting all Columns of a Table */
	final static public String SELECT_ALL = "SELECT * FROM ";

	////////////////////////////////////////////////////////////////////////////////
	//  Member Variables
	////////////////////////////////////////////////////////////////////////////////

	/** Reference to the ResultSet */
	protected ResultSet rs;

	/** Reference to the ResultSet MetaData */
	protected String[] FieldNames;

	/** Reference to the current Item */
	protected Relation currItem;

	/** Optimization for reusing the Relation and just exchanging the Values */
	protected Association[] Fields;

	/** Workaround for Forward Only Cursors */
	protected boolean atEnd = false;

	////////////////////////////////////////////////////////////////////////////////
	//  Constructors
	////////////////////////////////////////////////////////////////////////////////

	/** Creates a new ResultSet */
	public RecordSet(Connection conn, String strSelect) throws SQLException {
		this(conn.createStatement().executeQuery(strSelect)); } //never reuse Statements!

	/** Creates a new ResultSet */
	public RecordSet(final ResultSet _rs) throws SQLException {
		super(null);
		this.rs = _rs;
		ResultSetMetaData mRsMd;
		mRsMd = _rs.getMetaData();
		int numCols = mRsMd.getColumnCount();
		FieldNames = new  String[numCols];
		Fields = new Association[numCols];
		currItem = new Relation(); //Optimization from currRow()
		Association tmp;
		while (--numCols >= 0) {
			tmp = Fields[numCols] = new Association();
			tmp.key = FieldNames[numCols] = mRsMd.getTableName(numCols + 1) + '.' + mRsMd.getColumnName(numCols + 1);
			currItem.addItem(tmp);
		}
	}
	////////////////////////////////////////////////////////////////////////////////
	//  Methods
	////////////////////////////////////////////////////////////////////////////////

	/**
	 * Reads the ResultSet's current Row into the reused {@link #currItem} Relation.
	 * @return a single Record from the ResultSet read into a Relation
	 * A fundamental Optimization here is to reuse the same Relation
	 * and thus save Creation, Construction and Destruction of the Relation Objects.
	 */
	public Relation currRow() { //throws SQLException {
//			Relation ret = new Relation();
		int i = FieldNames.length;
		while (--i >= 0) {
//			if (Log.l) ("Current Field: " + i + ":" + FieldNames[i]); //just logging...
//				ret.addAt(FieldNames[i], mRS.getObject(i+1)); //
			try { Fields[i].val = rs.getObject(i + 1); //
			} catch (SQLException x) {
				if (x.getSQLState() != null)
					throw new OperationNotSupported("currRow()", x);
				Fields[i].val = null; }
		} return currItem; } // = ret; }

	/** Returns the Record last read by {@link #currRow()} or {@link #nextItem()}.
	 * @return the current Record from the ResultSet loaded into a Relation */
	public Object currItem() {
		return currItem;
	}

	/** Advances the underlying ResultSet and reads the new current Row.
	 * @return a single new Record from the ResultSet loaded into a Relation */
	public Object nextItem() {
		try { return nextRow();
		} catch (final SQLException x) { 
			throw new OperationNotSupported("nextItem()", x); 
		}
	}

	/** Approximates the total Row Count from the current Cursor Position.
	  * @return the total Number of Objects in this Enumerator / Container
	  * For Random Access Stores this is definitely limited and can thus be returned.
	  * Unfortunately ResultSet has no Methods to determine the Number of Rows!
	  */
	public int getInt() {
		return atEnd ? curr : curr+1; }

	/** Closes this RecordSet and frees it's Resources */
	public void close() throws SQLException {
		rs.close();
	}

	/** Advances, reads the Row, then moves the Cursor back one Row.
	 * @return the next Item without moving to it. */
	public Object peekItem() { //throws NoSuchMethodException {
		Object ret = nextItem();
		try { rs.relative(-1);
		} catch (SQLException x) { 
			throw new OperationNotSupported("peekItem()", x); }
		return ret;
	}
	//Marking and Resetting a Stream (for re-Processing, if supported)

	/**
	 * Resets the Iterator to the last marked Position,
	 * done automatically on Instantiation
	 * By Default the Start of the Iterator is marked on Instantiation
	 */
	public IReSetAble reSet() { //throws SQLException {
		try { rs.absolute(mark); 
		} catch (final SQLException x) { 
			throw new RuntimeException(x.toString()); 
		}
		return this; 
	}

	/**
	 * Resets the Iterator to the given Position
	 * counted from the last marked Position.
	 * @return the Number of Positions actually skipped
	 */
	public long reSet(final long Position) { //throws	NoSuchMethodException {
		if (Position == 0) return 0;
		try {
			rs.absolute(mark + (int)Position);
			return rs.getRow() - mark;
		} catch (final SQLException x) { 
			throw new OperationNotSupported("nextItem()", x); 
		}
	}

	/**
	 *Marks the current position in this Iterator.
	 * A subsequent call to the reset method repositions this Iterator
	 * at the last marked position.
	 * The readlimit arguments tells this input stream to allow that many Items
	 * to be read before the mark position gets invalidated.
	 * This is to limit the Blocking of System Ressources
	 */
	public IMarkAble mark(final long ReadLimit) { //throws	NoSuchMethodException {
		try {
			mark = rs.getRow();
		} catch (final SQLException x) { 
			throw new OperationNotSupported("mark()", x); 
		}
		return this;
	}

	/**
	 * Skips over and discards n Items from this Iterator.
	 * @return the actual number of bytes skipped.
	 * This dumb Implementation just reads all Elements and discards them.
	 */
	public long jump(long Position) {
		try {
			int Start = rs.getRow(); //current Row
			rs.relative((int)Position); //true, when not beyond the ResultSet.
			return rs.getRow() - Start;
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
	}

	/**
	 * Moves the underlying ResultSet's Cursor to its last Row and reads it.
	 * @return and moves to the last (Root) Object of this one.
	 * This should be used with Care, because it could result in Blocking
	 * or infinite Loops with infinite Streams.
	 */
	public Object lastItem() {
		try {
			rs.last();
			return currRow();
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
	}

	/** Moves the underlying ResultSet's Cursor to its first Row and reads it.
	 * @return and moves to the first Object of this Container. */
	public Object firstItem() {
		try {
			rs.first();
			return currRow();
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
	}

	/** Moves the underlying ResultSet's Cursor back one Row and reads it.
	 * @return and moves to the first Object of this Container. */
	public Object prevItem() {
		try {
			rs.previous();
			return currRow();
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
	}

	/**
	 * Moves the underlying ResultSet's Cursor to the given absolute Row and reads it.
	 * @return the Object at the given Position in this Enumeration
	 * The Result depends on whether the Iterator is deterministic
	 * and supports these Operations
	 */
	public Object getAt(int Position) { //throws NoSuchMethodException {
		try {
			rs.absolute(Position);
			return currRow();
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
	}

	/** Moves the underlying ResultSet's Cursor back one Row and reads it.
	 * @return the previous Record from the ResultSet read into a Relation */
	public Relation prevRow() throws SQLException {
		if (!rs.previous()) { return null; } //IStreamIn.SOI;
		atEnd = false;
		return currRow();
	}

	/** Advances the underlying ResultSet's Cursor by one Row and reads it.
	 * @return the next Record from the ResultSet read into a Relation */
	public Relation nextRow() throws SQLException {
		if (atEnd = !rs.next()) { return null; } //IStreamIn.EOI;
		return currRow();
	}

////////////////////////////////////////////////////////////////////////////////
//  Optimizations
////////////////////////////////////////////////////////////////////////////////

	/** Reports only whether the Cursor is known to be past the last Row.
	 * @return the minimum Number of Items still available from this ResultSet */
	public long availAble() {
		return atEnd ? -1 : 0;// : 1;
/*		try {
			//return mRS.next() ? 1 : 0;
			//return mRS.isAfterLast() ? 0 : 1; //islast() ? 0 : 1;
			//return mRS.isLast() ? 0 : 1; //
		} catch (SQLException x) {
			throw new OperationNotSupported("available()", x); }
*/	}

	/** Moves to and deletes the given Row from the underlying ResultSet.
	 * @return and removes the given Row from the ResultSet */
	public Object removeAt(int Row) {
		try {
			rs.absolute(Row); Object ret = currRow();
			rs.deleteRow(); return ret;
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
	}

	/** fills the current Row from the given Relation */
	protected void fillCurrRow(Relation New) {
		int i = FieldNames.length;
		while (--i >= 0) { //temporarily updating each Field individually
			try { //possibly ignore Records not updated...
				rs.updateObject(i, New.getAt(FieldNames[i]));
			} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
		}
	}

	/** Moves to the given Row, overwrites its Fields from New, and commits the Update.
	 * @return and replaces the given Row from the ResultSet */
	public Object setAt(int Row, Object New) {
		try {
			rs.absolute(Row);
			fillCurrRow((Relation)New);
			Object ret = currRow();
			rs.updateRow(); return ret; //making it permanent
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
	}

	/** Inserts New as a new Row via the ResultSet's Insert Row; Row is unused.
	 * @return and removes the given Row from the ResultSet */
	public IndexEnumerator addAt(int Row, Object New) {
		try {
			rs.moveToInsertRow();
			fillCurrRow((Relation)New);
			rs.insertRow();
		} catch (SQLException x) { throw new OperationNotSupported("mark()", x); }
		return this;
	}

	/**
	 * This Enumerator has no defined Sort Order, since it follows the ResultSet's own Cursor.
	 * @return the Order in which Elements are returned by the Iterators
	 * when they are added using addItem() and removed using nextItem().
	 */
	public byte getOrder() { return IPipe.ORDER_NONE; }

////////////////////////////////////////////////////////////////////////////////
//	Test Methods
////////////////////////////////////////////////////////////////////////////////

	/**
	 * Retrieve this Record from the DB.
	 *  The primary key must be set for this,
	 *  otherwise an IllegalStateException is thrown
	 *  Returns null when not found.
	 */

/*	public Container getObjects(String SQL) throws SQLException {
	mRS = Conn.createStatement().executeQuery(SQL); //create a new Statement for each Query!
	while (nextRow() != EOI)
		mCnt.add(getObject());
	return mCnt; }

/** Runs {@link #testIt(String[])} as this Class's command-line entry point.
 * @param args unused command-line arguments */
	public static void main(String[] args) throws java.io.IOException {// SQLException {
		testIt(args);
	}

	/** Tests all Methods of this Class */
	public static void testIt(String[] args) { //throws SQLException {
		final String strObject = "Object";
		try {
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver"); //loads the Driver, which registers with the DriverManager
		} catch (ClassNotFoundException x) { }
//		Enumeration Drivers = DriverManager.getDrivers ();
		try {
			Connection conn = DriverManager.getConnection("jdbc:odbc:TypedKnowledge_be");
			Statement StateMt = conn.createStatement();
			ResultSet rs = StateMt.executeQuery(SELECT_ALL + strObject); //create a new Statement for each Query!
			RecordSet RsObjects = new RecordSet(rs);
			RsObjects.Separator = "\n";
//			Relation rel = RsObjects.nextRow();
//			System.out.println(rel);
//			System.out.println(RsObjects);
			Object tmp;
			RecordSet RsStatus = new RecordSet(conn.createStatement().executeQuery(SELECT_ALL + "Status"));
			Array Status = new Array(); //make the Recordset restartable by copying it into an Array.
			CopyStreamIn cp = new CopyStreamIn(RsStatus);

			//Create an Index to speed up the Join:
			//...load the dependent Lookup Table into a Relation
			HashContainer index;
			String[] StatusKeys = {"Status.ID"};
			String[] ObjectKeys = {"Object.StatusID"};
			index = new HashContainer(new KeysEquivalence(StatusKeys));
//			index = new HashContainer(new KeyEquivalence("Status.ID"));
			index.addAt(cp); //index.reset();
			Status.addAt(index); //RsStatus); // .copyAt(RsStatus); //copyAt() resets the Stream!

			IStreamIn join; //relies on the Fact that index is a HashMap indexed by the wanted Key!
			//this Index should preferably be kept in Memory!
//			join = new JoinStreamByFields   (RsObjects, index, "Object.StatusID", "Status.ID", true);
//			join = new JoinStreamByKeyIndex (RsObjects, index, "Object.StatusID", true);
			join = new JoinStreamByKeysIndex(RsObjects, ObjectKeys, index, StatusKeys, true);

			String[] firstFields = {"Object.ID","Object.Name"};
			String[] secndFields = {"Status.ID","Status.Name"};
			FlattenStream flat = new FlattenStream(join, firstFields, secndFields);
			//on joining the copying is done automatically.
			tmp = join.nextItem();
			System.out.println(tmp); 
			flat.Separator = "\n";
//			System.out.println(tmp = join.nextItem());
			String p = flat.toString();
			System.out.println(p);
			//the Join now uses the Relation to join with an exact Match.

			//using an Equivalence Relation that checks for the Value of a certain Key
			//or the Values of certain Keys (when having a combined Key)
			//the Values needn't even be unique, but the Join only selects only the first Item.

			//flattening and selecting the Fields...
		} catch (SQLException x) {
			System.out.println(x.getErrorCode());
			System.out.println(x.getLocalizedMessage());
			System.out.println(x.getMessage());
			System.out.println(x.getSQLState());
			System.out.println(x.toString());
//		} catch (NoSuchMethodException x) {
		}
	}
}
