package knowledge;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Regression tests for the defects found while documenting this package.
 *
 * <p>Written in this codebase's own {@code testIt()} idiom rather than against a test
 * framework, because the tree has no build file and no test dependency: it compiles with
 * plain {@code javac} and runs with plain {@code java}.
 *
 * <p>No database is involved. A {@link Proxy}-based {@link Connection} records the SQL it
 * is handed, which is what makes the statement-building defects observable at all - they
 * are invisible from outside the class until the generated text is inspected.
 *
 * <pre>
 * javac -d out knowledge/*.java
 * java -cp "out;." knowledge.KnowledgeTest
 * </pre>
 *
 * @author  Matthias Heuer
 * @version 1.0
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:36:43Z
 * digest: 2b15bb93f00ea71f530c4482710ed8f06577d68787da0488586a95fb9c2ca373
 * stale: false
 * -->
 */
public class KnowledgeTest {

	////////////////////////////////////////////////////////////////////////////
	//  Test Infrastructure
	////////////////////////////////////////////////////////////////////////////

	/** Every SQL string handed to the fake connection during the current test. */
	static final List Sql = new ArrayList();

	/** Number of checks that failed during the current run. */
	static int Failures;

	/** Records the outcome of one check, continuing so a run reports every defect at once. */
	static void check(boolean condition, String what) {
		if (condition) { System.out.println("  ok   " + what); return; }
		Failures++;
		System.out.println("  FAIL " + what);
	}

	/** Compares two strings, showing both when they differ. */
	static void checkEquals(String expected, String actual, String what) {
		if (expected.equals(actual)) { System.out.println("  ok   " + what); return; }
		Failures++;
		System.out.println("  FAIL " + what);
		System.out.println("       expected: " + expected);
		System.out.println("       actual  : " + actual);
	}

	/** Returns a proxy for the given interface, so that no JDBC driver is needed. */
	static Object proxy(Class iface, InvocationHandler handler) {
		return Proxy.newProxyInstance(
			KnowledgeTest.class.getClassLoader(), new Class[] { iface }, handler);
	}

	/** Returns a result set reporting no rows and zero for every primitive. */
	static ResultSet emptyResultSet() {
		return (ResultSet) proxy(ResultSet.class, new InvocationHandler() {
			public Object invoke(Object p, Method m, Object[] a) {
				if (m.getReturnType() == boolean.class) return Boolean.FALSE;
				if (m.getReturnType() == int.class) return Integer.valueOf(0);
				if (m.getReturnType() == long.class) return Long.valueOf(0);
				return null; }
		});
	}

	/**
	 * Returns a connection whose statements record their SQL into {@link #Sql}.
	 *
	 * @param failQueries when true every statement throws, to exercise error handling
	 */
	static Connection fakeConnection(final boolean failQueries) {
		final Statement statement = (Statement) proxy(Statement.class, new InvocationHandler() {
			public Object invoke(Object p, Method m, Object[] a) throws SQLException {
				if (a != null && a.length > 0 && a[0] instanceof String) Sql.add(a[0]);
				if (failQueries) throw new SQLException("no such table");
				if (m.getReturnType() == int.class) return Integer.valueOf(1);
				if (m.getReturnType() == boolean.class) return Boolean.FALSE;
				if (m.getReturnType() == ResultSet.class) return emptyResultSet();
				return null; }
		});
		return (Connection) proxy(Connection.class, new InvocationHandler() {
			public Object invoke(Object p, Method m, Object[] a) {
				return "createStatement".equals(m.getName()) ? statement : null; }
		});
	}

	/** A factory that records the key it was asked for instead of reaching a database.
	 *
	 * <!-- docstate
	 * pass: 2
	 * mtime: 2026-09-05T08:36:43Z
	 * digest: 21f984434f4fc5e76a2352779e82d2a3040f6a55ee85a520d15e5066f1fc4bab
	 * stale: false
	 * -->
	 */
	static class RecordingFactory extends DBObjectFactory {

		/** The key passed to the most recent {@link #getObject(IPrimaryKey)} call. */
		public IPrimaryKey LastKey;

		/** Creates a recording factory over the given prototype. */
		public RecordingFactory(Connection conn, PersistAble prototype) { super(conn, prototype); }

		/** Records the key and returns null, leaving the caller's own logic under test. */
		public PersistAble getObject(IPrimaryKey Key) { LastKey = Key; return null; }
	}

	////////////////////////////////////////////////////////////////////////////
	//  Tests
	////////////////////////////////////////////////////////////////////////////

	/** An INSERT must line its values up with the columns it names. */
	static void insertAlignsValuesWithColumns() throws SQLException {
		Sql.clear();
		DBObjectFactory f =
			new DBObjectFactory(fakeConnection(false), new MetricAttribute(0, 0, 0, 0.0));
		f.insertObject(new MetricAttribute(11, 22, 33, 4.5));
		checkEquals(
			"INSERT INTO MetricAttribute(StatusID,SubjectID,TypeID,Value) VALUES (33,22,11,4.5)",
			(String) Sql.get(0), "insertObject aligns values with columns");
	}

	/** An UPDATE must not emit WHERE twice, since Condition() already carries one. */
	static void updateEmitsOneWhere() throws SQLException {
		Sql.clear();
		DBObjectFactory f =
			new DBObjectFactory(fakeConnection(false), new MetricAttribute(0, 0, 0, 0.0));
		f.updateObject(new MetricAttribute(11, 22, 33, 4.5));
		String sql = (String) Sql.get(0);
		check(sql.indexOf("WHERE") == sql.lastIndexOf("WHERE"), "updateObject emits one WHERE");
	}

