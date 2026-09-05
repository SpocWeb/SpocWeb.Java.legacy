package tools.threads;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Regression tests for the two concurrency defects found in {@link TimeOuter}.
 *
 * <p>Written in this codebase's own {@code testIt()} idiom, like
 * {@code knowledge.KnowledgeTest}, because the tree has no build file and no test
 * dependency: it compiles with plain {@code javac} and runs with plain {@code java}.
 *
 * <p><b>What the publication tests can and cannot show.</b> The unsafe-publication defect
 * was a data race, and a race cannot be reproduced on demand: a test that starts a Thread
 * from inside a Constructor will usually observe fully-written Fields anyway. So the checks
 * here pin the two structural properties whose absence made the race possible - that no
 * Constructor starts the monitoring Thread, and that the Fields it reads are final - rather
 * than pretending to observe the race itself.
 *
 * <pre>
 * javac -d out tools/threads/*.java
 * java -cp "out;." tools.threads.TimeOuterTest
 * </pre>
 *
 * @author  Matthias Heuer
 * @version 1.0
 * @see TimeOuter the watchdog under test
 * <!-- docstate
 * pass: 2
 * mtime: 2026-09-05T08:50:35Z
 * digest: 10449a95044a6b959b8becc2850a1ae38ff3f0d4c06f0937bfe2f3414b534136
 * stale: false
 * -->
 */
public class TimeOuterTest {

	////////////////////////////////////////////////////////////////////////////
	//  Test Infrastructure
	////////////////////////////////////////////////////////////////////////////

	/** Number of checks that failed during the current run. */
	static int Failures;

	/** Records the outcome of one check, continuing so a run reports every defect at once. */
	static void check(boolean condition, String what) {
		if (condition) { System.out.println("  ok   " + what); return; }
		Failures++;
		System.out.println("  FAIL " + what);
	}

	/** Records the outcome of one check, showing the counted value when it fails. */
	static void checkCount(int expected, int actual, String what) {
		if (expected == actual) { System.out.println("  ok   " + what); return; }
		Failures++;
		System.out.println("  FAIL " + what + " (expected " + expected + ", counted " + actual + ")");
	}

	/**
	 * A Thread that stays alive for a fixed span and counts the Interruptions it receives.
	 *
	 * <p>It sleeps in short slices rather than one long one, so that catching an Interruption
	 * costs it only the current slice and it remains alive to be interrupted again. That is
	 * what makes a repeating Watchdog distinguishable from a one-shot one.
	 * <!-- docstate
	 * pass: 2
	 * mtime: 2026-09-05T08:50:35Z
	 * digest: 7655cce26645c9660f13d65be9526b0045f9840d83c18a5a7d996f2f01fbcd3a
	 * stale: false
	 * -->
	 */
	static class Victim implements Runnable {

		/** How long this Thread stays alive, in Milliseconds. */
		private final long lifetime;

		/** Interruptions counted so far; written by this Thread, read by the test Thread. */
		volatile int Interrupts;

		/** The Thread running this body, once {@link #start()} has been called. */
		Thread Runner;

		/** Creates a victim that will stay alive for the given span. */
		Victim(long lifetime) { this.lifetime = lifetime; }

		/** Sleeps in slices until the lifetime is up, counting every Interruption. */
		public void run() {
			long end = System.currentTimeMillis() + lifetime;
			while (System.currentTimeMillis() < end) {
				try { Thread.sleep(25); }
				catch (InterruptedException counted) { Interrupts++; }
			}
		}

		/** Starts this victim's Thread and returns it for chaining. */
		Victim start() { Runner = new Thread(this); Runner.start(); return this; }

		/** Waits for this victim's Thread to finish its lifetime. */
		void join() {
			try { Runner.join(); } catch (InterruptedException x) { throw new IllegalStateException(x); }
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//  Tests
	////////////////////////////////////////////////////////////////////////////

	/**
	 * The documented contract is one interruption after the timeout, not one per timeout.
	 *
	 * <p>The victim outlives its timeout six times over, so a repeating watchdog is counted
	 * as several interruptions and a one-shot one as exactly one.
	 */
	static void interruptsExactlyOnce() {
		Victim victim = new Victim(900).start();
		TimeOuter watchdog = TimeOuter.monitor(victim.Runner, 150);
		victim.join();
		watchdog.stop();
		checkCount(1, victim.Interrupts, "the monitored Thread is interrupted exactly once");
	}

	/**
	 * Clearing {@link TimeOuter#doInterrupt} before the timeout elapses cancels it.
	 */
	static void clearingDoInterruptCancels() {
		Victim victim = new Victim(600).start();
		TimeOuter watchdog = TimeOuter.monitor(victim.Runner, 300);
		watchdog.doInterrupt = false;
		victim.join();
		watchdog.stop();
		checkCount(0, victim.Interrupts, "clearing doInterrupt before the timeout cancels it");
	}

	/**
	 * Constructing a TimeOuter must not start anything.
	 *
	 * <p>This is the observable half of the unsafe-publication fix: while a Constructor
	 * starts the monitoring Thread, that Thread reads a half-built object, and no amount of
	 * field marking can prevent it. The victim outlives the timeout six times over, so a
	 * Constructor that starts monitoring is caught by the Interruption count.
	 */
	static void constructorStartsNothing() throws Exception {
		Constructor constructor =
			TimeOuter.class.getDeclaredConstructor(new Class[] { Thread.class, long.class });
		constructor.setAccessible(true);

		Victim victim = new Victim(600).start();
		constructor.newInstance(new Object[] { victim.Runner, Long.valueOf(100) });
		victim.join();
		checkCount(0, victim.Interrupts, "the Constructor alone starts no monitoring Thread");
	}

	/**
	 * The fields the monitoring Thread reads are final.
	 *
	 * <p>Final fields are what make the finished object safe to hand to another Thread at
	 * all. They are necessary rather than sufficient - they guarantee nothing about an
	 * object published before its Constructor returns, which is why
	 * {@link #constructorStartsNothing()} exists alongside this check.
	 */
	static void monitoringStateIsFinal() throws Exception {
		String[] names = { "sleepTime", "taskThread", "monitoredThread" };
		for (int i = 0; i < names.length; i++) {
			Field field = TimeOuter.class.getDeclaredField(names[i]);
			check(Modifier.isFinal(field.getModifiers()), names[i] + " is final");
		}
	}

	////////////////////////////////////////////////////////////////////////////
	//  Test Methods
	////////////////////////////////////////////////////////////////////////////

	/** Main Method: Tests all Methods of this Class */
	public static void main(String[] args) throws Exception { testIt(args); }

	/**
	 * Tests all Methods of this Class, and exits non-zero when any check failed.
	 *
	 * @param args ignored
	 * @throws Exception when a reflective lookup fails, which is itself a failure
	 */
	public static void testIt(String[] args) throws Exception {
		Failures = 0;
		interruptsExactlyOnce();
		clearingDoInterruptCancels();
		constructorStartsNothing();
		monitoringStateIsFinal();
		System.out.println(Failures == 0
			? "tools.threads: all checks passed"
			: "tools.threads: " + Failures + " check(s) FAILED");
		if (Failures != 0) System.exit(1);
	}

}