	/** A string value must be quoted, and an embedded quote doubled, not passed through. */
	static void stringValuesAreQuotedAndEscaped() throws SQLException {
		Sql.clear();
		DBObjectFactory f =
			new DBObjectFactory(fakeConnection(false), new StringAttribute(0, 0, 0, ""));
		f.insertObject(new StringAttribute(11, 22, 33, "O'Brien"));
		checkEquals(
			"INSERT INTO StringAttribute(StatusID,SubjectID,TypeID,Value) VALUES (33,22,11,'O''Brien')",
			(String) Sql.get(0), "insertObject quotes and escapes a string value");
	}

	/** A quote in a value must not be able to terminate the literal it sits in. */
	static void literalNeutralisesInjection() {
		checkEquals("''' OR ''1''=''1'", DBObjectFactory.literal("' OR '1'='1"),
			"literal() neutralises a quote-injection payload");
		checkEquals("42", DBObjectFactory.literal(Integer.valueOf(42)),
			"literal() leaves a number unquoted");
		checkEquals("NULL", DBObjectFactory.literal(null), "literal() renders null as NULL");
	}

	/** getType must resolve through the type factory, keyed by the type ID. */
	static void getTypeResolvesByTypeId() throws SQLException {
		Connection c = fakeConnection(false);
		RecordingFactory types = new RecordingFactory(c, new Type(0));
		DBObjectFactory.FactoryType = types;
		DBObjectFactory.FactoryStatus = new RecordingFactory(c, new Status(0));

		new Objekt(1, "n", "d", 7, 9).getType();
		check(types.LastKey != null && ((IdKey) types.LastKey).getID() == 7,
			"Objekt.getType asks the type factory for the type ID");

		types.LastKey = null;
		new MetricAttribute(7, 5, 9, 1.0).getType();
		check(types.LastKey != null && ((IdKey) types.LastKey).getID() == 7,
			"BasicAttribute.getType asks the type factory for the type ID");
	}

	/** getStatus must still resolve through the status factory, keyed by the status ID. */
	static void getStatusResolvesByStatusId() throws SQLException {
		Connection c = fakeConnection(false);
		RecordingFactory statuses = new RecordingFactory(c, new Status(0));
		DBObjectFactory.FactoryStatus = statuses;
		new Objekt(1, "n", "d", 7, 9).getStatus();
		check(statuses.LastKey != null && ((IdKey) statuses.LastKey).getID() == 9,
			"Objekt.getStatus asks the status factory for the status ID");
	}

	/** The calculator runs when one is set, and the absence of one is reported. */
	static void assertIsDirtyUsesTheCalculatorWhenPresent() {
		final boolean[] ran = { false };
		CachedValue withCalculator = new CachedValue();
		withCalculator.calculator = new Runnable() { public void run() { ran[0] = true; } };
		withCalculator.setDirty(false);
		withCalculator.assertIsDirty(true);
		check(ran[0], "assertIsDirty runs the calculator when one is set");

		CachedValue without = new CachedValue();
		without.setDirty(false);
		try {
			without.assertIsDirty(true);
			check(false, "assertIsDirty reports a missing calculator as IllegalArgumentException");
		} catch (IllegalArgumentException expected) {
			check(true, "assertIsDirty reports a missing calculator as IllegalArgumentException");
		} catch (NullPointerException wrong) {
			check(false, "assertIsDirty reports a missing calculator as IllegalArgumentException");
		}
	}

	/** A failing Max(ID) query must surface, not leave MaxID silently at zero. */
	static void cachedFactoryReportsAFailingMaxQuery() {
		try {
			new DbCachedFactory(fakeConnection(true), new Status(0));
			check(false, "DbCachedFactory propagates a failing Max(ID) query");
		} catch (SQLException expected) {
			check(true, "DbCachedFactory propagates a failing Max(ID) query");
		}
	}

	/** The finalizer that never wrote anything back is gone rather than misleading. */
	static void statusHasNoFinalizer() {
		boolean declared = true;
		try { Status.class.getDeclaredMethod("finalize", new Class[0]); }
		catch (NoSuchMethodException gone) { declared = false; }
		check(!declared, "Status declares no finalize()");
	}

	////////////////////////////////////////////////////////////////////////////
	//  Test Methods
	////////////////////////////////////////////////////////////////////////////

	/** Main Method: Tests all Methods of this Class */
	public static void main(String[] args) throws SQLException { testIt(args); }

	/**
	 * Tests all Methods of this Class, and exits non-zero when any check failed.
	 *
	 * @param args ignored
	 * @throws SQLException never, in practice: the fake connection does not fail unasked
	 */
	public static void testIt(String[] args) throws SQLException {
		Failures = 0;
		insertAlignsValuesWithColumns();
		updateEmitsOneWhere();
		stringValuesAreQuotedAndEscaped();
		literalNeutralisesInjection();
		getTypeResolvesByTypeId();
		getStatusResolvesByStatusId();
		assertIsDirtyUsesTheCalculatorWhenPresent();
		cachedFactoryReportsAFailingMaxQuery();
		statusHasNoFinalizer();
		System.out.println(Failures == 0
			? "knowledge: all checks passed"
			: "knowledge: " + Failures + " check(s) FAILED");
		if (Failures != 0) System.exit(1);
	}

}
